//
//  JYMagicAlertLoader.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/17.
//

#import "JYMagicAlertLoader.h"
#import "JYMagicAlertViewController.h"
#import "JYMagicAlertDelegate.h"
#import "JYMagicAlertLoaderDelegate.h"

@interface JYMagicAlertLoader () <JYMagicAlertDelegate>

@property (nonatomic, strong) JYMagicAlertViewController *magicAlertVC;

@property (nonatomic, weak) id<JYMagicAlertLoaderDelegate> delegate;
@property (nonatomic, copy) JYMagicAlertLoaderCompletionBlock complete;

@end

@implementation JYMagicAlertLoader

/// 创建弹框加载器
/// @param delegate 事件代理
/// @param url 样式链接
/// @param data 数据
/// @param config 弹框配置信息
+ (id<JYMagicAlertLoaderProtocol>)loaderAlertWithDelegate:(id<JYMagicAlertLoaderDelegate>)delegate
                                                      url:(NSString *)url
                                                     data:(NSDictionary *)data
                                                   config:(JYMCAlertConfig *)config {
    JYMagicAlertLoader *loader = [[JYMagicAlertLoader alloc] init];
    loader.delegate = delegate;
    loader.magicAlertVC = [JYMagicAlertViewController createAlertWithDelegate:loader url:url data:data config:config];
    
    return loader;
}

/// 创建弹框加载器
/// @param url 样式链接
/// @param data 数据
/// @param config 弹框配置信息
/// @param complete 加载完成回调
+ (id<JYMagicAlertLoaderProtocol>)loaderAlertWithUrl:(nonnull NSString *)url
                                                data:(nonnull NSDictionary *)data
                                              config:(nonnull JYMCAlertConfig *)config
                                            complete:(JYMagicAlertLoaderCompletionBlock)complete {
    JYMagicAlertLoader *loader = [[JYMagicAlertLoader alloc] init];
    loader.complete = complete;
    loader.magicAlertVC = [JYMagicAlertViewController createAlertWithDelegate:loader url:url data:data config:config];
    
    return loader;
}

#pragma mark -- JYMagicAlertDelegate
/// DSL弹框加载成功
- (void)magicAlertDidLoadSuccess:(JYMagicAlertViewController *)magicAlert {
    if ([self.delegate respondsToSelector:@selector(loadMagicAlertDidSuccess:alert:)]) {
        [self.delegate loadMagicAlertDidSuccess:self alert:self.magicAlertVC];
    }
    if (self.complete) {
        self.complete(YES, self.magicAlertVC, nil);
    }
}

/// DSL弹框加载失败
- (void)magicAlert:(JYMagicAlertViewController *)magicAlert didLoadFailWithError:(NSError *)error {
    if ([self.delegate respondsToSelector:@selector(loadMagicAlert:failWithError:)]) {
        [self.delegate loadMagicAlert:self failWithError:error];
    }
    if (self.complete) {
        self.complete(NO, nil, error);
    }
}

@end
