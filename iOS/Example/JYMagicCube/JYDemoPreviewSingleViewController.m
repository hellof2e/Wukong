//
//  JYDemoSampleViewController.m
//  JYMagicCube_Example
//
//  Created by 姜灿明 on 2021/5/4.
//

#import "JYDemoPreviewSingleViewController.h"
#import "UIDevice+JY.h"
#import <JYMagicCube/JYMagicAlert.h>
#import <JYMagicCube/JYMagicCube.h>
#import <YYCategories/YYCategories.h>
#import <Masonry/Masonry.h>

@interface JYDemoPreviewSingleViewController () <JYMagicViewDelegate, UITextFieldDelegate, JYMagicAlertLoaderDelegate, JYMagicAlertLifeCycleDelegate>

@property (nonatomic, strong) JYMagicAlertLoader *magicAlertLoader;
@property (nonatomic, strong) JYMagicCubeView *magicView;
@property (nonatomic, strong) UIButton *alertbutton;
@property (nonatomic, copy) NSDictionary *dslData;
@property (nonatomic, copy) NSURL *dslURL;

@end

@implementation JYDemoPreviewSingleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self addNotifications];
    [self.view addSubview:self.magicView];
    [self commonInit];
}

#pragma mark - Private

- (void)commonInit {
    [self.alertButton addTarget:self action:@selector(alertAction) forControlEvents:UIControlEventTouchUpInside];
}

- (void)alertAction {
    JYMCAlertConfig *config = [[JYMCAlertConfig alloc] init];
    config.alertPostion = JYMCAlertPostionBottom;
    config.alertBgAlpha = 0.6;
    self.magicAlertLoader = [JYMagicAlertLoader loaderAlertWithDelegate:self url:self.dslURL.absoluteString data:self.dslData config:config];
}

- (void)addNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showJSLogOfNotification:) name:@"JYWKShowLogNotification" object:nil];
}

#pragma mark - JYDemoPreviewBaseViewControllerProtocal

- (void)previewLoadSuccessWithStyle:(NSString *)style data:(NSDictionary *)data {
    if (![self.magicView isSameStyle:style] || ![data isEqualToDictionary:self.dslData]) {
        self.dslData = data;
        SEL loadSEL = NSSelectorFromString(@"loadWithStyle:data:completion:");
        NSMethodSignature *sign = [self.magicView methodSignatureForSelector:loadSEL];
        NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:sign];
        invocation.target = self.magicView;
        invocation.selector = loadSEL;
        [invocation setArgument:&style atIndex:2];
        [invocation setArgument:&data atIndex:3];
        JYMagicCubeViewLoadCompletionBlock block = nil;
        [invocation setArgument:&block  atIndex:4];
        [invocation invoke];
    }
}

- (void)previewLoadSuccessWithStyleURL:(NSURL *)styleURL data:(NSDictionary *)data {
    if (![data isEqualToDictionary:self.dslData] || ![styleURL isEqual:self.dslURL]) {
        self.dslData = data;
        self.dslURL = styleURL;
        [self.magicView loadWithStyleURL:styleURL data:data];
    }
}

#pragma mark -- JYMagicAlertLoaderDelegate

/// DSL弹框加载成功
- (void)loadMagicAlertDidSuccess:(JYMagicAlertLoader *)alertLoader alert:(id<JYMagicAlertProtocol>)magicAlert {
    [self logTips:@"弹框加载成功!"];
    if (![magicAlert isShowing]) {
        [magicAlert configLifeCycleDelegate:self];
        [magicAlert show:self animated:YES];
    }
}

/// DSL弹框加载失败
- (void)loadMagicAlert:(JYMagicAlertLoader *)alertLoader failWithError:(NSError *)error {
    [self logError:@"弹框加载失败!"];
}

#pragma mark -- JYMagicAlertLifeCycleDelegate

/// DSL弹框展示成功回调
- (void)magicAlertDidShow:(id<JYMagicAlertProtocol>)magicAlert {
    [self logTips:@"弹框展示成功!"];
}

/// DSL弹框展示失败回调
- (void)magicAlertShowFailure:(id<JYMagicAlertProtocol>)magicAlert error:(NSError *)error {
    [self logError:@"弹框展示失败!"];
}

/// DSL弹框消失
- (void)magicAlertDidDismiss:(id<JYMagicAlertProtocol>)magicAlert {
    [self logTips:@"弹框消失!"];
    
    self.magicAlertLoader = nil;
}

/// DSL弹框js交互事件
- (void)magicAlert:(id<JYMagicAlertProtocol>)magicAlert jsCallNative:(NSDictionary<NSString *,id> *)params {
    [self logTips:[NSString stringWithFormat:@"弹框-jsCallNative: %@", [params jsonPrettyStringEncoded]]];
}

#pragma mark - JYMagicViewDelegate

/// 动态化视图加载渲染成功
- (void)magicCubeViewDidLoadSuccess:(JYMagicCubeView *)mcView {
    [self logTips:@"动态化视图加载渲染成功"];
    
    CGFloat width = [mcView widthInStyle];
    if (width == 0) {
        width = CGRectGetWidth(self.view.bounds);
    }
    CGFloat height = [mcView heightForWidth:width];
    CGFloat X = (CGRectGetWidth(self.view.bounds) - width) / 2.0;
    mcView.frame = CGRectMake(X, CGRectGetMaxY(self.contentView.frame) + 20, width, height);
}

/// 动态化视图加载渲染失败
- (void)magicCubeView:(JYMagicCubeView *)mcView didLoadFailWithError:(NSError *)error {
    [self logError:[NSString stringWithFormat:@"渲染失败: %@", error.localizedDescription]];
}

/// 开始处理交互事件
- (BOOL)magicCubeView:(JYMagicCubeView *)mcView didStartInterceptHandleAction:(JYMCActionContext *)actionContext {
    [self logTips:[NSString stringWithFormat:@"开始处理交互事件: %@", actionContext.url]];
    return NO;
}

- (void)magicCubeView:(JYMagicCubeView *)mcView didActionFinished:(JYMCActionContext *)context {
    if ([context.actionName isEqualToString:JYMCActionCounterName]) {
        if (context.error) {
            // context.error.code = 3001
            [self logTips:context.error.localizedDescription];
        } else {
            [self logTips:[NSString stringWithFormat:@"倒计时结束"]];
        }
    }
}

- (void)magicCubeView:(JYMagicCubeView *)mcView jsCallNative:(NSDictionary<NSString *,id> *)params {
    NSString *logInfo = [NSString stringWithFormat:@"jsCallNative: %@", params];
    [self logTips:logInfo];
}

#pragma mark - Getter

- (JYMagicCubeView *)magicView {
    if (!_magicView) {
        _magicView = [[JYMagicCubeView alloc] init];
        _magicView.delegate = self;
        _magicView.backgroundColor = [UIColor colorWithHexString:@"#D6D7D7"];
    }
    return _magicView;
}

#pragma mark - Notification_Log

- (void)showJSLogOfNotification:(NSNotification *)notification {
    NSDictionary *userInfo = [notification userInfo];
    if (!userInfo || userInfo.count == 0) {
        return;
    }
    
    NSString *logInfo = userInfo[@"logInfo"];
    if (logInfo.length == 0) {
        return;
    }
    
    NSNumber *logType = userInfo[@"logType"];
    if (logType.integerValue == 3) {
        [self logError:logInfo];
    } else {
        [self logTips:logInfo];
    }
}

@end
