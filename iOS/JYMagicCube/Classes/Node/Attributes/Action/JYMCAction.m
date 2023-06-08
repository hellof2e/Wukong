//
//  JYMCAction.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCAction.h"
#import <MJExtension/MJExtension.h>

@implementation JYMCAction

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    return @{@"clickEvent": @"click-event",
             @"countingFinishEvent":@"counting-finish-event"};
}

@end
