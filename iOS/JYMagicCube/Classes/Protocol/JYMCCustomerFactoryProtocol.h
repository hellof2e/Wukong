//
//  JYMCCustomerFactoryProtocol.h
//  Pods
//
//  Created by gaoshuaibin091 on 2022/2/11.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 悟空自定义视图工厂协议
@protocol JYMCCustomerFactoryProtocol <NSObject>

@required

/// 创建一个自定义视图
/// @param type 当前自定义视图在 style 中对应的 type 值
/// @param data 当前悟空视图对应的整体数据
/// @param scopeData 自定义视图所在层级的数据
- (UIView *)mc_createCustomViewWithType:(NSString *)type data:(NSDictionary<NSString *,id> *)data scopeData:(NSDictionary<NSString *,id> *)scopeData;

/// 更新数据
/// @param customView 当前自定义视图
/// @param type 当前自定义视图在 style 中对应的 type 值
/// @param data 当前悟空视图对应的整体数据
/// @param scopeData 自定义视图所在层级的数据
- (void)mc_updateCustomView:(UIView *)customView type:(NSString *)type data:(NSDictionary<NSString *,id> *)data scopeData:(NSDictionary<NSString *,id> *)scopeData;

@optional

/// customView 移动到屏幕外，可以在该时机去修改一些设置，如：暂停视频、音频播放等操作
/// @param customView 当前自定义视图
/// @param type 当前自定义视图在 style 中对应的 type 值
- (void)mc_moveInScreenCustomView:(UIView *)customView type:(NSString *)type data:(NSDictionary<NSString *,id> *)data scopeData:(NSDictionary<NSString *,id> *)scopeData;

/// customView 移动到屏幕外，可以在该时机去修改一些设置，如：暂停视频、音频播放等操作
/// @param customView 当前自定义视图
/// @param type 当前自定义视图在 style 中对应的 type 值
- (void)mc_moveOutScreenCustomView:(UIView *)customView type:(NSString *)type data:(NSDictionary<NSString *,id> *)data scopeData:(NSDictionary<NSString *,id> *)scopeData;

/// customView 移动到屏幕外且即将被释放, 可以在该时机去释放 customView 使用到的一些系统资源，如：关闭视频、音频播放器等操作
/// @param customView 当前自定义视图
/// @param type 当前自定义视图在 style 中对应的 type 值
- (void)mc_releaseCustomView:(UIView *)customView type:(NSString *)type;

@end

NS_ASSUME_NONNULL_END
