//
//  JYMCJSExpressionCache.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/17.
//

#import "JYMCJSExpressionCache.h"
#import <YYCache/YYMemoryCache.h>

@interface JYMCJSExpressionCache ()

@property (nonatomic, strong) YYMemoryCache *memoryCache;

@end

@implementation JYMCJSExpressionCache

+ (instancetype)shareCache {
    static JYMCJSExpressionCache *cache;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        cache = [[JYMCJSExpressionCache alloc] init];
    });
    return cache;
}

- (id)cacheValueWithJSExpression:(NSString *)expression {
    if (![expression isKindOfClass:NSString.class] || expression.length == 0) {
        return nil;
    }
    
    return [self.memoryCache objectForKey:expression];
}

- (void)storeValue:(id)value expression:(NSString *)expression {
    if (!value || ![expression isKindOfClass:NSString.class] || expression.length == 0) {
        return;
    }
    
    [self.memoryCache setObject:value forKey:expression];
}

#pragma mark - Getter

- (YYMemoryCache *)memoryCache {
    if (_memoryCache == nil) {
        _memoryCache = [[YYMemoryCache alloc] init];
        _memoryCache.name = @"com.magicCube.JSExpression";
        _memoryCache.countLimit = 1000;
    }
    return _memoryCache;
}

@end
