//
//  JYMCStyleDownloader.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/4/25.
//

#import "JYMCStyleDownloader.h"
#import "JYMCStyleDownloaderConfig.h"
#import "JYMCStyleDownloaderOperation.h"

@interface JYMCStyleDownloader ()<NSURLSessionDelegate,NSURLSessionDownloadDelegate>

@property (strong, nonatomic, nonnull) NSOperationQueue *downloadQueue;

@property (nonatomic, strong) NSMutableDictionary<NSURL*, JYMCStyleDownloaderOperation*> *urlToOperation;

@property (nonatomic, strong) JYMCStyleDownloaderConfig *config;

@property (strong, nonatomic) NSURLSession *session;

@end

@implementation JYMCStyleDownloader

- (instancetype)initWithConfig:(JYMCStyleDownloaderConfig*)config {
    self = [super init];
    if (self) {
        self.config = config;
        _downloadQueue = [[NSOperationQueue alloc] init];
        _downloadQueue.maxConcurrentOperationCount = config.maxConcurrentDownloads;
        _downloadQueue.name = @"com.magiccube.downloader";
        _urlToOperation = [[NSMutableDictionary alloc] init];
        NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
        sessionConfiguration.timeoutIntervalForRequest = config.downloadTimeout;
        _session = [NSURLSession sessionWithConfiguration:sessionConfiguration delegate:self delegateQueue:nil];
    }
    return self;
}

- (void)dealloc {
    [self.downloadQueue cancelAllOperations];
    [self.session invalidateAndCancel];
    self.session = nil;
}

- (void)cancelAllDownloads {
    [self.downloadQueue cancelAllOperations];
}

+ (instancetype)sharedInstance {
    static JYMCStyleDownloader *sharedObject;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedObject = [[JYMCStyleDownloader alloc] initWithConfig:[JYMCStyleDownloaderConfig defaultDownloaderConfig]];
    });
    return sharedObject;
}

- (id<JYMCStyleDownloadToken>)downloadStyleWithURL:(NSURL *)url completed:(JYMCStyleDownloadCompletedBlock)completedBlock {
    if (!url) return nil;
    id<JYMCStyleDownloadToken> token;
    @synchronized (self) {
        JYMCStyleDownloaderOperation *operation = self.urlToOperation[url];
        if (!operation) {
            operation = [[JYMCStyleDownloaderOperation alloc] initWithURL:url session:_session];
            _urlToOperation[url] = operation;
            [_downloadQueue addOperation:operation];
        }
        token = [operation addHandlersForCompleted:completedBlock];
    }
    return token;
}

- (NSUInteger)currentDownloadCount {
    return _downloadQueue.operationCount;
}

#pragma mark NSURLSessionDelegate
- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didFinishDownloadingToURL:(NSURL *)location {
    NSData *data = [NSData dataWithContentsOfURL:location];
    id<JYMCStyleDownloaderResultHandler> handler = _urlToOperation[downloadTask.currentRequest.URL];
    [handler handleDownloadData:data];
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    id<JYMCStyleDownloaderResultHandler> handler = _urlToOperation[task.currentRequest.URL];
    [handler handleDownloadResult:error];
    @synchronized (self) {
        [_urlToOperation removeObjectForKey:task.currentRequest.URL];
    }
}

@end
