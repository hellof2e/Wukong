//
//  JYWKJSEngineEventAdapter.m
//  JYPlatformMarketingModule
//
//  Created by gaoshuaibin091 on 2022/8/23.
//

#import "JYWKJSEngineEventAdapter.h"
#import "JYWKJSEngineAlertInfo.h"

#import <AFNetworking/AFHTTPSessionManager.h>
#import <YYCategories/YYCategories.h>
#import <MJExtension/MJExtension.h>
#import <JYMagicCube/JYMagicCubeView+Tool.h>
#import <JYMagicCube/JYMCStyleManager.h>
#import <JYMagicCube/JYMagicAlert.h>

@interface JYWKJSEngineEventAdapter () <JYMagicAlertLoaderDelegate, JYMagicAlertLifeCycleDelegate>

@property (nonatomic, strong) id<JYMagicAlertLoaderProtocol> alertLoader;
@property (nonatomic, strong) id<JYMagicAlertProtocol> magicAlert;
@property (nonatomic, copy) void (^DSLAlertShowBlock)(void);
@property (nonatomic, copy) void (^DSLAlertDismissBlock)(void);
@property (nonatomic, copy) void (^DSLAlertActionBlock)(NSDictionary<NSString *,id> * _Nullable);

@end

@implementation JYWKJSEngineEventAdapter

#pragma mark - Router
- (void)navigator:(NSString * _Nullable)urlString {
    // router
    [self openUrl:urlString];
}

#pragma mark - Network
- (void)requestWithType:(NSString * _Nonnull)type
                    url:(NSString * _Nullable)url
                 params:(NSDictionary<NSString *,id> * _Nullable)params
                success:(void (^ _Nullable)(NSDictionary<NSString *,id> * _Nullable))success
                failure:(void (^ _Nullable)(NSError * _Nonnull))failure {
    if ([type isEqualToString:@"GET"]) {
        AFHTTPSessionManager *sessionManager = [AFHTTPSessionManager manager];
        [sessionManager GET:url parameters:nil headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id _Nullable responseObject) {
            if (success) success(responseObject);
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            if (failure) failure(error);
        }];
    } else if ([type isEqualToString:@"POST"]) {
        AFHTTPSessionManager *sessionManager = [AFHTTPSessionManager manager];
        [sessionManager POST:url parameters:params headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id _Nullable responseObject) {
            if (success) success(responseObject);
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            if (failure) failure(error);
        }];
    }
}

#pragma mark - Timeout
- (void)timeoutWithDelegate:(id)delegate interval:(int32_t)interval callback:(void (^)(void))callback {
    if (interval <= 0) {
        !callback ?: callback();
        return;
    }
    
    if ([delegate isKindOfClass:JYMagicCubeView.class]) {
        JYMagicCubeView *magicView = (JYMagicCubeView *)delegate;
        [magicView jsTimeoutWithInterval:interval callback:callback];
    }
}

#pragma mark - Preload DSL Style
- (void)preloadDSLStyle:(NSString *)url {
    [[JYMCStyleManager shareManager] loadStyleWithURL:[NSURL URLWithString:url] completion:^(JYMCStyleMetaData * _Nullable styleMetaData, NSError * _Nullable error, JYMCStyleCacheType cacheType, NSURL * _Nullable styleURL) {
        
    }];
}

#pragma mark - DSL Alert
- (void)showDSLAlertWithDelegate:(id)delegate
                          params:(NSDictionary<NSString *,id> *)params
                            show:(void (^)(void))show
                         dismiss:(void (^)(void))dismiss
                        callback:(void (^)(NSDictionary<NSString *,id> * _Nullable))callback {
    JYWKJSEngineAlertInfo *alertInfo = [JYWKJSEngineAlertInfo mj_objectWithKeyValues:params];
    
    self.DSLAlertShowBlock = show;
    self.DSLAlertDismissBlock = dismiss;
    self.DSLAlertActionBlock = callback;
    
    self.alertLoader = [JYMagicAlertLoader loaderAlertWithDelegate:self url:alertInfo.style data:alertInfo.data config:[alertInfo alertConfig]];
}

- (void)dismissDSLAlert {
    [self.magicAlert dismissAnimated:YES];
}

#pragma mark - JYMagicAlertLoaderDelegate
/// DSL弹框加载成功
- (void)loadMagicAlertDidSuccess:(JYMagicAlertLoader *)alertLoader alert:(id<JYMagicAlertProtocol>)magicAlert {
    if (![magicAlert isShowing]) {
        self.magicAlert = magicAlert;
        [magicAlert configLifeCycleDelegate:self];
        [magicAlert show:[self topViewController] animated:YES];
    }
}

/// DSL弹框加载失败
- (void)loadMagicAlert:(JYMagicAlertLoader *)alertLoader failWithError:(NSError *)error {
    
}

#pragma mark - JYMagicAlertLifeCycleDelegate
/// DSL弹框展示成功回调
- (void)magicAlertDidShow:(id<JYMagicAlertProtocol>)magicAlert {
    !self.DSLAlertShowBlock ?: self.DSLAlertShowBlock();
}

/// DSL弹框展示失败回调
- (void)magicAlertShowFailure:(id<JYMagicAlertProtocol>)magicAlert error:(NSError *)error {
    
}

/// DSL弹框消失
- (void)magicAlertDidDismiss:(id<JYMagicAlertProtocol>)magicAlert {
    !self.DSLAlertDismissBlock ?: self.DSLAlertDismissBlock();
    [self clearDSLAlert];
}

/// DSL弹框js交互事件
- (void)magicAlert:(id<JYMagicAlertProtocol>)magicAlert jsCallNative:(NSDictionary<NSString *,id> *)params {
    !self.DSLAlertActionBlock ?: self.DSLAlertActionBlock(params);
}

#pragma mark - Private
- (void)openUrl:(NSString *)urlString {
    NSURL *url = [NSURL URLWithString:urlString];
    [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:nil];
}

- (void)clearDSLAlert {
    self.DSLAlertActionBlock = nil;
    self.DSLAlertShowBlock = nil;
    self.DSLAlertDismissBlock = nil;
    self.alertLoader = nil;
    self.magicAlert = nil;
}

- (UIViewController *)topViewController {
    return nil;
}

@end
