//
//  JYMCStyleDownloaderConfig.h
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/26.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleDownloaderConfig : NSObject

// 最大并发数 default 4
@property (nonatomic, assign) NSInteger maxConcurrentDownloads;

//default 20s
@property (nonatomic, assign) NSTimeInterval downloadTimeout;

/*
 初始化方法
 */
- (instancetype)initWithMaxConcurrentDownloads:(NSInteger)maxConcurrentDownloads downloadTimeout:(NSTimeInterval)downloadTimeout;

/*
 默认下载配置
 */
+ (JYMCStyleDownloaderConfig*)defaultDownloaderConfig;

@end

NS_ASSUME_NONNULL_END
