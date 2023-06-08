//
//  JYMCStyleManager.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import "JYMCStyleManager.h"
#import "JYMCError.h"
#import "JYMCStyleCache.h"
#import "JYMagicCubeMacro.h"
#import "JYMCStyleDownloader.h"
#import "JYMCConfigure+Private.h"
#import <YYCategories/YYCategories.h>
#import "JYMCStyleParser.h"

@interface JYMCStyleCombinedOperation ()

@property (nonatomic, assign, getter=isCancelled) BOOL cancelled;
@property (nonatomic, strong, nullable, readwrite) id<JYMCStyleOperation> cacheOperation;
@property (nonatomic, strong, nullable, readwrite) id<JYMCStyleOperation> downloadOperation;
@property (nonatomic, weak, nullable) JYMCStyleManager *manager;

@end


@interface JYMCStyleManager () {
    MC_LOCK_DECLARE(_runningOperationsLock);
}

@property (nonatomic, strong) JYMCStyleCache *cache;
@property (nonatomic, strong) JYMCStyleDownloader *downloader;
@property (nonatomic, strong) NSMutableSet<JYMCStyleCombinedOperation *> *runningOperations;

@end

@implementation JYMCStyleManager

+ (instancetype)shareManager {
    static JYMCStyleManager *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[JYMCStyleManager alloc] init];
    });
    return manager;
}

- (instancetype)init {
    if (self = [super init]) {
        _cache = [JYMCStyleCache shareCache];
        _downloader = [JYMCStyleDownloader sharedInstance];
        _runningOperations = [NSMutableSet new];
        MC_LOCK_INIT(_runningOperationsLock);
    }
    return self;
}

#pragma mark - Public

- (JYMCStyleCombinedOperation *)loadStyleWithURL:(NSURL *)url completion:(JYMCStyleLoadCompletionBlock)completionBlock {
    if ([url isKindOfClass:[NSString class]]) {
        url = [NSURL URLWithString:(NSString *)url];
    }
    if (![url isKindOfClass:[NSURL class]]) {
        url = nil;
    }
    if (url.absoluteString.length == 0) {
        NSError *error = [JYMCError errorWithCode:JYMCErrorInvalidURL localizedDescription:@"url 为空"];
        [self callCompletionBlock:completionBlock error:error url:url];
        return nil;
    }
    
    JYMCStyleCombinedOperation *operation = [JYMCStyleCombinedOperation new];
    operation.manager = self;
    
    MC_LOCK(_runningOperationsLock);
    [self.runningOperations addObject:operation];
    MC_UNLOCK(_runningOperationsLock);
    
    [self callCacheProcessForOperation:operation url:url completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL) {
        if (completionBlock == nil) {
            return;
        }
        // 样式版本校验
        if (styleMetaData) {
            BOOL styleVersionIsSupport = [self styleVersionIsSupport:styleMetaData];
            if (styleVersionIsSupport) {
                completionBlock(styleMetaData, error, cacheType, styleURL);
            } else {
                NSError *e = [JYMCError errorWithCode:JYMCErrorVersionNotSupport];
                completionBlock(nil, e, cacheType, styleURL);
            }
        } else {
            completionBlock(styleMetaData, error, cacheType, styleURL);
        }
    }];
    
    return operation;
}

- (JYMCStyleMetaData *)styleFromCacheForURL:(NSURL *)url {
    if ([url isKindOfClass:[NSString class]]) {
        url = [NSURL URLWithString:(NSString *)url];
    }
    if (![url isKindOfClass:[NSURL class]]) {
        url = nil;
    }
    if (url.absoluteString.length == 0) {
        return nil;
    }
    
    NSString *key = [self cacheKeyForURL:url];
    JYMCStyleMetaData *style = [self.cache styleFromCacheForKey:key];
    if (style) {
        BOOL styleVersionIsSupport = [self styleVersionIsSupport:style];
        if (styleVersionIsSupport) {
            return style;
        }
    }
    return nil;
}

- (void)removeCacheForURL:(NSURL *)url completion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    if ([url isKindOfClass:[NSString class]]) {
        url = [NSURL URLWithString:(NSString *)url];
    }
    if (![url isKindOfClass:[NSURL class]]) {
        url = nil;
    }
    if (url.absoluteString.length == 0) {
        if (completionBlock) {
            completionBlock();
        }
        return;
    }
    
    NSString *key = [self cacheKeyForURL:url];
    [self.cache removeStyleForKey:key cacheType:JYMCStyleCacheTypeAll completion:completionBlock];
}

- (void)clearCacheWithCompletion:(JYMCStyleCacheNoParamsBlock)completionBlock {
    [self.cache clearAllWithCacheType:JYMCStyleCacheTypeAll completion:completionBlock];
}

#pragma mark - Private

- (void)callCacheProcessForOperation:(nonnull JYMCStyleCombinedOperation *)operation
                                 url:(nonnull NSURL *)url
                          completion:(nullable JYMCStyleLoadCompletionBlock)completionBlock {
    NSString *key = [self cacheKeyForURL:url];
    @weakify(operation);
    operation.cacheOperation = [self.cache queryStyleForKey:key completion:^(JYMCStyleMetaData * _Nullable styleMetaData, JYMCStyleCacheType cacheType) {
        @strongify(operation);
        if (operation == nil || operation.isCancelled) {
            NSError *error = [JYMCError errorWithCode:JYMCErrorCancelled localizedDescription:@"查询缓存被取消"];
            [self callCompletionBlock:completionBlock error:error url:url];
            [self safelyRemoveOperationFromRunning:operation];
            return;
        }
        
        if (styleMetaData) {
            [self callCompletionBlock:completionBlock styleMetaData:styleMetaData error:nil cacheType:cacheType url:url];
            [self safelyRemoveOperationFromRunning:operation];
        } else {
            [self callDownloadProcessForOperation:operation url:url completion:completionBlock];
        }
    }];
}

- (void)callDownloadProcessForOperation:(nonnull JYMCStyleCombinedOperation *)operation
                                    url:(nonnull NSURL *)url
                             completion:(nullable JYMCStyleLoadCompletionBlock)completionBlock {
    @weakify(operation);
    operation.downloadOperation = [self.downloader downloadStyleWithURL:url completed:^(JYMCStyleMetaData * _Nullable data, NSError * _Nullable error) {
        @strongify(operation);
        if (operation == nil || operation.isCancelled) {
            NSError *error = [JYMCError errorWithCode:JYMCErrorCancelled localizedDescription:@"下载被取消"];
            [self callCompletionBlock:completionBlock error:error url:url];
            [self safelyRemoveOperationFromRunning:operation];
            return;
        }
        
        if (error) {
            [self callCompletionBlock:completionBlock error:error url:url];
        } else {
            [self callStoreStyleProcessWithStyle:data url:url];
            [self callCompletionBlock:completionBlock styleMetaData:data error:nil cacheType:JYMCStyleCacheTypeNone url:url];
        }
        [self safelyRemoveOperationFromRunning:operation];
    }];
}

- (void)callStoreStyleProcessWithStyle:(nonnull JYMCStyleMetaData *)styleMetaData url:(nonnull NSURL *)url{
    NSString *key = [self cacheKeyForURL:url];
    [self.cache storeStyle:styleMetaData forKey:key cacheType:JYMCStyleCacheTypeAll completion:nil];
}

- (void)callCompletionBlock:(nullable JYMCStyleLoadCompletionBlock)completionBlock
                      error:(nullable NSError *)error
                        url:(nullable NSURL *)url {
    [self callCompletionBlock:completionBlock styleMetaData:nil error:error cacheType:JYMCStyleCacheTypeNone url:url];
}

- (void)callCompletionBlock:(nullable JYMCStyleLoadCompletionBlock)completionBlock
              styleMetaData:(nullable JYMCStyleMetaData *)styleMetaData
                      error:(nullable NSError *)error
                  cacheType:(JYMCStyleCacheType)cacheType
                        url:(nullable NSURL *)url {
    if (completionBlock) {
        dispatch_main_async_safe(^{
            completionBlock(styleMetaData, error, cacheType, url);
        });
    }
}

- (void)safelyRemoveOperationFromRunning:(nullable JYMCStyleCombinedOperation *)operation {
    if (operation == nil) {
        return;
    }
    MC_LOCK(_runningOperationsLock);
    [self.runningOperations removeObject:operation];
    MC_UNLOCK(_runningOperationsLock);
}

- (NSString *)cacheKeyForURL:(NSURL *)url {
    if (url == nil || ![url isKindOfClass:[NSURL class]]) {
        return @"";
    }
    NSString *urlString = url.absoluteString;
    if (urlString.length == 0) {
        return @"";
    }
    return [urlString md5String];
}

- (BOOL)styleVersionIsSupport:(JYMCStyleMetaData *)style {
    NSString *currentVersion = [JYMCConfigure appVersion];
    NSString *styleVersion = style.supportedVersion;
    if (styleVersion.length == 0 || currentVersion.length == 0) {
        return NO;
    }
    if ([currentVersion compare:styleVersion options:NSNumericSearch] == NSOrderedAscending) {
        return NO;
    }
    return YES;
}

@end


@implementation JYMCStyleCombinedOperation

- (void)cancel {
    @synchronized (self) {
        if (self.isCancelled) {
            return;
        }
        self.cancelled = YES;
        if (self.cacheOperation) {
            [self.cacheOperation cancel];
            self.cacheOperation = nil;
        }
        if (self.downloadOperation) {
            [self.downloadOperation cancel];
            self.downloadOperation = nil;
        }
        [self.manager safelyRemoveOperationFromRunning:self];
    }
}

@end
