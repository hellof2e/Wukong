//
//  JYMCActionClick.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/10.
//

#import <Foundation/Foundation.h>
#import "JYMCActionReport.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCActionClick : NSObject

/// 跳转 URL
@property (nonatomic, copy) NSString *url;

/// 埋点上报信息
@property (nonatomic, strong, nullable) JYMCActionReport *report;

@end

NS_ASSUME_NONNULL_END
