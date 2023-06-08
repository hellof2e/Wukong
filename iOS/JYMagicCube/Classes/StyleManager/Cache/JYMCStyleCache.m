//
//  JYMCStyleCache.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import "JYMCStyleCache.h"
#import <YYCache/YYCache.h>

@interface JYMCStyleCache ()

@property (nonatomic, strong) YYCache *cache;

@end

@implementation JYMCStyleCache

#pragma mark - Public

+ (instancetype)shareCache {
    static JYMCStyleCache *cache;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        cache = [[JYMCStyleCache alloc] init];
    });
    return cache;
}

- (JYMCStyleMetaData *)styleFromCacheForKey:(NSString *)key {
    return (JYMCStyleMetaData *)[self.cache objectForKey:key];
}

- (id<JYMCStyleOperation>)queryStyleForKey:(NSString *)key completion:(JYMCStyleCacheQueryCompletionBlock)completionBlock {
    return [self queryStyleForKey:key cacheType:JYMCStyleCacheTypeAll completion:completionBlock];
}

- (id<JYMCStyleOperation>)queryStyleForKey:(NSString *)key cacheType:(JYMCStyleCacheType)cacheType completion:(JYMCStyleCacheQueryCompletionBlock)completionBlock {
    if (key == nil || cacheType == JYMCStyleCacheTypeNone) {
        if (completionBlock) {
            completionBlock(nil, JYMCStyleCacheTypeNone);
        }
        return nil;
    }
    
    JYMCStyleMetaData *styleMetaData;
    if (cacheType != JYMCStyleCacheTypeDisk) {
        styleMetaData = [self.cache.memoryCache objectForKey:key];
    }
    
    BOOL shouldQueryMemoryOnly = (cacheType == JYMCStyleCacheTypeMemory) || (styleMetaData && cacheType == JYMCStyleCacheTypeAll);
    if (shouldQueryMemoryOnly) {
        if (completionBlock) {
            completionBlock(styleMetaData, JYMCStyleCacheTypeMemory);
        }
        return nil;
    }
    
    NSOperation *operation = [NSOperation new];
    void(^queryDiskBlock)(JYMCStyleMetaData *) = ^(JYMCStyleMetaData *diskData) {
        if (diskData) {
            [self.cache.memoryCache setObject:diskData forKey:key];
        }
        
        if (operation.isCancelled) {
            if (completionBlock) {
                completionBlock(nil, JYMCStyleCacheTypeNone);
            }
            return;
        }
        
        if (completionBlock) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completionBlock(diskData, JYMCStyleCacheTypeDisk);
            });
        }
    };
    [self.cache.diskCache objectForKey:key withBlock:^(NSString * _Nonnull key, id<NSCoding>  _Nullable object) {
        JYMCStyleMetaData *diskData;
        if ([(id<NSObject>)object isKindOfClass:[JYMCStyleMetaData class]]) {
            diskData = (JYMCStyleMetaData *)object;
        }
        queryDiskBlock(diskData);
    }];
    
    return operation;
}

- (void)storeStyle:(JYMCStyleMetaData *)styleMetaData forKey:(NSString *)key cacheType:(JYMCStyleCacheType)cacheType completion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    switch (cacheType) {
        case JYMCStyleCacheTypeNone: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeMemory: {
            [self.cache.memoryCache setObject:styleMetaData forKey:key];
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeDisk: {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
                [self storeStyleToDisk:styleMetaData forKey:key completion:completionBlock];
            });
        }
            break;
        case JYMCStyleCacheTypeAll: {
            [self.cache.memoryCache setObject:styleMetaData forKey:key];
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
                [self storeStyleToDisk:styleMetaData forKey:key completion:completionBlock];
            });
        }
            break;
        default: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
    }
}

- (void)storeStyleToDisk:(JYMCStyleMetaData *)styleMetaData forKey:(NSString *)key completion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    if (styleMetaData == nil) {
        if (completionBlock) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completionBlock();
            });
        }
        return;
    }
    [self.cache.diskCache setObject:styleMetaData forKey:key withBlock:^{
        if (completionBlock) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completionBlock();
            });
        }
    }];
}

- (void)removeStyleForKey:(NSString *)key cacheType:(JYMCStyleCacheType)cacheType completion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    switch (cacheType) {
        case JYMCStyleCacheTypeNone: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeMemory: {
            [self.cache.memoryCache removeObjectForKey:key];
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeDisk: {
            [self.cache.diskCache removeObjectForKey:key withBlock:^(NSString * _Nonnull key) {
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock();
                    });
                }
            }];
        }
            break;
        case JYMCStyleCacheTypeAll: {
            [self.cache.memoryCache removeObjectForKey:key];
            [self.cache.diskCache removeObjectForKey:key withBlock:^(NSString * _Nonnull key) {
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock();
                    });
                }
            }];
        }
            break;
        default: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
    }
}

- (void)containsStyleForKey:(NSString *)key cacheType:(JYMCStyleCacheType)cacheType completion:(JYMCStyleCacheContainsBlock)completionBlock {
    switch (cacheType) {
        case JYMCStyleCacheTypeNone: {
            if (completionBlock) {
                completionBlock(JYMCStyleCacheTypeNone);
            }
        }
            break;
        case JYMCStyleCacheTypeMemory: {
            BOOL isInMemoryCache = [self.cache.memoryCache objectForKey:key] != nil;
            if (completionBlock) {
                completionBlock(isInMemoryCache ? JYMCStyleCacheTypeMemory : JYMCStyleCacheTypeNone);
            }
        }
            break;
        case JYMCStyleCacheTypeDisk: {
            [self.cache.diskCache containsObjectForKey:key withBlock:^(NSString * _Nonnull key, BOOL contains) {
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock(contains ? JYMCStyleCacheTypeDisk : JYMCStyleCacheTypeNone);
                    });
                }
            }];
        }
            break;
        case JYMCStyleCacheTypeAll: {
            BOOL isInMemoryCache = [self.cache.memoryCache objectForKey:key] != nil;
            if (isInMemoryCache) {
                if (completionBlock) {
                    completionBlock(JYMCStyleCacheTypeMemory);
                }
                return;
            }
            [self.cache.diskCache containsObjectForKey:key withBlock:^(NSString * _Nonnull key, BOOL contains) {
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock(contains ? JYMCStyleCacheTypeDisk : JYMCStyleCacheTypeNone);
                    });
                }
            }];
        }
            break;
        default: {
            if (completionBlock) {
                completionBlock(JYMCStyleCacheTypeNone);
            }
        }
            break;
    }
}

- (void)clearAllWithCacheType:(JYMCStyleCacheType)cacheType completion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    switch (cacheType) {
        case JYMCStyleCacheTypeNone: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeMemory: {
            [self.cache.memoryCache removeAllObjects];
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
        case JYMCStyleCacheTypeDisk: {
            [self.cache.diskCache removeAllObjectsWithBlock:^{
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock();
                    });
                }
            }];
        }
            break;
        case JYMCStyleCacheTypeAll: {
            [self.cache.memoryCache removeAllObjects];
            [self.cache.diskCache removeAllObjectsWithBlock:^{
                if (completionBlock) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        completionBlock();
                    });
                }
            }];
        }
            break;
        default: {
            if (completionBlock) {
                completionBlock();
            }
        }
            break;
    }
}

#pragma mark - Getter

- (YYCache *)cache {
    if (_cache == nil) {
        _cache = [YYCache cacheWithName:@"com.magicCube.style"];
    }
    return _cache;
}

@end
