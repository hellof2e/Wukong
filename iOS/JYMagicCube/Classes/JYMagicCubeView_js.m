//
//  JYMagicCubeView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import "JYMagicCubeView_js.h"
#import "JYMCError.h"
#import "JYMCTracker.h"
#import "JYMCTrackActionInfo.h"
#import "JYMagicCubeMacro.h"
#import "JYMCLoadingSession.h"
#import "JYMCStyleManager.h"
#import "JYMCElement.h"
#import "JYMagicCubeView_private.h"
#import "JYMCActionInternalContext.h"
#import <YYCategories/YYCategories.h>
#import <MJExtension/MJExtension.h>

@import JYWKJSEngine;
@import JavaScriptCore;

@interface JYMagicCubeView()<JYWKJSContextDelegate>
@property (nonatomic, strong) JYWKJSContext *jsContext;
@property (nonatomic, strong, readonly) JYMCLoadingSession *loadingSession;
@property (nonatomic, strong, readonly) NSDictionary *data;
@property (nonatomic, strong, readonly) JYMCStyleMetaData *currentStyleMetaData;

@end

@implementation JYMagicCubeView(js)
- (void)execPraeviaScriptIfNeed:(JYMCLoadingSession *)session
                           data:(NSDictionary *)originData
                     completion:(void (^)(NSDictionary *data, JYMCError * _Nullable error))completion {
    self.jsContext = nil;
    if (!self.currentStyleMetaData.needExecuteScript) {
        if (completion) {
            completion(originData, nil);
        }
        return;
    }
    
    NSMutableDictionary *properties = [NSMutableDictionary dictionary];
    [properties setValue:originData forKey:@"data"];
    JYMCLoadingSession *loadingSession = self.loadingSession;
    NSString* logic = self.currentStyleMetaData.rootElement.logic;
    __weak typeof(self) weak_self = self;
    dispatch_async(JYWKJSEngine.dispatchQueue, ^{
        __block BOOL isSame = NO;
        dispatch_sync(dispatch_get_main_queue(), ^{
            isSame = [weak_self.loadingSession isEqual:session];
        });
        if (!isSame) {
           if (completion) {
                completion(nil, [JYMCError errorWithCode:JYMCErrorCancelled]);
            }
            return;
        }
        NSError *error = nil;
        NSDictionary* newData = nil;
        JYWKJSContext *jsContext = [JYWKJSEngine.sharedInstance runJS:logic delegate:weak_self error:&error];
        if (jsContext) {
            [jsContext setCustomData:loadingSession];
            [jsContext bindingProperties:properties];
            (void)[jsContext nativeCallJSFunc:@"beforeCreate" arguments:@[] error:&error];
        }

        if (!error) {
            JSValue* result = [jsContext readProperty:@"data"];
            newData = result.isObject ? [result toDictionary] : nil;
        } else {
            [[self class] js_trackException:@"beforeCreate" loadingSession:loadingSession arguments:@[] error:error];
        }
        
        dispatch_main_async_safe(^{
            JYMCError* localError = nil;
            if (![weak_self.loadingSession isEqual:session]) {
                localError = [JYMCError errorWithCode:JYMCErrorCancelled];
            } else if (error) {
                localError = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:error.localizedDescription];
            } else if (nil == newData) {
                localError = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:@"beforeCreate执行后，获取Data为空"];
            }
            weak_self.jsContext = jsContext;
            
            if (completion) {
                completion(newData, localError);
            }
        })
    });
}

- (void)execLifeCycleOnCreatedFuncIfNeed {
    (void)[self.jsContext nativeCallJSFunc:@"onCreated" arguments:@[] error:nil];
}

- (void)execLifeCycleOnUpdatedFuncIfNeed {
    (void)[self.jsContext nativeCallJSFunc:@"onUpdated" arguments:@[] error:nil];
}

- (void)_wkjs_setState:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext {
    JSValue* result = [jsContext readProperty:@"data"];
    NSDictionary* renderData = result.isObject ? [result toDictionary] : nil;
    JYMCLoadingSession *loadingSession = jsContext.customData;
    
    __weak typeof(self) weak_self = self;
    dispatch_main_async_safe(^{
        if ([weak_self.loadingSession isEqual:loadingSession]) {
            [weak_self createLoadingSession:weak_self.url];
            [weak_self.loadingSession.performanceRecord recodeStart:kJYMCPRLoad];
            [weak_self changeData:renderData needRefresh:NO];
            [weak_self.loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:nil];
            [weak_self renderSameStyleWithData:renderData loadingSession:weak_self.loadingSession completion:nil];
            
            /// 刷新后，loadingSession已经发生变化，需要重置，否则后续callNative是（_wkjs_callNative）jsContext.customData 为空
            dispatch_async(JYWKJSEngine.dispatchQueue, ^{
                jsContext.customData = weak_self.loadingSession;
            });
        }
    });
}

- (void)_wkjs_callNative:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext {
    JYMCLoadingSession *loadingSession = jsContext.customData;
    __weak typeof(self) weak_self = self;
    dispatch_main_async_safe(^{
        if ([weak_self.loadingSession isEqual:loadingSession] &&
            [weak_self.delegate respondsToSelector:@selector(magicCubeView:jsCallNative:)]) {
            [weak_self.delegate magicCubeView:weak_self jsCallNative:params];
        }
    });
}

- (void)updateJSContent:(JYWKJSContext *)jsContext {
    dispatch_main_async_safe(^{
        self.jsContext = jsContext;
        self.jsContext.delegate = self;
    });
}

- (void)callJS:(JYMCActionInternalContext *)context arguments:(NSArray *)arguments {
    if (self.currentStyleMetaData.rootElement.logic.length == 0) {
        JYMCError* error = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:@"JS logic is empty or null"];
        [[self class] js_trackException:context.js.methodName loadingSession:self.loadingSession arguments:arguments error:error];
        return;
    }

    if (context.js.methodName.length == 0) {
        JYMCError* error = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:@"JS method is empty or null"];
        [[self class] js_trackException:context.js.methodName loadingSession:self.loadingSession arguments:arguments error:error];
        return;
    }
    
    NSMutableDictionary *properties = [NSMutableDictionary dictionary];
    [properties setValue:self.data forKey:@"data"];
    JYMCLoadingSession *loadingSession = self.loadingSession;
    
    NSString* logic = self.currentStyleMetaData.rootElement.logic;
    __block JYWKJSContext *jsContext = self.jsContext;
    __weak typeof(self) weak_self = self;
    
    dispatch_block_t callJSBlock = ^{
        [jsContext bindingProperties:properties];
        [jsContext setCustomData:loadingSession];
        NSError* error = nil;
        (void)[jsContext nativeCallJSFunc:context.js.methodName arguments:arguments error:&error];
        if (error) {
            [[self class] js_trackException:context.js.methodName loadingSession:self.loadingSession arguments:arguments error:error];
        }
    };
    
    if (nil == jsContext) {
        dispatch_async(JYWKJSEngine.dispatchQueue, ^{
            NSError *error = nil;
            jsContext = [JYWKJSEngine.sharedInstance runJS:logic delegate:weak_self error:&error];
            if (error) {
                [[self class] js_trackException:context.js.methodName loadingSession:self.loadingSession arguments:arguments error:error];
            }
            callJSBlock();
            dispatch_main_async_safe(^{
                if ([weak_self.loadingSession isEqual:loadingSession]) {
                    weak_self.jsContext = jsContext;
                }
            })
        });
    } else {
        dispatch_async(JYWKJSEngine.dispatchQueue, ^{
            callJSBlock();
        });
    }
}

/// 异常埋点
+ (void)js_trackException:(NSString *)methodName
           loadingSession:(JYMCLoadingSession *)loadingSession
                arguments:(NSArray *)arguments
                    error:(NSError *)error
{
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@"methodName"] = methodName;
    dic[@"arguments"] = [arguments mj_JSONString];
    dic[@"code"] = @(error.code).stringValue;
    dic[@"fail_desc"] = error.localizedDescription;
    dic[@"session"] = loadingSession.performanceRecord.sessionId;
    dic[@"styleUrl"] = loadingSession.performanceRecord.styleUrl;
    dic[@"channel"] = loadingSession.performanceRecord.channel;
    
    JYMCTrackActionInfo *trackInfo = [[JYMCTrackActionInfo alloc] init];
    trackInfo.busInfo = dic.copy;
    [JYMCTracker trackForAction:trackInfo];
    
#if DEBUG
    printf("%s %s\n", __FUNCTION__, error.localizedDescription.UTF8String);
#endif
    
}

@end
