//
//  JYTestPayExportHandler.m
//  JYMagicCube_Example
//
//  Created by gaoshuaibin091 on 2023/5/17.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import "JYTestPayExportHandler.h"
#import <YYCategories/YYCategories.h>
@import JYWKJSEngine;

@implementation JYTestPayExportHandler

- (void)goPay:(NSDictionary *)params {
    NSString *log = [NSString stringWithFormat:@"js-CorePay: %@", [params jsonPrettyStringEncoded]];
    [[JYWKAdapterManager sharedInstance].logAdapter log:log];
}

@end
