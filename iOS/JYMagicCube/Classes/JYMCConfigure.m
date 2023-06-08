//
//  JYMCConfigure.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/14.
//

#import "JYMCConfigure.h"
#import "JYMCStyleManager.h"
#import "JYMCConfigure+Private.h"

@interface JYMCConfigure ()

@property (nonatomic, copy, readwrite) NSString *appName;
@property (nonatomic, copy, readwrite) NSString *appVersion;
@property (nonatomic, copy, readwrite) JYMCHandleActionBlock globalActionHandler;
@property (nonatomic, copy) JYMCLocalParametersBlock parametersHandler;
@property (nonatomic, strong, readwrite) id<JYMCTrackAdapter> trackAdapter;

@end

@implementation JYMCConfigure

+ (void)initWithAppName:(NSString *)appName appVersion:(NSString *)appVersion {
    [JYMCConfigure shareConfigure].appName = appName;
    [JYMCConfigure shareConfigure].appVersion = appVersion;
}

+ (void)setGlobalActionHandler:(JYMCHandleActionBlock)globalActionHandler {
    [JYMCConfigure shareConfigure].globalActionHandler = globalActionHandler;
}

+ (void)setLocalParametersHandler:(JYMCLocalParametersBlock)parametersHandler {
    [JYMCConfigure shareConfigure].parametersHandler = parametersHandler;
}

+ (void)setTrackAdapter:(id<JYMCTrackAdapter>)trackAdapter {
    [JYMCConfigure shareConfigure].trackAdapter = trackAdapter;
}

+ (void)clearAllCacheWithCompletion:(nullable void(^)(void))completionBlock {
    [[JYMCStyleManager shareManager] clearCacheWithCompletion:completionBlock];
}

@end
