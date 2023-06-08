//
//  JYMCLottieElement.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/18.
//

#import "JYMCProgressElement.h"
#import <MJExtension/MJExtension.h>

@implementation JYMCProgressElement
+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"max-progress" forKey:@"maxProgress"];
    [dic setObject:@"progress-color" forKey:@"progressColor"];
    return dic;
}

- (id)init {
    self = [super init];
    if (self) {
        _progress = @"";
        _maxProgress = @"";
        _progressColor = @"#ffff00";
    }
    
    return self;
}

@end
