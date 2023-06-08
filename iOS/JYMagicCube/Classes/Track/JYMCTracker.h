//
//  JYMCTracker.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/30.
//

#import <Foundation/Foundation.h>
@class JYMCTrackActionInfo;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCTrackExceptionInfo : NSObject

/// 异常信息
@property (nonatomic, copy) NSString *exception;
/// 样式链接
@property (nonatomic, copy, nullable) NSString *styleUrl;
/// 额外信息
@property (nonatomic, copy, nullable) NSDictionary *extra;

@end

@interface JYMCTracker : NSObject

/// 异常埋点
+ (void)trackForException:(JYMCTrackExceptionInfo *)exceptionInfo;
/// 交互事件埋点
+ (void)trackForAction:(JYMCTrackActionInfo *)actionInfo;
/// 性能埋点
+ (void)trackPerformance:(NSDictionary *)performanceInfo;

@end

NS_ASSUME_NONNULL_END
