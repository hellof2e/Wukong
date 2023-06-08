//
//  JYMCPreloadCacheManager.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/11/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class JYMCMetaData;

@interface JYMCPreloadCacheManager : NSObject

/// 获取单例
+ (instancetype)shareCache;

/// 通过styleUrl & data 获取唯一的md5值
/// @param styleUrl 样式链接
/// @param data 数据（确保数据唯一性 ⚠️字典无序⚠️ ）
/// @return md5Key
+ (nullable NSString *)md5KeyWithStyleUrl:(NSString *)styleUrl data:(NSDictionary *)data;

/// 查询缓存中的预加载数据
/// @param md5Key 缓存数据匹配唯一key
/// @return 预加载数据
- (JYMCMetaData *)cacheMetaDataForKey:(NSString *)md5Key;

/// 清除缓存中的预加载数据
/// @param md5Key 缓存数据匹配唯一key
- (void)clearCacheMetaDataWithKey:(NSString *)md5Key;

/// 查询缓存中的预加载数据
/// @param styleUrl 样式链接
/// @param data 数据（确保数据唯一性 ⚠️字典无序⚠️ ）
/// @return 预加载数据
- (JYMCMetaData *)cacheMetaDataWithStyleUrl:(NSString *)styleUrl data:(NSDictionary *)data;

/// 存储预加载数据
/// @param metaData 预加载数据
/// @param md5Key 缓存数据匹配唯一key
- (void)storeValue:(JYMCMetaData *)metaData key:(NSString *)md5Key;

@end

NS_ASSUME_NONNULL_END
