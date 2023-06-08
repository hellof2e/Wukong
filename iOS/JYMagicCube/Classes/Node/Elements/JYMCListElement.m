//
//  JYMCListElement.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCListElement.h"
#import <MJExtension/MJExtension.h>

NSString* const kJYMCListItemTypeDefaultKey = @"JYMCListItemTypeDefaultKey";

@implementation JYMCListItemElement
- (id)init {
    self = [super init];
    if (self) {
        _itemType = kJYMCListItemTypeDefaultKey;
    }
    return self;
}

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"item-type" forKey:@"itemType"];
    return dic;
}

@end

@implementation JYMCListElement

@synthesize children = _children;

- (id)init {
    self = [super init];
    if (self) {
        _orientation = @"row";
    }
    return self;
}

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"list-data" forKey:@"listData"];
    [dic setObject:@"item-key" forKey:@"itemKey"];
    return dic;
}


- (void)setChildren:(NSArray<JYMCElement *> *)children {
    _children = children;
    NSMutableDictionary* items = [[NSMutableDictionary alloc] init];
    [_children enumerateObjectsUsingBlock:^(JYMCElement * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[JYMCListItemElement class]]) {
            JYMCListItemElement *item = (JYMCListItemElement *)obj;
            NSString *key = item.itemType.length == 0 ? kJYMCListItemTypeDefaultKey : item.itemType;
            items[key] = item;
        }
    }];
    
    _items = items.copy;
}

@end
