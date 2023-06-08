//
//  JYMCStyleDownloaderOperation.h
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/27.
//

#import <Foundation/Foundation.h>
#import "JYMCStyleDownloaderBlocks.h"
#import "JYMCStyleDownloadToken.h"
#import "JYMCStyleDownloaderResultHandler.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleDownloaderOperation : NSOperation<JYMCStyleDownloaderResultHandler>

- (instancetype)initWithURL:(NSURL*)url session:(NSURLSession*)session;

- (id<JYMCStyleDownloadToken>)addHandlersForCompleted:(JYMCStyleDownloadCompletedBlock)completedBlock;

@end

NS_ASSUME_NONNULL_END
