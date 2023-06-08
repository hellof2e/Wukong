//
//  JYWKJSEngineAlertInfo.m
//  JYPlatformMarketingModule
//
//  Created by huangshengzhong118 on 2023/3/8.
//

#import "JYWKJSEngineAlertInfo.h"

@implementation JYWKJSEngineAlertInfo

- (JYMCAlertConfig *)alertConfig {
    JYMCAlertConfig *config = [[JYMCAlertConfig alloc] init];
    config.alertPostion = [self alertPos];
    config.alertBgAlpha = 0.5;
    config.disableBgCloseAction = self.canceledOnTouchOutside;
    
    return config;
}

- (JYMCAlertPostion)alertPos {
    JYMCAlertPostion pos = JYMCAlertPostionCenter;
    if ([self.gravity isEqualToString:@"top"]) {
        pos = JYMCAlertPostionTop;
    } else if ([self.gravity isEqualToString:@"bottom"]) {
        pos = JYMCAlertPostionBottom;
    }
    return pos;
}

@end
