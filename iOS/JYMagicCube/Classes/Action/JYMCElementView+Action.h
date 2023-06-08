//
//  JYMCElementView+Action.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView.h"

@class JYMCAction;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCElementView (Action)

/// 配置交互
/// @param action 交互信息
/// @param data 动态下发数据
- (void)action_applyAction:(JYMCAction *)action info:(JYMCStateInfo *)data;


- (void)action_applyexpose;
@end

NS_ASSUME_NONNULL_END
