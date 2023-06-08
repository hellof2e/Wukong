//
//  JYMCElement.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/9.
//

#import "JYMCElement.h"
#import <MJExtension/MJExtension.h>

@implementation JYMCElement

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    return @{
        @"mIf": @"m-if",
        @"mFor": @"m-for",
        @"activeStyle": @"active-style",
        @"jsLifecycle": @"lifecycle",
    };
}

- (BOOL)useActive {
    return self.activeStyle != nil;
}
@end
