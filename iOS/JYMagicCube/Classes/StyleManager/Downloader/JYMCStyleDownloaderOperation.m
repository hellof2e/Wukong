//
//  JYMCStyleDownloaderOperation.m
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/27.
//

#import "JYMCStyleDownloaderOperation.h"
#import "JYMCError.h"
#import "JYMagicCubeMacro.h"
#import "JYMCStyleMetaData.h"
#import <MJExtension/MJExtension.h>
#import <YYCategories/YYCategories.h>

@protocol JYMCStyleDownloadTokenDelegate <NSObject>

- (void)cancel:(id<JYMCStyleDownloadToken>)token;

- (BOOL)downloadIsCanceled;

@end


@interface JYMCStyleSimpleDownloadToken : NSObject <JYMCStyleDownloadToken>

@property (nonatomic, strong) NSURL *url;

@property (nonatomic, copy) JYMCStyleDownloadCompletedBlock completedBlock;

@property (nonatomic, weak) id<JYMCStyleDownloadTokenDelegate> delegate;

@end

@implementation JYMCStyleSimpleDownloadToken

- (instancetype)initWithURL:(NSURL*)url delegate:(id<JYMCStyleDownloadTokenDelegate>)delegate completedBlock:(JYMCStyleDownloadCompletedBlock)completedBlock {
    self = [super init];
    if (self) {
        self.delegate = delegate;
        self.url = url;
        self.completedBlock = completedBlock;
    }
    return self;
}

#pragma mark JYMCStyleDownloadToken
- (void)cancel {
    if (![_delegate downloadIsCanceled]) {
        [_delegate cancel:self];
    }
}

- (NSURL *)url {
    return _url;
}

@end

/*
 ***************************************************************************************************************
 ***************************************************************************************************************
 ***************************************************************************************************************
 ***************************************************************************************************************
 ***************************************************************************************************************
 */

@interface JYMCStyleDownloaderOperation ()<JYMCStyleDownloadTokenDelegate>

@property (nonatomic, strong) NSURLSession *session;

@property (nonatomic, strong) NSURL *url;

@property (nonatomic, copy) NSDictionary<NSString*, NSString*> *urlParams;

@property (assign, nonatomic, getter = isExecuting) BOOL executing;

@property (assign, nonatomic, getter = isFinished) BOOL finished;

@property (nonatomic, strong) NSMutableArray<JYMCStyleSimpleDownloadToken*> *tokens;

@property (nonatomic, strong) NSURLSessionDownloadTask *downloadTask;

@property (nonatomic, strong) JYMCStyleMetaData *metaData;

#if TARGET_OS_IOS || TARGET_OS_TV
@property (assign, nonatomic) UIBackgroundTaskIdentifier backgroundTaskId;
#endif

@end

@implementation JYMCStyleDownloaderOperation

@synthesize executing = _executing;
@synthesize finished = _finished;

- (NSDictionary*)parsingURLParams:(NSURL*)url {
    NSMutableDictionary *params = [NSMutableDictionary new];
    NSArray<NSString*> *components = [url.query componentsSeparatedByString:@"&"];
    for (NSString *str in components) {
        NSArray<NSString*> *keyValArray = [str componentsSeparatedByString:@"="];
        if (keyValArray.count == 2) {
            params[keyValArray[0]] = [keyValArray[1] stringByURLDecode];
        }
    }
    return params;
}

- (instancetype)initWithURL:(NSURL*)url session:(NSURLSession*)session {
    self = [super init];
    if (self) {
        self.urlParams = [self parsingURLParams:url];
        _finished = NO;
        _executing = NO;
        self.tokens = [[NSMutableArray alloc] init];
        self.url = url;
        self.session = session;
#if TARGET_OS_IOS || TARGET_OS_TV
        _backgroundTaskId = UIBackgroundTaskInvalid;
#endif
    }
    return self;
}

- (id<JYMCStyleDownloadToken>)addHandlersForCompleted:(JYMCStyleDownloadCompletedBlock)completedBlock {
    id<JYMCStyleDownloadToken> token = [[JYMCStyleSimpleDownloadToken alloc] initWithURL:_url delegate:self completedBlock:completedBlock];
    @synchronized (self) {
        if (self.isCancelled || self.isFinished) {
            [self callResultBlock:(JYMCStyleSimpleDownloadToken*)token data:self.metaData error:[JYMCError errorWithCode:JYMCErrorCancelled localizedDescription:@"取消下载"]];
        } else {
            [self.tokens addObject:(JYMCStyleSimpleDownloadToken*)token];
        }
    }
    return token;
}


#pragma mark Override
- (void)start {
    @synchronized (self) {
        if (self.isCancelled) {
            if (!self.isFinished) self.finished = YES;
            [self reset];
            return;
        }
        self.downloadTask = [self.session downloadTaskWithURL:self.url];
        [self.downloadTask resume];
        self.executing = YES;
#if TARGET_OS_IOS || TARGET_OS_TV
        Class UIApplicationClass = NSClassFromString(@"UIApplication");
        BOOL hasApplication = UIApplicationClass && [UIApplicationClass respondsToSelector:@selector(sharedApplication)];
        if (hasApplication) {
            __weak typeof(self) wself = self;
            UIApplication * app = [UIApplicationClass performSelector:@selector(sharedApplication)];
            _backgroundTaskId = [app beginBackgroundTaskWithExpirationHandler:^{
                [wself cancel];
                [self closeBackgroundTask];
            }];
        }
#endif
    }
}

- (void)cancel {
    @synchronized (self) {
        if (self.isFinished) return;
        [super cancel];
        if (self.isExecuting || self.isFinished) {
            if (self.isExecuting) self.executing = NO;
            if (!self.isFinished) self.finished = YES;
        }
        if (self.downloadTask) {
            [self.downloadTask cancel];
            self.downloadTask = nil;
        }
        //调用block
        for (JYMCStyleSimpleDownloadToken *token in _tokens) {
            [self callResultBlock:token data:self.metaData error:[JYMCError errorWithCode:JYMCErrorCancelled localizedDescription:@"取消下载"]];
        }
        [self reset];
    }
}

- (void)done {
    @synchronized (self) {
        self.finished = YES;
        self.executing = NO;
        [self reset];
    }
}

- (void)setFinished:(BOOL)finished {
    [self willChangeValueForKey:@"isFinished"];
    _finished = finished;
    [self didChangeValueForKey:@"isFinished"];
}

- (void)setExecuting:(BOOL)executing {
    [self willChangeValueForKey:@"isExecuting"];
    _executing = executing;
    [self didChangeValueForKey:@"isExecuting"];
}

- (void)closeBackgroundTask {
#if TARGET_OS_IOS || TARGET_OS_TV
    if (_backgroundTaskId != UIBackgroundTaskInvalid) {
        UIApplication * app = [UIApplication performSelector:@selector(sharedApplication)];
        [app endBackgroundTask:self.backgroundTaskId];
        _backgroundTaskId = UIBackgroundTaskInvalid;
    }
#endif
}

- (void)reset {
    @synchronized (self) {
        [self.tokens removeAllObjects];
        self.downloadTask = nil;
        [self closeBackgroundTask];
    }
}

- (BOOL)isConcurrent {
    return YES;
}

- (void)callResultBlock:(JYMCStyleSimpleDownloadToken*)token data:(JYMCStyleMetaData*)data error:(NSError*)error {
    NSError *targetError = error;
    if (!data && !error) {
        targetError = [JYMCError errorWithCode:JYMCErrorDownloadFail localizedDescription:@"MD5 校验失败"];
    }
    if (targetError && ![targetError isKindOfClass:[JYMCError class]]) {
        targetError = [JYMCError errorWithCode:JYMCErrorDownloadFail localizedDescription:error.localizedDescription ? error.localizedDescription : @""];
    }
    dispatch_main_async_safe(^{
        token.completedBlock(data, targetError);
    });
}

#pragma mark JYMCStyleDownloaderResultHandler
- (void)handleDownloadResult:(NSError *)error {
    if (self.executing) {
        NSArray *tokens = nil;
        @synchronized (self) {
            tokens = [_tokens copy];
        }
        for (JYMCStyleSimpleDownloadToken *token in tokens) {
            [self callResultBlock:token data:self.metaData error:error];
        }
        [self done];
    }
}

- (void)handleDownloadData:(NSData *)data {
    NSString *urlMD5 = [_urlParams[@"md5"] lowercaseString];
    NSString *dataMD5 = [[data md5String] lowercaseString];
    BOOL md5Vaild = [urlMD5 isEqualToString:dataMD5];
    
    if (self.executing && data.length > 0 && md5Vaild) {
        NSString *content = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        JYMCStyleMetaData *metaData = [JYMCStyleMetaData new];
        metaData.content = content;
        NSString *guid = @"";
        NSArray<NSString*> *array = [[[_url pathComponents] lastObject] componentsSeparatedByString:@"."];
        if (array.count == 2) guid = array[0];
        metaData.guid = guid;
        metaData.url = _url.absoluteString;
        metaData.supportedVersion = _urlParams[@"supportVersion"];
        self.metaData = metaData;
    }
}


#pragma mark JYMCStyleDownloadTokenDelegate
- (BOOL)downloadIsCanceled {
    return self.isCancelled;
}


- (void)cancel:(id<JYMCStyleDownloadToken>)token {
    if (!token) return;
    BOOL shouldCancel = NO;
    @synchronized (self) {
        if ([_tokens containsObject:(JYMCStyleSimpleDownloadToken*)token]) {
            [_tokens removeObject:(JYMCStyleSimpleDownloadToken*)token];
            [self callResultBlock:(JYMCStyleSimpleDownloadToken*)token data:self.metaData error:[JYMCError errorWithCode:JYMCErrorCancelled localizedDescription:@"取消下载"]];
            if (![_tokens count]) {
                shouldCancel = YES;
            }
        }
    }
    if (shouldCancel) {
        [self cancel];
    }
}

@end
