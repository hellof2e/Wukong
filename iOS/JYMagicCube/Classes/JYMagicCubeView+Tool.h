//
//  JYMagicCubeView+Tool.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/24.
//

#import "JYMagicCubeView.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMagicCubeView (Tool)

/// 样式是否与内部相同
/// @param styleContent 样式内容
- (BOOL)isSameStyle:(NSString *)styleContent;

/// 设置js timeout倒计时
/// @param interval 倒计时间（ms）
/// @param callback 倒计时完成回调
- (void)jsTimeoutWithInterval:(NSTimeInterval)interval callback:(dispatch_block_t)callback;

/// 释放资源
- (void)destructAllRes;

/// 触发内部曝光（当外部重新加载后，可调用该函数重新上报内部DSL配置的曝光埋点）
- (void)trackExpose;

/// 获取样式中根节点宽度（预加载成功后调用有效。如果样式根节点未设置宽度，则返回 0）
/// @return 宽度
- (CGFloat)widthInStyle;

@end

NS_ASSUME_NONNULL_END
