//
//  JYMCLocalParameters.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/6/29.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCLocalParameters : NSObject

/// 用户标识
@property (nonatomic, copy, nullable) NSString *userToken;
/// 区域代码
@property (nonatomic, copy, nullable) NSString *adCode;
/// 城市代码
@property (nonatomic, copy, nullable) NSString *cityCode;
/// 系统版本号
@property (nonatomic, copy, readonly) NSString *osv;
/// 设备名称
@property (nonatomic, copy, readonly) NSString *deviceModel;
/// 平台标识
@property (nonatomic, copy, readonly) NSString *platform;

@end

NS_ASSUME_NONNULL_END
