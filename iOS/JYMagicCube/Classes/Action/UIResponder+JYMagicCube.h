//
//  UIResponder+JYMagicCube.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/22.
//

#import <UIKit/UIKit.h>
#import "JYMCActionInternalContext.h"
#import "JYMCActionContext.h"
#import "JYMCTrackContext.h"

NS_ASSUME_NONNULL_BEGIN

@interface UIResponder (JYMagicCube)

/// 开始处理事件(URL跳转事件)
/// @param context 事件上下文
/// @return 是否由接入方处理该事件. YES: 接入方处理
- (BOOL)mc_didStartHandleAction:(JYMCActionInternalContext *)context;

/// 开始处理埋点
/// @param context 埋点上下文
/// @返回值：是否由接入方处理该埋点. YES: 接入方处理
- (BOOL)mc_didStartHandleTrack:(JYMCTrackContext *)context;

/// 倒计时结束
/// @param context 交互上下文
- (void)mc_didActionFinished:(JYMCActionContext *)context;

/// 倒计时Tick
/// @param context 交互上下文
- (void)mc_didActionTimerTick:(JYMCActionContext *)context;

/// 开始处理js事件
/// @param context 交互上下文
- (void)mc_didStartJSAction:(JYMCActionInternalContext *)context;

@end

NS_ASSUME_NONNULL_END
