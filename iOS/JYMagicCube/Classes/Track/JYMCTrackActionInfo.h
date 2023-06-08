//
//  JYMCTrackActionInfo.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/10.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// Action 埋点信息
@interface JYMCTrackActionInfo : NSObject

/// 业务信息
@property (nonatomic, copy, nullable) NSDictionary<NSString *, NSString *> *busInfo;

/// 是否已经上传
@property (nonatomic, assign) BOOL isUploaded;

@end


/// 曝光埋点信息
@interface JYMCTrackActionExposeInfo : JYMCTrackActionInfo

/// 曝光视图
@property (nonatomic, weak) UIView* view;

@end

NS_ASSUME_NONNULL_END
