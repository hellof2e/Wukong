//
//  JYMagicAlertDelegate.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/4/8.
//  Copyright © 2022 张新令. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class JYMagicAlertViewController;

@protocol JYMagicAlertDelegate <NSObject>

/// DSL弹框加载成功
- (void)magicAlertDidLoadSuccess:(JYMagicAlertViewController *)magicAlert;

/// DSL弹框加载失败
- (void)magicAlert:(JYMagicAlertViewController *)magicAlert didLoadFailWithError:(NSError *)error;

@end

NS_ASSUME_NONNULL_END
