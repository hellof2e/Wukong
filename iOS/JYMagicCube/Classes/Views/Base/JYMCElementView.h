//
//  JYMCElementView.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/4.
//

#import <UIKit/UIKit.h>

@class JYMCElement, JYMCStateInfo;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCElementView : UIView

/// 元素信息
@property (nonatomic, strong, readonly) __kindof JYMCElement *element;

/// 数据信息
@property (nonatomic, strong) JYMCStateInfo *stateInfo;

/// 应用元素信息
/// @param element 元素信息
- (void)applyElement:(__kindof JYMCElement *)element;

/// 更新数据
/// @param info 数据
- (void)updateInfo:(JYMCStateInfo *)info;

/// 根据宽度估算高度
/// @param width 希望 view 展示的宽度
- (CGFloat)heightForWidth:(CGFloat)width;

/// 根据高度估算宽度
/// @param height 希望 view 展示的高度
- (CGFloat)widthForHeight:(CGFloat)height;

/// 刷新布局
- (void)refreshLayout;

/// 是否哨兵View
- (BOOL)isSentryView;

@end

NS_ASSUME_NONNULL_END
