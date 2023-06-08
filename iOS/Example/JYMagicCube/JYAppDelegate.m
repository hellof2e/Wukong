//
//  JYAppDelegate.m
//  JYMagicCube
//
//  Created by 姜灿明 on 10/09/2020.
//

#import "JYAppDelegate.h"
// adapter
#import "JYTestTrackAdapter.h"
#import "JYWKJSEngineLogAdapter.h"
#import "JYWKJSEngineCacheAdapter.h"
#import "JYWKJSEngineEventAdapter.h"
#import "JYWKJSEngineLocalAdapter.h"
#import "JYWKJSEngineToastAdapter.h"
#import "JYWKJSEngineTrackAdapter.h"
// export handler
#import "JYTestPayExportHandler.h"
#import "JYTestShareExportHandler.h"

#import "JYDemoPreviewSingleViewController.h"
#import <JYMagicCube/JYMagicCube.h>
@import JYWKJSEngine;

@implementation JYAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:[JYDemoPreviewSingleViewController new]];
    self.window.rootViewController = nav;
    [self.window makeKeyAndVisible];
    
    // 初始化SDK
    [self setupJSEngine];
    [self setupMagicCube];
    
    return YES;
}

- (void)setupMagicCube {
    [JYMCConfigure initWithAppName:@"JYMagicCubeDemo" appVersion:[UIDevice currentDevice].systemVersion];
    [JYMCConfigure setTrackAdapter:[JYTestTrackAdapter new]];
    [JYMCConfigure setGlobalActionHandler:^(JYMCActionContext * _Nonnull actionContext) {
        NSLog(@"全局事件处理，url = %@", actionContext.url);
    }];
    [JYMCConfigure setLocalParametersHandler:^(JYMCLocalParameters * _Nonnull parameters) {
        parameters.userToken = @"用户标识";
        parameters.cityCode = @"021";
        parameters.adCode = @"310112";
    }];
}

- (void)setupJSEngine {
    [JYWKAdapterManager sharedInstance].logAdapter = [JYWKJSEngineLogAdapter new];
    [JYWKAdapterManager sharedInstance].toastAdapter = [JYWKJSEngineToastAdapter new];
    [JYWKAdapterManager sharedInstance].localAdapter = [JYWKJSEngineLocalAdapter new];
    [JYWKAdapterManager sharedInstance].trackAdapter = [JYWKJSEngineTrackAdapter new];
    [JYWKAdapterManager sharedInstance].eventAdapter = [JYWKJSEngineEventAdapter new];
    [JYWKAdapterManager sharedInstance].cacheAdapter = [JYWKJSEngineCacheAdapter new];
    [[JYWKAdapterManager sharedInstance] registerGlobalObjWithKey:@"CorePay" value:[JYTestPayExportHandler new]];
    [[JYWKAdapterManager sharedInstance] registerInternalObjWithKey:@"CoreShare" value:[JYTestShareExportHandler new]];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
