//
//  JYLoadingSession.h
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/19.
//

#import <Foundation/Foundation.h>
#import "JYMCPerformanceRecord.h"

NS_ASSUME_NONNULL_BEGIN

/// 加载和渲染阶段时的上下文
@interface JYMCLoadingSession : NSObject
@property (nonatomic, strong) JYMCPerformanceRecord* performanceRecord;

+ (instancetype)sessionWithStyleURL:(NSURL * _Nullable)url;

@end

NS_ASSUME_NONNULL_END
