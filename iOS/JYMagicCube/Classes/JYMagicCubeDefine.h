//
//  JYMagicCubeDefine.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/5/12.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMCStyleMetaData.h>

/// 样式缓存类型
typedef NS_ENUM(NSInteger, JYMCStyleCacheType) {
    JYMCStyleCacheTypeNone,   //无缓存
    JYMCStyleCacheTypeDisk,   //磁盘缓存
    JYMCStyleCacheTypeMemory, //内存缓存
    JYMCStyleCacheTypeAll     //内存缓存+磁盘缓存
};

typedef void(^JYMCStyleCacheNoParamsBlock)(void);
typedef void(^JYMCStyleCacheContainsBlock)(JYMCStyleCacheType cacheType);
typedef void(^JYMCStyleCacheQueryCompletionBlock)(JYMCStyleMetaData * _Nullable styleMetaData, JYMCStyleCacheType cacheType);

typedef void(^JYMCStyleLoadCompletionBlock)(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL);

typedef void(^JYMCStylePrefetcherCompletionBlock)(NSSet<NSString*> * _Nullable successfulUrls, NSSet<NSString*> * _Nullable failureUrls);
