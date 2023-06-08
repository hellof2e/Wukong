//
//  JYMCActionExpose.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2023/5/8.
//

#import <Foundation/Foundation.h>
#import "JYMCActionReport.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCActionExpose : NSObject

/// 埋点上报信息
@property (nonatomic, strong, nullable) JYMCActionReport *report;

@end

NS_ASSUME_NONNULL_END
