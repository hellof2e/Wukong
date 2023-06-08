//
//  JYMCPreloadCacheManager.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/11/2.
//

#import "JYMCPreloadCacheManager.h"

#import <YYCache/YYMemoryCache.h>
#import <MJExtension/MJExtension.h>
#import <YYCategories/YYCategories.h>

@interface JYMCPreloadCacheManager ()

@property (nonatomic, strong) YYMemoryCache *memoryCache;

@end

@implementation JYMCPreloadCacheManager

+ (instancetype)shareCache {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

+ (nullable NSString *)md5KeyWithStyleUrl:(NSString *)styleUrl data:(NSDictionary *)data {
    if (styleUrl.length == 0 || data.count == 0) return nil;
    NSString *dataString = [data mj_JSONString];
    if (dataString.length == 0) return nil;
    NSString *s = [NSString stringWithFormat:@"%@%@", styleUrl, dataString];
    return s.md5String;
}

- (JYMCMetaData *)cacheMetaDataForKey:(NSString *)md5Key {
    if (![md5Key isKindOfClass:NSString.class] || md5Key.length == 0) {
        return nil;
    }

    return [self.memoryCache objectForKey:md5Key];
}

- (void)clearCacheMetaDataWithKey:(NSString *)md5Key {
    if (![md5Key isKindOfClass:NSString.class] || md5Key.length == 0) {
        return;
    }
    
    [self.memoryCache removeObjectForKey:md5Key];
}

- (JYMCMetaData *)cacheMetaDataWithStyleUrl:(NSString *)styleUrl data:(NSDictionary *)data {
    NSString *md5Key = [JYMCPreloadCacheManager md5KeyWithStyleUrl:styleUrl data:data];
    return [self cacheMetaDataForKey:md5Key];
}

- (void)storeValue:(JYMCMetaData *)metaData key:(NSString *)md5Key {
    if (!metaData || ![md5Key isKindOfClass:NSString.class] || md5Key.length == 0) {
        return;
    }

    [self.memoryCache setObject:metaData forKey:md5Key];
}

#pragma mark - Getter

- (YYMemoryCache *)memoryCache {
    if (_memoryCache == nil) {
        _memoryCache = [[YYMemoryCache alloc] init];
        _memoryCache.name = @"com.magicCube.PreloadCache";
        _memoryCache.countLimit = 10;
    }
    return _memoryCache;
}

@end
