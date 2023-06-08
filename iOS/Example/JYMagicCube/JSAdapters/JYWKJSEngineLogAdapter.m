//
//  JYWKJSEngineLogAdapter.m
//  JYMagicCube_Example
//
//  Created by gaoshuaibin091 on 2023/5/18.
//

#import "JYWKJSEngineLogAdapter.h"
#import <YYCategories/YYCategories.h>
@import JYWKJSEngine;

@implementation JYWKJSEngineLogAdapter

#pragma mark - JYWKLogAdapterProtocol

- (void)log:(NSString *)content {
    [self postNotificationWithLogInfo:content logType:0];
}

- (void)info:(NSString *)content {
    [self postNotificationWithLogInfo:content logType:1];
}

- (void)warn:(NSString *)content {
    [self postNotificationWithLogInfo:content logType:2];
}

- (void)error:(NSString *)content {
    [self postNotificationWithLogInfo:content logType:3];
}

- (void)table:(id)content {
    [self postNotificationWithLogInfo:[content jsonPrettyStringEncoded] logType:0];
}

#pragma mark - Notification

- (void)postNotificationWithLogInfo:(NSString *)logInfo logType:(NSUInteger)logType {
    NSMutableDictionary *userInfo = [NSMutableDictionary dictionary];
    userInfo[@"logInfo"] = logInfo;
    userInfo[@"logType"] = @(logType);
    [[NSNotificationCenter defaultCenter] postNotificationName:@"JYWKShowLogNotification" object:nil userInfo:userInfo.copy];
}

@end
