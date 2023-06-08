//
//  JYMCTracker.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/30.
//

#import "JYMCTracker.h"
#import "JYMCConfigure+Private.h"
#import "JYMCTrackActionInfo.h"

@implementation JYMCTrackExceptionInfo

@end

@implementation JYMCTracker

+ (void)trackForException:(JYMCTrackExceptionInfo *)exceptionInfo {
    if (!exceptionInfo) {
        return;
    }
    
    id<JYMCTrackAdapter> trackAdapter = [JYMCConfigure trackAdapter];
    if (trackAdapter) {
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        params[@"channel"] = @"iOS";
        params[@"styleUrl"] = exceptionInfo.styleUrl ?: @"";
        params[@"exception"] = exceptionInfo.exception ?: @"";
        if (exceptionInfo.extra.count > 0) {
            [params addEntriesFromDictionary:exceptionInfo.extra];
        }
        [trackAdapter trackWithParams:params];
    }
}

+ (void)trackForAction:(JYMCTrackActionInfo *)actionInfo {
    if (!actionInfo || actionInfo.isUploaded) {
        return;
    }
    
    id<JYMCTrackAdapter> trackAdapter = [JYMCConfigure trackAdapter];
    if (trackAdapter) {
        actionInfo.isUploaded = YES;
        [trackAdapter trackWithParams:actionInfo.busInfo];
    }
}

/// 性能埋点
+ (void)trackPerformance:(NSDictionary *)performanceInfo {
    if (!performanceInfo) {
        return;
    }
    
#if DEBUG
    printf("%s %s\n", __FUNCTION__, [NSString stringWithFormat:@"%@", performanceInfo].UTF8String);
#endif
    
    id<JYMCTrackAdapter> trackAdapter = [JYMCConfigure trackAdapter];
    if (trackAdapter) {
        [trackAdapter trackWithParams:performanceInfo];
    }
}

@end
