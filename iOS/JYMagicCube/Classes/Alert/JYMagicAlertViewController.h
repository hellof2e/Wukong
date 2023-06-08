//
//  JYMagicAlertViewController.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/15.
//

#import <UIKit/UIKit.h>
#import "JYMagicAlertProtocol.h"

NS_ASSUME_NONNULL_BEGIN

@class JYMCAlertConfig;
@protocol JYMagicAlertDelegate;

@interface JYMagicAlertViewController : UIViewController <JYMagicAlertProtocol>

/// 创建弹框
/// @param delegate 事件代理
/// @param url 样式链接
/// @param data 数据
/// @param config 弹框配置信息
+ (instancetype)createAlertWithDelegate:(id<JYMagicAlertDelegate>)delegate
                                    url:(NSString *)url
                                   data:(NSDictionary *)data
                                 config:(JYMCAlertConfig *)config;

@end

NS_ASSUME_NONNULL_END
