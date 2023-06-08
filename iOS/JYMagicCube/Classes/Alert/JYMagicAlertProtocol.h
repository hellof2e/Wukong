//
//  JYMagicAlertProtocol.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/4/6.
//  Copyright © 2022 张新令. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@protocol JYMagicAlertLifeCycleDelegate;

@protocol JYMagicAlertProtocol <NSObject>

/// 配置弹框生命周期代理
- (void)configLifeCycleDelegate:(id<JYMagicAlertLifeCycleDelegate>)lifeCycleDelegate;

/// 弹出DSL弹框
/// @param viewcontroller 父控制器
/// @param animated 是否动画
- (void)show:(UIViewController *)viewcontroller animated:(BOOL)animated;

/// 销毁DSL弹框
/// @param animated 是否动画
- (void)dismissAnimated:(BOOL)animated;

/// 弹框是否已弹出
- (BOOL)isShowing;

@end

NS_ASSUME_NONNULL_END
