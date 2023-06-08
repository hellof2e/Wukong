//
//  JYMCStyleDownloader.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/4/25.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMCStyleDownloadToken.h>
#import <JYMagicCube/JYMCStyleDownloaderBlocks.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleDownloader : NSObject

- (id<JYMCStyleDownloadToken>)downloadStyleWithURL:(NSURL *)url
                            completed:(JYMCStyleDownloadCompletedBlock)completedBlock;

/*
 当前下载任务数量
 */
- (NSUInteger)currentDownloadCount;

/*
 取消所有下载
 */
- (void)cancelAllDownloads;

/*
 单例入口
 */
+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END
