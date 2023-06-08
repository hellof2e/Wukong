//
//  JYMCStylePrefetcher.m
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/30.
//

#import "JYMCStylePrefetcher.h"
#import <JYMagicCube/JYMCStyleManager.h>
#import <JYMagicCube/JYMCStyleOperation.h>


@protocol JYMCStylePrefetcherTokenDelegate

- (void)prefetchFinishedWithToken:(id<JYMCStyleOperation>)token;

@end

@interface JYMCStylePrefetcherToken: NSObject<JYMCStyleOperation>

@property (nonatomic, copy) NSSet<NSString*> *urls;

@property (nonatomic, weak) id<JYMCStylePrefetcherTokenDelegate> delegate;

@property (nonatomic, copy) JYMCStylePrefetcherCompletionBlock completedBlock;

@property (nonatomic, strong) NSMutableDictionary<NSString*, id<JYMCStyleOperation>> *urlToOperation;

@property (nonatomic, strong) NSMutableSet<NSString*> *successfulUrls, *failureUrls;

@end

@implementation JYMCStylePrefetcherToken

- (instancetype)initWithURLs:(NSSet<NSString*>*)urls completedBlock:(JYMCStylePrefetcherCompletionBlock)completedBlock delegate:(id<JYMCStylePrefetcherTokenDelegate>)delegate {
    self = [super init];
    if (self) {
        self.urlToOperation = [[NSMutableDictionary alloc] init];
        self.urls = urls;
        self.delegate = delegate;
        self.failureUrls = urls ? [[NSMutableSet alloc] initWithSet:urls] : [NSMutableSet new];
        self.successfulUrls = [[NSMutableSet alloc] init];
        self.completedBlock = completedBlock;
    }
    return self;
}

- (void)prefetch {
    if ([_urls count] > 0) {
        for (NSString *url in _urls) {
            NSURL *URL = [NSURL URLWithString:url];
            _urlToOperation[url] = [[JYMCStyleManager shareManager] loadStyleWithURL:URL completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL) {
                NSString *urlStr = styleURL.absoluteString;
                if (!error) {
                    [self.failureUrls removeObject:urlStr];
                    [self.successfulUrls addObject:urlStr];
                }
                // 这里故意将 finished 操作放到下一次 runloop，因为如果样式本身已经在内存缓存中，当前 block 是同步执行的，而此时的 opeartion 还未加到 _urlToOperation 中
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self prefetchFinished:urlStr];
                });
            }];
        }
    } else {
        _completedBlock([NSSet new], [NSSet new]);
    }
}

- (void)prefetchFinished:(NSString*)url {
    @synchronized (self) {
        [self.urlToOperation removeObjectForKey:url];
    }
    if ([_urlToOperation count] == 0) {
        self.completedBlock(self.successfulUrls, self.failureUrls);
        [self.delegate prefetchFinishedWithToken:self];
    }
}

- (void)cancel {
    NSArray<id<JYMCStyleOperation>> *operations = [self.urlToOperation allValues];
    for (id<JYMCStyleOperation> operation in operations) {
        [operation cancel];
    }
    @synchronized (self) {
        [self.urlToOperation removeAllObjects];
    }
    [self.delegate prefetchFinishedWithToken:self];
}

@end

@interface JYMCStylePrefetcher ()<JYMCStylePrefetcherTokenDelegate>

@property (nonatomic, strong) NSMutableSet<id<JYMCStyleOperation>> *tokens;

@end

@implementation JYMCStylePrefetcher

#pragma mark JYMCStylePrefetcherTokenDelegate
- (void)prefetchFinishedWithToken:(id<JYMCStyleOperation>)token {
    @synchronized (self) {
        [_tokens removeObject:token];
    }
}


- (instancetype)init {
    self = [super init];
    if (self) {
        self.tokens = [[NSMutableSet alloc] init];
    }
    return self;
}

- (id<JYMCStyleOperation>)prefetchURLs:(NSSet<NSString *> *)urls completed:(JYMCStylePrefetcherCompletionBlock)completedBlock {
    JYMCStylePrefetcherToken *token = [[JYMCStylePrefetcherToken alloc] initWithURLs:urls completedBlock:completedBlock delegate:self];
    @synchronized (self) {
        [self.tokens addObject:token];
    }
    [token prefetch];
    return token;
}

- (id)copy {
    return self;
}

- (id)mutableCopy {
    return self;
}

+ (instancetype)sharedInstance {
    static JYMCStylePrefetcher *sharedObject;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedObject = [[JYMCStylePrefetcher alloc] init];
    });
    return sharedObject;
}

@end
