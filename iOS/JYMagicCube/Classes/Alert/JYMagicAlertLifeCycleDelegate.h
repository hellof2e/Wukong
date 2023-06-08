//
//  JYMagicAlertDelegate.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/4/8.
//  Copyright © 2022 张新令. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@protocol JYMagicAlertProtocol;

@protocol JYMagicAlertLifeCycleDelegate <NSObject>

@optional
/// DSL弹框展示成功回调
- (void)magicAlertDidShow:(id<JYMagicAlertProtocol>)magicAlert;

/// DSL弹框展示失败回调
- (void)magicAlertShowFailure:(id<JYMagicAlertProtocol>)magicAlert error:(NSError *)error;

/// DSL弹框消失
- (void)magicAlertDidDismiss:(id<JYMagicAlertProtocol>)magicAlert;

/// DSL弹框js交互事件
- (void)magicAlert:(id<JYMagicAlertProtocol>)magicAlert jsCallNative:(NSDictionary<NSString *,id> *)params;

@end

NS_ASSUME_NONNULL_END
