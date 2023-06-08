//
//  JYMagicCubeView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import "JYMagicCubeView.h"
#import "JYMagicCubeView_v2.h"
#import "JYMagicCubeView_private.h"
#import "JYMCError.h"
#import "JYMCTracker.h"
#import "JYMCElement.h"
#import "JYMCElementView.h"
#import "JYMCStyleParser.h"
#import "JYMCStyleManager.h"
#import "JYMagicCubeMacro.h"
#import "JYMCCustomerFactoryProtocol.h"
#import "UIResponder+JYMagicCube.h"
#import <YYCategories/YYCategories.h>
#import "JYMCDataParser.h"
#import "JYMCLayout.h"
#import "JYMCContainerElement.h"
#import "JYMCLoadingSession.h"
#import "JYMCStateInfo.h"
#import "JYMagicCubeView_js.h"
#import "JYMCElementView+Action.h"
#import "JYMCMetaData.h"
#import "JYMCMetaData+Private.h"

@import JYWKJSEngine;

@interface JYMagicCubeView ()<JYWKJSContextDelegate>

@property (nonatomic, strong, readwrite) NSURL *url;
@property (nonatomic, strong) NSDictionary *data;
@property (nonatomic, copy) NSString *logic;
@property (nonatomic, strong) JYMCLoadingSession *loadingSession;
@property (nonatomic, strong) id<JYMCStyleOperation> currentOperation;
@property (nonatomic, strong) UIView *placeholder;
@property (nonatomic, strong) JYMCElementView *contentView;
@property (nonatomic, strong) JYMCStyleParser *styleParser;
@property (nonatomic, strong) JYMCStyleMetaData *currentStyleMetaData;
@property (nonatomic, strong) JYWKJSContext *jsContext;
@property (nonatomic, weak) JYMCMetaData *metaData;

/// 保存注册的自定义视图工厂
@property (nonatomic, strong) NSMutableDictionary<NSString *,id<JYMCCustomerFactoryProtocol>> *customerFactoryDic;

@end

@implementation JYMagicCubeView

#pragma mark - Public

- (void)loadWithStyleURL:(NSURL *)url data:(NSDictionary *)data {
    [self loadWithStyleURL:url data:data placeholder:nil completion:nil];
}

- (void)loadWithStyleURL:(NSURL *)url data:(NSDictionary *)data placeholder:(UIView *)placeholder {
    [self loadWithStyleURL:url data:data placeholder:placeholder completion:nil];
}

- (void)loadWithStyleURL:(nonnull NSURL *)url data:(nullable NSDictionary *)data completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock {
    [self loadWithStyleURL:url data:data placeholder:nil completion:completionBlock];
}

- (void)loadWithStyleURL:(NSURL *)url
                    data:(NSDictionary *)data
             placeholder:(UIView *)placeholder
              completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock {
    [self _loadWithStyleURLV2:url data:data placeholder:placeholder completion:completionBlock];
}

- (void)loadWithMetaData:(nonnull JYMCMetaData *)metaData
              completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock {
    [self _loadWithMetaData:metaData completion:completionBlock];
}

- (void)loadWithStyle:(NSString *)style data:(NSDictionary *)data completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock {
    self.url = nil;
    
    JYMCLoadingSession* loadingSession = [self createLoadingSession:nil];
    [loadingSession.performanceRecord recodeStart:kJYMCPRLoad];
    
    JYMCStyleMetaData *styleMetaData = [JYMCStyleMetaData new];
    styleMetaData.guid = @"custom_style";
    styleMetaData.supportedVersion = @"1.0.0";
    styleMetaData.content = style;
    
    if (styleMetaData.content.length == 0) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:@"样式内容为空"];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];

        [self loadFinishWithURL:nil error:error block:completionBlock];
        return;
    }
    
    if (![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorInvalidData localizedDescription:@"数据为空"];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRLoad error:error isFinished:YES];
        
        [self loadFinishWithURL:nil error:error block:completionBlock];
        return;
    }
    
    dispatch_main_async_safe(^{
        if ([styleMetaData isEqual:self.currentStyleMetaData]) {
            [self _asynRenderSameStyleWithData:data loadingSession:loadingSession completion:completionBlock];
        } else {
            styleMetaData.rootElement = [self.styleParser parseStyle:styleMetaData error:nil];
            self.currentStyleMetaData = styleMetaData;
            [self _ayncRenderWithStyle:styleMetaData data:data loadingSession:loadingSession completion:completionBlock];
        }
    });
}

- (CGFloat)heightForWidth:(CGFloat)width {
    return [self.contentView heightForWidth:width];
}

+ (CGFloat)heightForWidth:(CGFloat)width metaData:(JYMCMetaData *)metaData {
    if (metaData == nil) {
        return 0;
    }
    
    JYMCStyleMetaData *style = metaData.styleMetaData;
    if (style == nil) {
        return 0;
    }
    
    JYMagicCubeView *mcView = [[JYMagicCubeView alloc] init];
    __block CGFloat height = 0;
    dispatch_main_async_safe(^{
        [mcView renderWithStyleV2:style
                             data:metaData.transData
                   loadingSession:nil
                       completion:nil];
        if (mcView.contentView) {
            height = [mcView heightForWidth:width];
        }
    });
    return height;
}

- (CGFloat)calculateHeightWithURL:(NSURL *)url data:(NSDictionary *)data width:(CGFloat)width
{
    JYMCStyleMetaData *styleMetaData = [[JYMCStyleManager shareManager] styleFromCacheForURL:url];
    if (styleMetaData.content.length == 0) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:@"样式内容为空"];
        [self loadFinishWithURL:nil error:error block:nil];
        return 0;
    }
    
    if (![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorInvalidData localizedDescription:@"数据为空"];
        [self loadFinishWithURL:nil error:error block:nil];
        return 0;
    }
    
    __block CGFloat height = 0;
    dispatch_main_async_safe(^{
        if ([styleMetaData isEqual:self.currentStyleMetaData]) {
            [self renderSameStyleWithData:data
                           loadingSession:nil
                               completion:nil];
        } else {
            self.currentStyleMetaData = styleMetaData;
            [self renderWithStyle:styleMetaData
                             data:data
                              url:url
                   loadingSession:nil
                       completion:nil];
        }
        
        if (self.contentView) {
            height = [self heightForWidth:width];
        }
    });
    return height;
}

- (void)registerCustomerFactory:(id<JYMCCustomerFactoryProtocol>)factory type:(NSString *)type {
    if (!factory || type.length == 0) {
        return;
    }
    
    if ([factory conformsToProtocol:@protocol(JYMCCustomerFactoryProtocol)]) {
        dispatch_main_async_safe(^{
            [self.customerFactoryDic setObject:factory forKey:type];
        });
    }
}

#pragma mark - Life Cycle

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    if (self.contentView) {
        self.contentView.width = self.width;
        JYMCContainerElement* element = (JYMCContainerElement *)self.contentView.element;
        if ([element.matchType isEqualToString:@"all"] && self.height > 0) {
            self.contentView.height = self.height;
        }
        [self.contentView refreshLayout];
    }
    if (self.placeholder) {
        self.placeholder.frame = self.bounds;
    }
}

#pragma mark - Private

/// 加载并渲染样式数据
/// @param style 样式
/// @param data 样式数据
/// @param url 样式链接
/// @param loadingSession 加载上下文
/// @param completionBlock 结束回调
- (void)renderWithStyle:(JYMCStyleMetaData *)style
                   data:(NSDictionary *)data
                    url:(NSURL * _Nullable)url
         loadingSession:(JYMCLoadingSession *)loadingSession
             completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock
{
    self.data = data;
    [loadingSession.performanceRecord recodeStart:kJYMCPRParse];
    [loadingSession.performanceRecord recodeStart:kJYMCPRParseVM];
    JYMCError *error;
    JYMCElement *rootElement = [self.styleParser parseStyle:style error:&error];
    if (error) {
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseVM error:error];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];
        
        [self loadFinishWithURL:url error:error block:completionBlock];
        return;
    }
    [loadingSession.performanceRecord recodeEnd:kJYMCPRParseVM error:nil];
    [loadingSession.performanceRecord recodeStart:kJYMCPRParseView];
    Class viewClass = rootElement.viewClass;
    if (viewClass == NULL || ![viewClass isSubclassOfClass:[JYMCElementView class]]) {
        NSString *desc = [NSString stringWithFormat:@"type 为 '%@' 类型的元素找不到对应 UI 组件", rootElement.type];
        error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:desc];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:error];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];
        
        [self loadFinishWithURL:url error:error block:completionBlock];
        return;
    }
    
    JYMCElementView *contentView = [[viewClass alloc] init];
    @try {
        [contentView applyElement:rootElement];
        
        JYMCStateInfo * info = [JYMCStateInfo stateInfoWithData:data];
        info.customerFactories = self.customerFactoryDic.copy;
        [contentView updateInfo:info];
    } @catch (NSException *exception) {
        error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:exception.reason];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:error];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];
        
        [self loadFinishWithURL:url error:error block:completionBlock];
        return;
    }
    
    for (UIView *subview in self.subviews) {
        [subview removeFromSuperview];
    }
    [self addSubview:contentView];
    self.contentView = contentView;

    [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:nil];
    [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:nil isFinished:YES];

    [self loadFinishWithURL:url error:nil block:completionBlock];
}

- (void)renderSameStyleWithData:(NSDictionary *)data
                 loadingSession:(JYMCLoadingSession *)loadingSession
                     completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock {
    dispatch_main_async_safe(^{
        self.data = data;
        [loadingSession.performanceRecord recodeStart:kJYMCPRParse];
        [loadingSession.performanceRecord recodeStart:kJYMCPRParseVM];
        [loadingSession.performanceRecord recodeStart:kJYMCPRParseView];

        @try {
            JYMCStateInfo * info = [JYMCStateInfo stateInfoWithData:data];
            info.customerFactories = self.customerFactoryDic.copy;
            [self.contentView updateInfo:info];
            [self.contentView refreshLayout];
        } @catch (NSException *exception) {
            NSString *reason = exception.reason;
            JYMCError *error = [JYMCError errorWithCode:JYMCErrorInvalidData localizedDescription:reason];
            [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:error];
            [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:error isFinished:YES];

            [self loadFinishWithURL:self.url error:error block:completionBlock];
            return;
        }
        
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParseView error:nil];
        [loadingSession.performanceRecord recodeEnd:kJYMCPRParse error:nil isFinished:YES];

        [self loadFinishWithURL:self.url error:nil block:completionBlock];
    });
}


- (void)resetViewWithPlaceholder:(UIView * _Nullable)placeholder {
    dispatch_main_async_safe(^{
        if (self.contentView) {
            [self.contentView removeFromSuperview];
            self.contentView = nil;
        }
        if (self.placeholder) {
            [self.placeholder removeFromSuperview];
            self.placeholder = nil;
        }
        if (placeholder && [placeholder isKindOfClass:[UIView class]]) {
            [self addSubview:placeholder];
            self.placeholder = placeholder;
            [self setNeedsLayout];
        }
    });
}

- (void)cancelCurrentOperation {
    @synchronized (self) {
        if (self.currentOperation) {
            [self.currentOperation cancel];
            self.currentOperation = nil;
        }
    }
}

- (void)updateCurrentOperation:(id<JYMCStyleOperation>)operation {
    [self cancelCurrentOperation];
    if (operation) {
        @synchronized (self) {
            self.currentOperation = operation;
        }
    }
}

- (void)loadFinishWithURL:(NSURL *)url error:(NSError *)error block:(JYMagicCubeViewLoadCompletionBlock)block {
    dispatch_main_async_safe(^{
        if (block) {
            block(url, error);
        }
        if (error) {
            if (self.delegate && [self.delegate respondsToSelector:@selector(magicCubeView:didLoadFailWithError:)]) {
                [self.delegate magicCubeView:self didLoadFailWithError:error];
            }
        } else {
            if (self.delegate && [self.delegate respondsToSelector:@selector(magicCubeViewDidLoadSuccess:)]) {
                [self.delegate magicCubeViewDidLoadSuccess:self];
            }
        }
    });
}

#pragma mark - getters

- (JYMCStyleParser *)styleParser {
    if (!_styleParser) {
        _styleParser = [[JYMCStyleParser alloc] init];
    }
    return _styleParser;
}

- (NSMutableDictionary<NSString *,id<JYMCCustomerFactoryProtocol>> *)customerFactoryDic {
    if (!_customerFactoryDic) {
        _customerFactoryDic = [NSMutableDictionary dictionary];
    }
    return _customerFactoryDic;
}

- (JYMCLoadingSession *)createLoadingSession:(NSURL * _Nullable)url {
    JYMCLoadingSession* loadingSession = [JYMCLoadingSession sessionWithStyleURL:url];
    self.loadingSession = loadingSession;
    return loadingSession;
}

#pragma mark - Logic.JYWKJSContextDelegate
- (void)wkjs_setState:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext {
    [self _wkjs_setState:params jsContext:jsContext];
}

- (void)wkjs_callNative:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext {
    [self _wkjs_callNative:params jsContext:jsContext];
}

@end
