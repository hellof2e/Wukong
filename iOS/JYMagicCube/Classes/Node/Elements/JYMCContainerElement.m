//
//  JYMCContainerElement.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCContainerElement.h"
#import <MJExtension/MJExtension.h>

@implementation JYMCContainerElement
@synthesize children;

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"background-image" forKey:@"backgroundImage"];
    [dic setObject:@"active-background-image" forKey:@"activeBackgroundImage"];
    [dic setObject:@"match" forKey:@"matchType"];
    
    return dic;
}


- (BOOL)useActive{
    return self.activeStyle != nil || self.activeBackgroundImage.length > 0;
}
@end
