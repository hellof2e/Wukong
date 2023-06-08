//
//  JYWKJSEngineCacheAdapter.m
//  JYPlatformMarketingModule
//
//  Created by gaoshuaibin091 on 2022/8/24.
//

#import "JYWKJSEngineCacheAdapter.h"

#import <YYCache/YYCache.h>
#import <YYCategories/YYCategories.h>

@interface JYWKJSEngineCacheAdapter ()

@property (nonatomic, strong) dispatch_semaphore_t semaphore;
@property (nonatomic, strong) NSMutableDictionary *caches;

@end

@implementation JYWKJSEngineCacheAdapter

- (instancetype)init {
    self = [super init];
    if (self) {
        self.semaphore = dispatch_semaphore_create(1);
    }
    return self;
}

#pragma mark - JYWKCacheAdapterProtocol

- (void)cacheItemWithFileName:(NSString * _Nonnull)fileName key:(NSString * _Nonnull)key value:(id _Nonnull)value {
    if (fileName.length == 0 || key.length == 0) {
        return;
    }
    YYCache *cache = [self cacheWithFileName:fileName];
    [cache setObject:value forKey:key withBlock:nil];
}

- (id _Nullable)readItemWithFileName:(NSString * _Nonnull)fileName key:(NSString * _Nonnull)key {
    if (fileName.length == 0 || key.length == 0) {
        return nil;
    }
    YYCache *cache = [self cacheWithFileName:fileName];
    return [cache objectForKey:key];
}

- (YYCache *)cacheWithFileName:(NSString *)fileName {
    if (fileName.length == 0) {
        return nil;
    }
    dispatch_semaphore_wait(self.semaphore, DISPATCH_TIME_FOREVER);
    if ([self.caches containsObjectForKey:fileName]) {
        YYCache *cache = [self.caches objectForKey:fileName];
        dispatch_semaphore_signal(self.semaphore);
        return cache;
    }
    
    YYCache *cache = [YYCache cacheWithName:fileName];
    if (cache) [self.caches setObject:cache forKey:fileName];
    dispatch_semaphore_signal(self.semaphore);
    
    return cache;
}

#pragma mark -- Getter
- (NSMutableDictionary *)caches {
    if (!_caches) {
        _caches = @{}.mutableCopy;
    }
    return _caches;
}

@end
