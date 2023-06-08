//
//  JYMCConfigure.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/14.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMCTrackAdapter.h>
#import <JYMagicCube/JYMCActionContext.h>
#import <JYMagicCube/JYMCLocalParameters.h>

NS_ASSUME_NONNULL_BEGIN

/// 交互处理 block
typedef void(^JYMCHandleActionBlock)(JYMCActionContext *actionContext);
typedef void(^JYMCLocalParametersBlock)(JYMCLocalParameters *parameters);

@interface JYMCConfigure : NSObject

/// 初始化
/// @param appName app 名称，必传
/// @param appVersion app 版本号，必传
+ (void)initWithAppName:(NSString *)appName appVersion:(NSString *)appVersion;

/// 设置全局交互处理 block
/// @param globalActionHandler 全局交互处理 block
+ (void)setGlobalActionHandler:(JYMCHandleActionBlock)globalActionHandler;

/// 设置本地参数
+ (void)setLocalParametersHandler:(JYMCLocalParametersBlock)parametersHandler;

/// 设置全局埋点代理
/// @param trackAdapter 埋点代理
+ (void)setTrackAdapter:(id<JYMCTrackAdapter>)trackAdapter;

/// 清理所有缓存样式
+ (void)clearAllCacheWithCompletion:(nullable void(^)(void))completionBlock;

@end

NS_ASSUME_NONNULL_END
