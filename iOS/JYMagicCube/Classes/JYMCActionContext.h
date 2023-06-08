//
//  JYMCActionContext.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/4.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 倒计时事件
FOUNDATION_EXTERN NSString * const JYMCActionCounterName;

/// 暴露给外界使用的事件数据模型
@interface JYMCActionContext : NSObject

/// 跳转 URL
@property (nonatomic, copy, nullable) NSString *url;

/// 领域
@property (nonatomic, copy, nullable) NSDictionary *scopeData;

/// 事件名称：如counter、js method
@property (nonatomic, copy, nullable) NSString *actionName;

/// 错误信息
@property (nonatomic, strong, nullable) NSError *error;

@end

NS_ASSUME_NONNULL_END
