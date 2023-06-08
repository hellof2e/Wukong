//
//  JYMCElementView+Style.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView.h"

@class JYMCViewStyle;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCElementView (Style)

/// 配置样式
/// @param style 样式信息
/// @param info 数据info
- (void)style_applyStyle:(JYMCViewStyle *)style info:(JYMCStateInfo *)info;

/// 主类layoutSubviews调用分类方法
- (void)style_layoutSubViews;

/// 创建圆角shape
/// @param bounds 宿主View.bounds
/// @param radius 各个圆角大小数组<左上，右上，右下，左下>
/// @return 带圆角shape
+ (nullable CAShapeLayer *)style_createShaperWithBounds:(CGRect)bounds radius:(NSArray *)radius;

/// 获取radius
/// @param cornerRadius 未解析radiu字符串
/// @param info 数据info
/// @return 解析后radius
+ (CGFloat)cornerRadiusWithString:(NSString *)cornerRadius info:(JYMCStateInfo *)info;

/// 获取radii
/// @param cornerRadii 未解析radius数组
/// @param info 数据info
/// @return 解析后radii数组
+ (NSArray *)cornerRadiiWithArray:(NSArray *)cornerRadii info:(JYMCStateInfo *)info;

- (void)style_applyActiveStyle;
- (void)style_applyNormalStyle;

@end

NS_ASSUME_NONNULL_END
