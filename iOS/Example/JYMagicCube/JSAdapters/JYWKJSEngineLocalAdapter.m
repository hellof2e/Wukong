//
//  JYWKJSEngineLocalAdapter.m
//  JYPlatformMarketingModule
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

#import "JYWKJSEngineLocalAdapter.h"

@implementation JYWKJSEngineLocalAdapter

- (NSDictionary<NSString *,id> *)userInfo {
    NSLog(@"悟空：返回用户信息");
    return @{};
}

- (NSDictionary<NSString *,id> *)deviceInfo {
    NSLog(@"悟空：返回设备信息");
    return @{};
}

- (NSDictionary<NSString *,id> *)location {
    NSLog(@"悟空：返回定位信息");
    return @{};
}

@end
