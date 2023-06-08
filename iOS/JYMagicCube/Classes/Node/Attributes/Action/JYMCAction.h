//
//  JYMCAction.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import <Foundation/Foundation.h>
#import "JYMCActionClick.h"
#import "JYMCActionExpose.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCAction : NSObject

/// 点击事件
@property (nonatomic, strong) JYMCActionClick *click;

/// js事件
@property (nonatomic, copy) NSString *clickEvent;

/// 曝光事件
@property (nonatomic, strong) JYMCActionExpose *expose;

/// 倒计时结束事件
@property (nonatomic, copy) NSString *countingFinishEvent;

@end

NS_ASSUME_NONNULL_END
