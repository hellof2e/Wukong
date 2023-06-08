//
//  JYMagicCubeView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import "JYMagicCubeView.h"
#import "JYMCError.h"
#import "JYMCTracker.h"
#import "JYMCElement.h"
#import "JYMCElementView.h"
#import "JYMCStyleParser.h"
#import "JYMagicCubeMacro.h"
#import "JYMCCustomerFactoryProtocol.h"
#import "UIResponder+JYMagicCube.h"
#import <YYCategories/YYCategories.h>
#import "JYMCDataParser.h"
#import "JYMCLayout.h"
#import "JYMCContainerElement.h"
#import "JYMCLoadingSession.h"
#import "JYMagicCubeView_private.h"
#import "JYMagicCubeView_js.h"
#import "JYMCStyleManager.h"
#import "JYMCStateInfo.h"
#import "JYMCMetaData.h"
#import "JYMCMetaData+Private.h"

@import JYWKJSEngine;

@interface JYMagicCubeView ()
@property (nonatomic, strong) NSURL *url;
@property (nonatomic, strong) NSDictionary *data;
@property (nonatomic, strong, readonly) JYMCLoadingSession *loadingSession;
@property (nonatomic, strong, readonly) id<JYMCStyleOperation> currentOperation;
@property (nonatomic, strong, readonly) UIView *placeholder;
@property (nonatomic, strong) JYMCElementView *contentView;
@property (nonatomic, strong) JYMCStyleMetaData *currentStyleMetaData;
@property (nonatomic, weak) JYMCMetaData *metaData;
/// 保存注册的自定义视图工厂
@property (nonatomic, strong, readonly) NSMutableDictionary<NSString *,id<JYMCCustomerFactoryProtocol>> *customerFactoryDic;

@end

@implementation JYMagicCubeView(V2)

#pragma mark - Public
+ (JYMCError *)_checkStyleLoadParams:(NSURL *)url data:(NSDictionary *)data {
    JYMCError *error = nil;
    if (url == nil) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidURL localizedDescription:@"url 为空"];
    } else if (![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidData localizedDescription:@"data 为空"];
    }
    return error;
}

+ (JYMCError *)_checkMetaData:(JYMCMetaData *)metaData {
    JYMCError *error = nil;
    if (metaData.styleMetaData == nil) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:@"样式内容为空"];
    } else if (![metaData.transData isKindOfClass:[NSDictionary class]] || metaData.transData.count == 0) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidData localizedDescription:@"data 为空"];
    }
    return error;
}

- (void)_loadWithMetaData:(JYMCMetaData *)metaData completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock {
    JYMCLoadingSession* loadingSession = [self createLoadingSession:metaData.styleUrl];
    loadingSession.performanceRecord.isPreloadRecord = YES;
    [loadingSession.performanceRecord recodeStart:kJYMCPRLoad];
    
    [self cancelCurrentOperation];
    
    JYMCError *error = [JYMagicCubeView _checkMetaData:metaData];
    if (error) {
        [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];
        [self loadFinishWithURL:metaData.styleUrl error:error block:completionBlock];
        return;
    }
    
    [loadingSession.performanceRecord recodeStart:kJYMCPRParseVM];
    [loadingSession.performanceRecord recodeEnd:kJYMCPRParseVM error:nil];
    [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:nil];

    @synchronized (self) {
        if ([self.url.absoluteString isEqualToString:metaData.styleUrl.absoluteString] && self.contentView) {
            self.metaData = metaData;
            [self updateJSContent:metaData.jsContext];// 更新jsContent
            [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:nil];
            [self renderSameStyleWithData:metaData.transData loadingSession:loadingSession completion:completionBlock];
            [self execLifeCycleOnUpdatedFuncIfNeed];
            return;
        }
        self.url = metaData.styleUrl;
        [self resetViewWithPlaceholder:nil];
    }
    
    dispatch_main_async_safe(^{
        self.metaData = metaData;
        [self updateJSContent:metaData.jsContext];// 更新jsContent
        self.currentStyleMetaData = metaData.styleMetaData;
        [self renderWithStyleV2:metaData.styleMetaData
                           data:metaData.transData
                 loadingSession:loadingSession
                     completion:completionBlock];
    });
}

- (void)_loadWithStyleURLV2:(NSURL *)url
                       data:(NSDictionary *)data
                placeholder:(UIView *)placeholder
                 completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock
{
    JYMCLoadingSession *loadingSession = [self createLoadingSession:url];
    [loadingSession.performanceRecord recodeStart:kJYMCPRLoad];
    
    [self cancelCurrentOperation];
    
    JYMCError *error = [JYMagicCubeView _checkStyleLoadParams:url data:data];
    if (error) {
        [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];
        [self loadFinishWithURL:url error:error block:completionBlock];
        return;
    }
    
    [loadingSession.performanceRecord recodeStart:kJYMCPRLoadNet];

    @synchronized (self) {
        if ([self.url.absoluteString isEqualToString:url.absoluteString] && self.contentView) {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:nil];
            [self _asynRenderSameStyleWithData:data
                                loadingSession:loadingSession
                                    completion:completionBlock];
            [self execLifeCycleOnUpdatedFuncIfNeed];
            return;
        }
        self.url = url;
        [self resetViewWithPlaceholder:placeholder];
    }
    
    @weakify(self);
    id<JYMCStyleOperation> operation = [[JYMCStyleManager shareManager] loadStyleWithURL:url completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL) {
        @strongify(self);
        if (self == nil) {
            return;
        }
        if (cacheType == JYMCStyleCacheTypeNone) {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRLoadNet error:error];
        }
        
        if (styleMetaData == nil || error) {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];
            [self loadFinishWithURL:url error:error block:completionBlock];
            return;
        }

        self.currentStyleMetaData = styleMetaData;

        [loadingSession.performanceRecord recodeStart:kJYMCPRParseVM];
        [self _parseStyleVMIfNeed:loadingSession style:styleMetaData completion:^(JYMCError * _Nullable error) {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRParseVM error:error];
            [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error];
            if (nil == error) {
                [self _ayncRenderWithStyle:styleMetaData
                                      data:data
                            loadingSession:loadingSession
                                completion:completionBlock];
            } else {
                [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];
            }
        }];

    }];
    
    [self updateCurrentOperation:operation];
}

- (void)_parseStyleVMIfNeed:(JYMCLoadingSession *)session
                      style:(JYMCStyleMetaData *)style
                 completion:(void (^)(JYMCError *_Nullable error))completion {
    
    if (nil != style.rootElement) {
        completion(nil);
        return;
    }
    JYMCError *error = nil;
    style.rootElement = [JYMCStyleParser2 parseStyle:style error:&error];
    if (!error) {
        if (![self.loadingSession isEqual:session]) {
            error = [JYMCError errorWithCode:JYMCErrorCancelled];
        }
    }
    completion(error);
}

- (void)_ayncRenderWithStyle:(JYMCStyleMetaData *)style
                        data:(NSDictionary *)originData
              loadingSession:(JYMCLoadingSession *)loadingSession
                  completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock
{
    [loadingSession.performanceRecord recodeStart:kJYMCPRJS];
    self.data = originData;
    @weakify(self);
    [self execPraeviaScriptIfNeed:loadingSession
                             data:originData
                       completion:^(NSDictionary * _Nonnull newData, JYMCError * _Nonnull error) {
        
        [loadingSession.performanceRecord recodeEnd:kJYMCPRJS error:error];
        @strongify(self);
        if (!error) {
            [self changeData:newData needRefresh:NO];
            [self renderWithStyleV2:style
                               data:newData
                     loadingSession:loadingSession
                         completion:completionBlock];
        } else {
            [self loadFinishWithURL:[NSURL URLWithString:style.url] error:error block:completionBlock];
        }
    }];
}

/// 加载并渲染样式数据
/// @param style 样式
/// @param data 样式数据
/// @param loadingSession 加载上下文
/// @param completionBlock 结束回调
- (void)renderWithStyleV2:(JYMCStyleMetaData *)style
                     data:(NSDictionary *)data
           loadingSession:(JYMCLoadingSession *)loadingSession
               completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock
{
    [loadingSession.performanceRecord recodeStart:kJYMCPRParse];
    [loadingSession.performanceRecord recodeStart:kJYMCPRParseView];
    JYMCError *error = nil;
    Class viewClass = style.rootElement.viewClass;
    if (viewClass == NULL || ![viewClass isSubclassOfClass:[JYMCElementView class]]) {
        NSString *desc = [NSString stringWithFormat:@"type 为 '%@' 类型的元素找不到对应 UI 组件", style.rootElement.type];
        error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:desc];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:error];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];
        
        [self loadFinishWithURL:[NSURL URLWithString:style.url] error:error block:completionBlock];
        return;
    }
    
    self.data = data;
    JYMCElementView *contentView = [[viewClass alloc] init];
    @try {
        [contentView applyElement:style.rootElement];
        JYMCStateInfo * info = [JYMCStateInfo stateInfoWithData:data];
        info.customerFactories = self.customerFactoryDic.copy;
        [contentView updateInfo:info];
    } @catch (NSException *exception) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:exception.reason];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:error];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];
        
        [self loadFinishWithURL:[NSURL URLWithString:style.url] error:error block:completionBlock];
        return;
    }
    
    for (UIView *subview in self.subviews) {
        [subview removeFromSuperview];
    }
    [self addSubview:contentView];
    self.contentView = contentView;
    
    [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:nil];
    [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:nil isFinished:YES];
    
    [self execLifeCycleOnCreatedFuncIfNeed];
    [self loadFinishWithURL:[NSURL URLWithString:style.url] error:nil block:completionBlock];
}

- (void)_asynRenderSameStyleWithData:(NSDictionary *)originData
                      loadingSession:(JYMCLoadingSession *)loadingSession
                          completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock {
    
    self.data = originData;
    [loadingSession.performanceRecord recodeStart:kJYMCPRJS];
    @weakify(self);
    [self execPraeviaScriptIfNeed:loadingSession
                             data:originData
                       completion:^(NSDictionary * _Nonnull newData, JYMCError * _Nonnull error) {
        
        @strongify(self);
        if (!error) {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRJS error:error];
            [self changeData:newData needRefresh:NO];
            [self renderSameStyleWithData:newData loadingSession:loadingSession completion:completionBlock];
        } else {
            [loadingSession.performanceRecord recodeEnd:kJYMCPRJS error:error isFinished:YES];
            [self loadFinishWithURL:[NSURL URLWithString:self.currentStyleMetaData.url] error:error block:completionBlock];
        }
    }];
}

- (void)changeData:(NSDictionary *)newData needRefresh:(BOOL)needRefresh {
    if (![self.data isEqualToDictionary:newData]) {
        self.data = newData;
        self.metaData.transData = self.data;// 确保metaData中数据最新
        if ([self.delegate respondsToSelector:@selector(magicCubeView:syncData:needRefresh:)]) {
            [self.delegate magicCubeView:self syncData:newData needRefresh:needRefresh];
        }
    }
}
@end
