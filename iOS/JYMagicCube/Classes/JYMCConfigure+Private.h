//
//  JYMCConfigure+Private.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import "JYMCConfigure.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCConfigure (Private)

+ (instancetype)shareConfigure;

+ (NSString *)appName;
+ (NSString *)appVersion;
+ (nullable JYMCHandleActionBlock)globalActionHandler;
+ (nullable JYMCLocalParametersBlock)parametersHandler;
+ (nullable id<JYMCTrackAdapter>)trackAdapter;

@end

NS_ASSUME_NONNULL_END
