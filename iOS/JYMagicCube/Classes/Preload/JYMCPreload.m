//
//  JYMCProload.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/9/27.
//

#import "JYMCPreload.h"
#import "JYMagicCubeView.h"
#import "JYMCStyleManager.h"
#import "JYMCStyleParser.h"
#import "JYMagicCubeView_private.h"
#import "JYMCMetaData.h"
#import "JYMCMetaData+Private.h"
#import "JYMCPreloadCacheManager.h"
#import "JYMCError.h"
#import "JYMCElement.h"
#import "JYMagicCubeMacro.h"

@import JYWKJSEngine;
@import JavaScriptCore;

@implementation JYMCPreloadRequest

+ (instancetype)requestWithStyleURL:(NSURL *)url data:(NSDictionary *)data {
    JYMCPreloadRequest* item = [[JYMCPreloadRequest alloc] init];
    item.styleURL = url;
    item.data = data;
    
    return item;
}

@end


@interface JYMCPreloadResponse ()

@property (nonatomic, strong) JYMCPreloadRequest *request;
@property (nonatomic, strong) JYMCMetaData *metaData;
@property (nonatomic, strong) NSError *error;

@end

@implementation JYMCPreloadResponse

+ (instancetype)responseWithRequest:(JYMCPreloadRequest *)request {
    JYMCPreloadResponse* response = [[JYMCPreloadResponse alloc] init];
    response.request = request;
    return response;
}

@end


@implementation JYMCPreload

#pragma mark -- Public
+ (void)preloadWithStyleURL:(NSURL *)styleURL
                       data:(NSDictionary *)data
                 completion:(void (^)(JYMCPreloadResponse * _Nonnull))completionBlock {
    JYMCPreloadRequest *requst = [JYMCPreloadRequest requestWithStyleURL:styleURL data:data];
    [self preloadWithRequsts:@[requst] completion:^(NSArray<JYMCPreloadResponse *> * _Nonnull responses) {
        if (completionBlock) {
            completionBlock(responses.firstObject);
        }
    }];
}

+ (void)preloadWithRequsts:(NSArray<JYMCPreloadRequest *> *)requests
                completion:(JYMCPreloadCompletionBlock)completionBlock {
    if (requests.count == 0) {
        !completionBlock ?: completionBlock(@[]);
        return;
    }
    
    __block NSInteger countOfLoaded = 0;
    NSUInteger totalLoadCount = requests.count;
    NSMutableArray<JYMCPreloadResponse *>* responses = [[NSMutableArray alloc] initWithCapacity:totalLoadCount];
    
    [requests enumerateObjectsUsingBlock:^(JYMCPreloadRequest * _Nonnull request, NSUInteger idx, BOOL * _Nonnull stop) {
        JYMCPreloadResponse *response = [JYMCPreloadResponse responseWithRequest:request];
        [responses addObject:response];
        
        [self preloadWithRequst:request response:response completion:^(JYMCPreloadResponse *response) {
            
            countOfLoaded ++;
            if (countOfLoaded == totalLoadCount) {
                !completionBlock ?: completionBlock(responses.copy);
            }
        }];
    }];
}

+ (void)removePreloadMetaDataWithStyleURL:(NSURL *)styleURL data:(NSDictionary *)data {
    NSString *md5Key = [JYMCPreloadCacheManager md5KeyWithStyleUrl:styleURL.absoluteString data:data];
    [[JYMCPreloadCacheManager shareCache] clearCacheMetaDataWithKey:md5Key];
}

+ (JYMCMetaData *)fetchPreloadMetaDataWithStyleURL:(NSURL *)styleURL data:(NSDictionary *)data {
    return [[JYMCPreloadCacheManager shareCache] cacheMetaDataWithStyleUrl:styleURL.absoluteString data:data];
}

#pragma mark -- Private
+ (void)preloadWithRequst:(JYMCPreloadRequest *)request
                 response:(JYMCPreloadResponse *)response
               completion:(nullable void (^)(JYMCPreloadResponse * response))completionBlock {
    JYMCError *error = [JYMagicCubeView _checkStyleLoadParams:request.styleURL data:request.data];// 数据校验 与 悟空View渲染保持一致
    if (error) {
        response.error = error;
        dispatch_main_async_safe(^{
            !completionBlock ?: completionBlock(response);
        });
        return;
    }
    NSString *md5Key = [JYMCPreloadCacheManager md5KeyWithStyleUrl:request.styleURL.absoluteString data:request.data];
    JYMCMetaData *metaData = [[JYMCPreloadCacheManager shareCache] cacheMetaDataForKey:md5Key];
    if (metaData) {
        response.metaData = metaData;
        dispatch_main_async_safe(^{
            !completionBlock ?: completionBlock(response);
        });
        return;
    }
    
    [self preloadWithStyleURL:request.styleURL completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nonnull error) {
        if (error || styleMetaData == nil) {
            response.error = error;
            !completionBlock ?: completionBlock(response);
            return;
        }
            
        NSDictionary *originData = request.data.copy;
        [self execPreviaScriptWithStyleMetaData:styleMetaData
                                           data:request.data
                                     completion:^(NSDictionary * _Nonnull newData, JYWKJSContext * _Nullable jsContext, JYMCError * _Nullable error) {
            JYMCMetaData *metaData = nil;
            if (!error) {
                metaData = [JYMCMetaData metaDataWithStyleURL:request.styleURL originData:originData];
                metaData.styleMetaData = styleMetaData;
                metaData.jsContext = jsContext;
                metaData.transData = newData;
                
                if (request.needCache) {
                    [[JYMCPreloadCacheManager shareCache] storeValue:metaData key:md5Key];
                }
            }
            response.error = error;
            response.metaData = metaData;
            !completionBlock ?: completionBlock(response);
        }];
    }];
}

+ (void)preloadWithStyleURL:(NSURL *)styleURL
                 completion:(nullable void (^)(JYMCStyleMetaData * _Nullable styleMetaData, NSError *))completionBlock {
    [[JYMCStyleManager shareManager] loadStyleWithURL:styleURL
                                           completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL) {
        NSError* loacalError = error;
        if (styleMetaData != nil &&  nil == styleMetaData.rootElement) {
            JYMCError* err = nil;
            styleMetaData.rootElement = [JYMCStyleParser2 parseStyle:styleMetaData error:&err];
            loacalError = (NSError *)err;
        }
        if (completionBlock) {
            completionBlock(styleMetaData, loacalError);
        }
    }];
}

+ (void)execPreviaScriptWithStyleMetaData:(JYMCStyleMetaData *)styleMetaData
                                      data:(NSDictionary *)originData
                                completion:(void (^)(NSDictionary *newData, JYWKJSContext * _Nullable jsContext, JYMCError * _Nullable error))completion {
    if (!styleMetaData.needExecuteScript) {
        if (completion) {
            completion(originData, nil, nil);
        }
        return;
    }
    
    NSMutableDictionary *properties = [NSMutableDictionary dictionary];
    [properties setValue:originData forKey:@"data"];
    
    dispatch_async(JYWKJSEngine.dispatchQueue, ^{
        NSError *error = nil;
        NSDictionary* newData = nil;
        JYWKJSContext *jsContext = [JYWKJSEngine.sharedInstance runJS:styleMetaData.rootElement.logic delegate:nil error:&error];
        if (jsContext) {
            [jsContext bindingProperties:properties];
            (void)[jsContext nativeCallJSFunc:@"beforeCreate" arguments:@[] error:&error];
        }

        if (!error) {
            JSValue* result = [jsContext readProperty:@"data"];
            newData = result.isObject ? [result toDictionary] : nil;
        }
        
        dispatch_main_async_safe(^{
            JYMCError* localError = nil;
            if (error) {
                localError = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:error.localizedDescription];
            } else if (nil == newData) {
                localError = [JYMCError errorWithCode:JYMCErrorJSActionException localizedDescription:@"beforeCreate执行后，获取Data为空"];
            }
            
            if (completion) {
                completion(newData, jsContext, localError);
            }
        });
    });
}

@end
