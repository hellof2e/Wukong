//
//  UIDevice+JY.m
//  JYMagicCube_Example
//
//  Created by huangshengzhong118 on 2023/5/19.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import "UIDevice+JY.h"

@implementation UIDevice (JY)

#pragma mark - Tool
+ (CGFloat)stautsBarAndNaviBarHeight {
    return 44.f + [UIDevice stautsBarHeight];
}

+ (CGFloat)stautsBarHeight {
    return  [UIDevice safeAreaInsetsTopHeight] > 0 ? [UIDevice safeAreaInsetsTopHeight] : 20;
}

+ (CGFloat)safeAreaInsetsTopHeight {
    CGFloat gap = 0.f;
    if (@available(iOS 11, *)) {
        gap = [UIApplication sharedApplication].keyWindow.safeAreaLayoutGuide.layoutFrame.origin.y;
    } else {
        gap = 0;
    }
    return gap;
}

@end
