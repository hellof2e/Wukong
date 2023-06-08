//
//  JYMCCountingElement.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/24.
//

#import "JYMCCountingElement.h"
#import <MJExtension/MJExtension.h>

@implementation JYMCCountingElement
- (id)init {
    self = [super init];
    if (self) {
        _stop = @"0";
        _step = @"1000";
        _countingType = @"time";
        _interval = @"1000";
        _clockOffset = @"0";
        _needRefreshContent = YES;
    }
    
    return self;
}

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"clock-offset" forKey:@"clockOffset"];
    [dic setObject:@"counting-type" forKey:@"countingType"];
    
    return dic;
}

- (void)mj_didConvertToObjectWithKeyValues:(NSDictionary *)keyValues {
    id refreshContent = [keyValues objectForKey:@"refresh-content"];
    if (refreshContent && [refreshContent isKindOfClass:NSString.class]) {
        NSString *refreshContentStr = (NSString *)refreshContent;
        if (refreshContentStr.length && ![refreshContentStr isEqualToString:@"1"]) {
            self.needRefreshContent = NO;
        }
    }
}

@end
