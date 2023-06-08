//
//  JYMCStyleCache.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMagicCubeDefine.h>
#import <JYMagicCube/JYMCStyleOperation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleCache : NSObject

/// 获取单例
+ (instancetype)shareCache;

/// 同步获取样式缓存（先查询内存缓存，没有再查询磁盘缓存）
/// @param key 缓存 key
- (nullable JYMCStyleMetaData *)styleFromCacheForKey:(NSString *)key;

/// 异步查询样式缓存（先查询内存缓存，没有再查询磁盘缓存），通过 cancel operation 可以中止查询操作
/// @param key 缓存 key
/// @param completionBlock 完成回调，如果 operation 被 cancel 将不执行
/// @return 查询操作对象 operion
- (nullable id<JYMCStyleOperation>)queryStyleForKey:(NSString *)key
                                         completion:(nullable JYMCStyleCacheQueryCompletionBlock)completionBlock;

/// 异步查询样式缓存（根据 cacheType 进行对应查询），通过 cancel operation 可以中止查询操作
/// @param key 缓存 key
/// @param cacheType 缓存类型
/// @param completionBlock 完成回调，如果 operation 被 cancel 将不执行
/// @return 查询操作对象 operion
- (nullable id<JYMCStyleOperation>)queryStyleForKey:(NSString *)key
                                          cacheType:(JYMCStyleCacheType)cacheType
                                         completion:(nullable JYMCStyleCacheQueryCompletionBlock)completionBlock;

/// 缓存样式
/// @param styleMetaData 样式元数据
/// @param key 缓存 key
/// @param cacheType 缓存类型
/// @param completionBlock 完成回调
- (void)storeStyle:(JYMCStyleMetaData *)styleMetaData
            forKey:(NSString *)key
         cacheType:(JYMCStyleCacheType)cacheType
        completion:(nullable JYMCStyleCacheNoParamsBlock)completionBlock;

/// 删除指定样式缓存
/// @param key 缓存 key
/// @param cacheType 缓存类型
/// @param completionBlock 完成回调
- (void)removeStyleForKey:(NSString *)key
                cacheType:(JYMCStyleCacheType)cacheType
               completion:(nullable JYMCStyleCacheNoParamsBlock)completionBlock;

/// 指定样式是否已缓存
/// @param key 缓存 key
/// @param cacheType 缓存类型
/// @param completionBlock 完成回调
- (void)containsStyleForKey:(NSString *)key
                  cacheType:(JYMCStyleCacheType)cacheType
                 completion:(nullable JYMCStyleCacheContainsBlock)completionBlock;

/// 清除所有缓存
/// @param cacheType 缓存类型
/// @param completionBlock 完成回调
- (void)clearAllWithCacheType:(JYMCStyleCacheType)cacheType
                   completion:(nullable JYMCStyleCacheNoParamsBlock)completionBlock;

@end

NS_ASSUME_NONNULL_END
