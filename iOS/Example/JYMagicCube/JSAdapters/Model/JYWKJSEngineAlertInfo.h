//
//  JYWKJSEngineAlertInfo.h
//  JYPlatformMarketingModule
//
//  Created by huangshengzhong118 on 2023/3/8.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMCAlertConfig.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYWKJSEngineAlertInfo : NSObject

/// dsl 样式链接
@property (nonatomic, copy) NSString *style;
/// dsl 数据字典
@property (nonatomic, copy) NSDictionary *data;
/// 弹窗弹出的方位。( top | center | bottom) 默认是： center
@property (nonatomic, copy) NSString *gravity;
/// 触摸弹窗外部是否需要关闭。默认值：true
@property (nonatomic, assign) BOOL canceledOnTouchOutside;

- (JYMCAlertConfig *)alertConfig;

@end

NS_ASSUME_NONNULL_END
