//
//  JYTestShareExportHandler.m
//  JYMagicCube_Example
//
//  Created by gaoshuaibin091 on 2023/5/23.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import "JYTestShareExportHandler.h"
#import <YYCategories/YYCategories.h>
@import JYWKJSEngine;

@implementation JYTestShareExportHandler

- (void)goShare:(NSDictionary *)params {
    NSString *log = [NSString stringWithFormat:@"js-CoreShare: %@", [params jsonPrettyStringEncoded]];
    [[JYWKAdapterManager sharedInstance].logAdapter log:log];
}

@end
