//
//  JYMCTrackContext.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/6/29.
//

#import <Foundation/Foundation.h>

/// 埋点类型
typedef NS_ENUM(NSUInteger, JYMCTrackType) {
    JYMCTrackTypeClick,   // 点击
    JYMCTrackTypeExpose,  // 曝光
    JYMCTrackTypeCustom   // 自定义
};

NS_ASSUME_NONNULL_BEGIN

@interface JYMCTrackContext : NSObject

/// 埋点类型，default is JYMCTrackTypeClick
@property (nonatomic, assign) JYMCTrackType type;

/// 业务信息
@property (nonatomic, copy, nullable) NSDictionary<NSString *, NSString *> *busInfo;

@end

NS_ASSUME_NONNULL_END
