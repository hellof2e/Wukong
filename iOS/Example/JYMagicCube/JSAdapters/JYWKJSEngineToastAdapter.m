//
//  JYWKJSEngineToastAdapter.m
//  JYPlatformMarketingModule
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

#import "JYWKJSEngineToastAdapter.h"

#import <MBProgressHUD/MBProgressHUD.h>

@implementation JYWKJSEngineToastAdapter

- (void)showToast:(NSString * _Nonnull)content {
    UIView *window =[UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [[MBProgressHUD alloc] initWithView:window];
    hud.mode = MBProgressHUDModeText;
    [window addSubview:hud];
    hud.label.text = content;
    [hud showAnimated:YES];
    [hud hideAnimated:YES afterDelay:3];
}

@end
