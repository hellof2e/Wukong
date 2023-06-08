//
//  JYMagicAlertViewController.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/15.
//

#import "JYMagicAlertViewController.h"
#import "JYMCAlertConfig.h"
#import "JYMagicAlertDelegate.h"
#import "JYMagicAlertLifeCycleDelegate.h"
#import "JYMCError.h"
#import "JYMagicCubeView+Tool.h"

#import <JYMagicCube/JYMagicCube.h>

@interface JYMagicAlertViewController () <JYMagicViewDelegate>

@property (nonatomic, strong) UIView *blackView;
@property (nonatomic, strong) JYMagicCubeView *magicCubeView;

@property (nonatomic, weak) id<JYMagicAlertDelegate> delegate;
@property (nonatomic, weak) id<JYMagicAlertLifeCycleDelegate> lifeCycleDelegate;

@property (nonatomic, strong) JYMCAlertConfig *alertConfig;
@property (nonatomic, assign) BOOL alertShowing;

@end

@implementation JYMagicAlertViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)commonInitWithUrl:(NSString *)url data:(NSDictionary *)data {
    self.modalPresentationStyle = UIModalPresentationOverFullScreen;
    
    [self.view addSubview:self.blackView];
    self.blackView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:self.alertConfig.alertBgAlpha];
    self.blackView.frame = self.view.bounds;
    
    [self.magicCubeView loadWithStyleURL:[NSURL URLWithString:url] data:data];
}

#pragma mark -- Private
- (BOOL)needAddSafeAreaHeight {
    CGFloat statusHeight = [[UIApplication sharedApplication] statusBarFrame].size.height;
    return statusHeight <= 20;
}

- (void)layoutMagicView {
    CGFloat width = [self.magicCubeView widthInStyle];
    CGFloat height = [self.magicCubeView heightForWidth:width];
    
    CGFloat x = (self.view.frame.size.width - width) / 2.0;
    CGFloat y = (self.view.frame.size.height - height) / 2.0;
    if (self.alertConfig.alertPostion == JYMCAlertPostionCenter) {
        self.magicCubeView.frame = CGRectMake(x, y, width, height);
    } else if (self.alertConfig.alertPostion == JYMCAlertPostionBottom) {
        CGFloat y = self.view.frame.size.height - height + ([self needAddSafeAreaHeight] ? (34 * width / 375.0) : 0);
        self.magicCubeView.frame = CGRectMake(x, y, width, height);
    } else if (self.alertConfig.alertPostion == JYMCAlertPostionTop) {
        self.magicCubeView.frame = CGRectMake(x, 0, width, height);
    }
}

- (void)callbackDidShow {
    if ([self.lifeCycleDelegate respondsToSelector:@selector(magicAlertDidShow:)]) {
        [self.lifeCycleDelegate magicAlertDidShow:self];
    }
}

- (void)blackViewTapAction {
    [self dismissAnimated:YES];
}

#pragma mark -- JYMagicViewDelegate
/// 加载并渲染样式数据成功
/// @param mcView 动态化视图
- (void)magicCubeViewDidLoadSuccess:(JYMagicCubeView *)mcView {
    if ([self.delegate respondsToSelector:@selector(magicAlertDidLoadSuccess:)]) {
        [self.delegate magicAlertDidLoadSuccess:self];
    }
}

/// 加载并渲染样式数据失败
/// @param mcView 动态化视图
/// @param error 错误信息
- (void)magicCubeView:(JYMagicCubeView *)mcView didLoadFailWithError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(magicAlert:didLoadFailWithError:)]) {
        [self.delegate magicAlert:self didLoadFailWithError:error];
    }
}

/// js call native
- (void)magicCubeView:(JYMagicCubeView *)mcView jsCallNative:(NSDictionary<NSString *,id> *)params {
    if ([self.lifeCycleDelegate respondsToSelector:@selector(magicAlert:jsCallNative:)]) {
        [self.lifeCycleDelegate magicAlert:self jsCallNative:params];
    }
}

#pragma mark -- JYMagicAlertProtocol
+ (instancetype)createAlertWithDelegate:(id<JYMagicAlertDelegate>)delegate
                                    url:(NSString *)url
                                   data:(NSDictionary *)data
                                 config:(JYMCAlertConfig *)config {
//    NSAssert(config.alertViewWidth > 0, @"请配置弹框宽度!");
    JYMagicAlertViewController *alertVC = [[JYMagicAlertViewController alloc] init];
    alertVC.delegate = delegate;
    alertVC.alertConfig = config;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [alertVC commonInitWithUrl:url data:data];
    });
    return alertVC;
}

/// 配置弹框生命周期代理
- (void)configLifeCycleDelegate:(id<JYMagicAlertLifeCycleDelegate>)lifeCycleDelegate {
    self.lifeCycleDelegate = lifeCycleDelegate;
}

- (void)show:(UIViewController *)viewcontroller animated:(BOOL)animated {
    NSAssert([viewcontroller isKindOfClass:UIViewController.class], @"传入ViewController非法");
    if (self.alertShowing) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorAlertAlreadyShow localizedDescription:@"当前弹框已弹出!"];
        
        if ([self.lifeCycleDelegate respondsToSelector:@selector(magicAlertShowFailure:error:)]) {
            [self.lifeCycleDelegate magicAlertShowFailure:self error:error];
        }
        return;
    }
    if (viewcontroller.presentingViewController) {
        NSString *msg = [NSString stringWithFormat:@"当前页面已有弹框:%@", NSStringFromClass(viewcontroller.presentingViewController.class)];
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorAlertShowException localizedDescription:msg];
        
        if ([self.lifeCycleDelegate respondsToSelector:@selector(magicAlertShowFailure:error:)]) {
            [self.lifeCycleDelegate magicAlertShowFailure:self error:error];
        }
        return;
    }
    
    self.alertShowing = YES;
    if (![self.view.subviews containsObject:self.magicCubeView]) {
        [self.view addSubview:self.magicCubeView];
    }
    [self layoutMagicView];
    if (animated) self.view.alpha = 0;
    
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:self];
    navigationController.modalTransitionStyle = self.modalTransitionStyle;
    navigationController.modalPresentationStyle = self.modalPresentationStyle;
    
    __weak typeof(self) weak_self = self;
    [viewcontroller presentViewController:navigationController animated:NO completion:^{
        [weak_self callbackDidShow];
        if (animated) [self startAnimate];
    }];
}

- (void)startAnimate {
    [UIView animateWithDuration:0.25 animations:^{
        self.view.alpha = 1;
    } completion:^(BOOL finished) {
        self.view.alpha = 1;
    }];
}

- (void)dismiss {
    [self dismissAnimated:NO];
}

- (void)dismissAnimated:(BOOL)animated {
    void (^AfterAnimated)(void) = ^() {
        [self.magicCubeView destructAllRes];
        [self dismissViewControllerAnimated:NO completion:^{
            if ([self.lifeCycleDelegate respondsToSelector:@selector(magicAlertDidDismiss:)]) {
                [self.lifeCycleDelegate magicAlertDidDismiss:self];
            }
        }];
    };
    
    if (animated) {
        [UIView animateWithDuration:0.25 animations:^{
            self.view.alpha = 0;
        } completion:^(BOOL finished) {
            AfterAnimated();
        }];
    } else {
        AfterAnimated();
    }
}

- (BOOL)isShowing {
    return self.alertShowing;
}

#pragma mark -- Getter
- (UIView *)blackView {
    if (!_blackView) {
        _blackView = [[UIView alloc] init];
        if (!self.alertConfig.disableBgCloseAction) {
            [_blackView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(blackViewTapAction)]];
        }
    }
    return _blackView;
}

- (JYMagicCubeView *)magicCubeView {
    if (!_magicCubeView) {
        _magicCubeView = [[JYMagicCubeView alloc] init];
        _magicCubeView.delegate = self;
    }
    return _magicCubeView;
}

@end
