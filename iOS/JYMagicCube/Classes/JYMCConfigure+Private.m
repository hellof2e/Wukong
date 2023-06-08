//
//  JYMCConfigure+Private.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import "JYMCConfigure+Private.h"

@interface JYMCConfigure ()

@property (nonatomic, copy, readonly) NSString *appName;
@property (nonatomic, copy, readonly) NSString *appVersion;
@property (nonatomic, copy, readonly) JYMCHandleActionBlock globalActionHandler;
@property (nonatomic, copy, readonly) JYMCLocalParametersBlock parametersHandler;
@property (nonatomic, strong, readonly) id<JYMCTrackAdapter> trackAdapter;

@end

@implementation JYMCConfigure (Private)

+ (instancetype)shareConfigure {
    static JYMCConfigure *configure;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        configure = [[JYMCConfigure alloc] init];
    });
    return configure;
}

+ (NSString *)appName {
    return [JYMCConfigure shareConfigure].appName;
}

+ (NSString *)appVersion {
    return [JYMCConfigure shareConfigure].appVersion;
}

+ (JYMCHandleActionBlock)globalActionHandler {
    return [JYMCConfigure shareConfigure].globalActionHandler;
}

+ (JYMCLocalParametersBlock)parametersHandler {
    return [JYMCConfigure shareConfigure].parametersHandler;
}

+ (id<JYMCTrackAdapter>)trackAdapter {
    return [JYMCConfigure shareConfigure].trackAdapter;
}

@end
