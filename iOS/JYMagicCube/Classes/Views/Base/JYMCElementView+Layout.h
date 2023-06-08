//
//  JYMCElementView+Layout.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView.h"

@class JYMCLayout;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCElementView (Layout)

/// 配置布局
/// @param config 布局信息
- (void)layout_applyLayout:(JYMCLayout *)config;

/// 根据宽度计算高度
/// @param width 宽度
- (CGFloat)layout_heightForWidth:(CGFloat)width;

/// 根据高度计算宽度
/// @param height 高度
- (CGFloat)layout_widthForHeight:(CGFloat)height;
/// 刷新布局
- (void)layout_refrehLayout;

@end

NS_ASSUME_NONNULL_END
