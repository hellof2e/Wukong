//
//  JYMagicCubeView+Action.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/6/27.
//

#import "JYMagicCubeView.h"

NS_ASSUME_NONNULL_BEGIN

@class JYMCCountingElement;
@class JYMCStateInfo;

/// 处理事件的分类
@interface JYMCCountingTimer : NSObject
@property (nonatomic, copy, readonly) NSDictionary* infoData;
@property (nonatomic, assign, readonly) BOOL isListItem;

- (void)setElement:(JYMCCountingElement *)element info:(JYMCStateInfo *)info;
- (BOOL)startWithFinishBlock:(dispatch_block_t)finishedBlock stepBlock:(dispatch_block_t)stepBlock;
- (void)stopTimer;
@end

@interface JYMCCountingTimerFactory:NSObject
+ (JYMCCountingTimer * _Nullable)createTimer:(JYMCCountingElement *)element
                                        info:(JYMCStateInfo *)info;
@end


NS_ASSUME_NONNULL_END
