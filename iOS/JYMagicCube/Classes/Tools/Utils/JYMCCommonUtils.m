//
//  JYMCCommonUtils.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/11/25.
//

#import "JYMCCommonUtils.h"
#import "JYMCActionContext.h"
#import "JYMCConfigure.h"
#import "JYMCConfigure+Private.h"
#import "JYMCActionWebViewController.h"

@implementation JYMCCommonUtils

#pragma mark - Public

+ (void)navigator:(NSString *)router {
    if (router.length == 0) {
        return;
    }
    
    JYMCActionContext *context = [[JYMCActionContext alloc] init];
    context.url = router;
    JYMCHandleActionBlock globalActionHandler = [JYMCConfigure globalActionHandler];
    if (globalActionHandler) {
        globalActionHandler(context);
        return;
    }
    
    [self handleURLString:router];
}

#pragma mark - Private

+ (void)handleURLString:(NSString *)urlString
{
    if ([urlString hasPrefix:@"http"]) {
        JYMCActionWebViewController *vc = [JYMCActionWebViewController new];
        vc.url = urlString;
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
        nav.modalPresentationStyle = UIModalPresentationFullScreen;
        [[self topViewController] presentViewController:nav animated:YES completion:nil];
    } else {
        NSURL *url = [NSURL URLWithString:urlString];
        if (url) {
            [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:nil];
        }
    }
}

+ (UIViewController *)topViewController {
    UIViewController *resultVC;
    resultVC = [self _topViewController:[[UIApplication sharedApplication].keyWindow rootViewController]];
    while (resultVC.presentedViewController) {
        resultVC = [self _topViewController:resultVC.presentedViewController];
    }
    return resultVC;
}

+ (UIViewController *)_topViewController:(UIViewController *)vc {
    if ([vc isKindOfClass:[UINavigationController class]]) {
        return [self _topViewController:[(UINavigationController *)vc topViewController]];
    } else if ([vc isKindOfClass:[UITabBarController class]]) {
        return [self _topViewController:[(UITabBarController *)vc selectedViewController]];
    } else {
        return vc;
    }
    return nil;
}

@end
