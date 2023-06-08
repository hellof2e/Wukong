//
//  JYMCStylePrefetcher.h
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/30.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMagicCubeDefine.h>
#import <JYMagicCube/JYMCStyleOperation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStylePrefetcher : NSOperation

/// 预加载样式
/// - Parameters:
///   - urls: 样式链接集合
///   - completedBlock: 完成回调
- (id<JYMCStyleOperation>)prefetchURLs:(nullable NSSet<NSString *> *)urls
                             completed:(nullable JYMCStylePrefetcherCompletionBlock)completedBlock;

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END
