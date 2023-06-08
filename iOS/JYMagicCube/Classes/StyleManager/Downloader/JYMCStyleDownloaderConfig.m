//
//  JYMCStyleDownloaderConfig.m
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/26.
//

#import "JYMCStyleDownloaderConfig.h"

@implementation JYMCStyleDownloaderConfig

- (instancetype)initWithMaxConcurrentDownloads:(NSInteger)maxConcurrentDownloads downloadTimeout:(NSTimeInterval)downloadTimeout {
    self = [super init];
    if (self) {
        self.maxConcurrentDownloads = maxConcurrentDownloads;
        self.downloadTimeout = downloadTimeout;
    }
    return self;
}

+ (JYMCStyleDownloaderConfig *)defaultDownloaderConfig {
    return [[JYMCStyleDownloaderConfig alloc] initWithMaxConcurrentDownloads:4 downloadTimeout:20];
}

@end
