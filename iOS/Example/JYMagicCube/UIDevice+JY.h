//
//  UIDevice+JY.h
//  JYMagicCube_Example
//
//  Created by huangshengzhong118 on 2023/5/19.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIDevice (JY)

+ (CGFloat)stautsBarAndNaviBarHeight;

+ (CGFloat)stautsBarHeight;

+ (CGFloat)safeAreaInsetsTopHeight;

@end

NS_ASSUME_NONNULL_END
