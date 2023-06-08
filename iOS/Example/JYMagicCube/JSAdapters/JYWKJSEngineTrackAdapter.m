//
//  JYWKJSEngineTrackAdapter.m
//  JYPlatformMarketingModule
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

#import "JYWKJSEngineTrackAdapter.h"

@implementation JYWKJSEngineTrackAdapter

- (void)trackClick:(NSDictionary *)info {
    NSLog(@"悟空：记录点击");
}

- (void)trackCustom:(NSDictionary *)info {
    NSLog(@"悟空：记录性能埋点");
}

- (void)trackExpose:(NSDictionary *)info {
    NSLog(@"悟空：记录曝光");
}
@end
