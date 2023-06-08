//
//  JYMCStyleManager.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMagicCubeDefine.h>
#import <JYMagicCube/JYMCStyleOperation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleCombinedOperation : NSObject <JYMCStyleOperation>

@property (nonatomic, strong, nullable, readonly) id<JYMCStyleOperation> cacheOperation;
@property (nonatomic, strong, nullable, readonly) id<JYMCStyleOperation> downloadOperation;

- (void)cancel;

@end


@interface JYMCStyleManager : NSObject

/// 获取单例
+ (instancetype)shareManager;

/// 异步加载样式数据
/// @param url 样式链接
/// @param completionBlock 完成回调
/// @return 加载操作 operation，可以通过 cancel 取消加载
- (nullable JYMCStyleCombinedOperation *)loadStyleWithURL:(NSURL *)url completion:(JYMCStyleLoadCompletionBlock)completionBlock;


/// 同步获取样式缓存
/// @param url 样式链接
- (nullable JYMCStyleMetaData *)styleFromCacheForURL:(NSURL *)url;

/// 移除指定缓存样式
/// @param url 样式链接
/// @param completionBlock 完成回调
- (void)removeCacheForURL:(NSURL *)url completion:(nullable JYMCStyleCacheNoParamsBlock)completionBlock;;

/// 清除所有缓存样式
/// @param completionBlock 完成回调
- (void)clearCacheWithCompletion:(nullable JYMCStyleCacheNoParamsBlock)completionBlock;

@end

NS_ASSUME_NONNULL_END
