//
//  JYMCMForSentryView.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/30.
//

#import "JYMCElementView.h"

NS_ASSUME_NONNULL_BEGIN

/// mFor 视图的哨兵视图
@interface JYMCMForSentryView : JYMCElementView

/// 对应视图的 Class
@property (nonatomic, strong) Class viewClass;

@end

NS_ASSUME_NONNULL_END
