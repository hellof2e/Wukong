//
//  JYMCCountingElement.h
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/24.
//

#import "JYMCElement.h"
#import "JYMCContainerElement.h"

NS_ASSUME_NONNULL_BEGIN

/// 倒计时
@interface JYMCCountingElement : JYMCContainerElement
/// 步长
@property (nonatomic, copy) NSString* step;

/// 倒计时初始值
@property (nonatomic, copy) NSString* count;

/// 定时器类型，分为时间，数字，默认"time"
/// 
/// 如果是时间类型，step单位为秒
/// 如果是数字类型，step单位为个位数
@property (nonatomic, copy) NSString* countingType; // time、number

/// 倒计时停止位置
@property (nonatomic, copy) NSString* stop;

/// 间隔时间
@property (nonatomic, copy) NSString* interval;


/// 本地时间戳与服务器时间戳偏移量  小于0表示本地时间靠前，大于0表示本地时间靠后
@property (nonatomic, copy) NSString* clockOffset;


/// 结束时间戳
@property (nonatomic, copy) NSString* deadline;

/// 是否刷新最外层
@property (nonatomic, assign) BOOL needRefreshContent;

@end

NS_ASSUME_NONNULL_END
