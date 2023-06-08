//
//  JYMCAlertConfig.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/15.
//

#import "JYMCAlertConfig.h"

@implementation JYMCAlertConfig

- (void)setAlertBgAlpha:(CGFloat)alertBgAlpha {
    if (alertBgAlpha < 0) alertBgAlpha = 0;
    if (alertBgAlpha > 1) alertBgAlpha = 1;
    _alertBgAlpha = alertBgAlpha;
}

@end
