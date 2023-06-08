//
//  JYMagicAlertProtocol.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/4/6.
//  Copyright © 2022 张新令. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class JYMCAlertConfig;
@protocol JYMagicAlertLoaderDelegate, JYMagicAlertProtocol;

typedef void(^JYMagicAlertLoaderCompletionBlock)(BOOL success, _Nullable id<JYMagicAlertProtocol> magicAlert,  NSError * _Nullable error);

@protocol JYMagicAlertLoaderProtocol <NSObject>

/// 创建弹框加载器
/// @param delegate 事件代理
/// @param url 样式链接
/// @param data 数据
/// @param config 弹框配置信息
+ (id<JYMagicAlertLoaderProtocol>)loaderAlertWithDelegate:(id<JYMagicAlertLoaderDelegate>)delegate
                                                      url:(NSString *)url
                                                     data:(NSDictionary *)data
                                                   config:(JYMCAlertConfig *)config;
/// 创建弹框加载器
/// @param url 样式链接
/// @param data 数据
/// @param config 弹框配置信息
/// @param complete 加载完成回调
+ (id<JYMagicAlertLoaderProtocol>)loaderAlertWithUrl:(NSString *)url
                                                data:(NSDictionary *)data
                                              config:(JYMCAlertConfig *)config
                                            complete:(JYMagicAlertLoaderCompletionBlock)complete;

@end

NS_ASSUME_NONNULL_END
