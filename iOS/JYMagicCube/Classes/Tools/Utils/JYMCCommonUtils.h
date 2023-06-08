//
//  JYMCCommonUtils.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/11/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCCommonUtils : NSObject

/// 跳转到路由页面
/// - Parameter router: 路由地址
+ (void)navigator:(NSString *)router;

@end

NS_ASSUME_NONNULL_END
