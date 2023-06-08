//
//  JYMCStyleDownloaderResultHandler.h
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol JYMCStyleDownloaderResultHandler <NSObject>

/*
 结果处理
 */
- (void)handleDownloadData:(NSData*)data;

/*
 结果处理
 */
- (void)handleDownloadResult:(NSError*)error;

@end

NS_ASSUME_NONNULL_END
