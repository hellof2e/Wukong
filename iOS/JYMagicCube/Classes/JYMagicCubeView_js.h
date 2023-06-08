//
//  JYMagicCubeView_js.h
//  JYMagicCube
//
//  Created by 张新令 on 2022/9/6.
//

#import "JYMagicCubeView.h"
NS_ASSUME_NONNULL_BEGIN

@class JYMCError;
@class JYMCLoadingSession;
@class JYWKJSContext;
@class JYMCActionInternalContext;

@interface JYMagicCubeView(js)
- (void)execPraeviaScriptIfNeed:(JYMCLoadingSession *)session
                           data:(NSDictionary *)originData
                     completion:(void (^)(NSDictionary *data, JYMCError * _Nullable error))completion;

- (void)_wkjs_setState:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext;
- (void)_wkjs_callNative:(NSDictionary<NSString *,id> *)params jsContext:(JYWKJSContext *)jsContext;
- (void)callJS:(JYMCActionInternalContext *)context arguments:(NSArray *)arguments;

// 通过缓存加载需要更新jsContent 并设置代理
- (void)updateJSContent:(JYWKJSContext *)jsContext;
// 执行onCreated js方法
- (void)execLifeCycleOnCreatedFuncIfNeed;
// 执行onUpdated js方法
- (void)execLifeCycleOnUpdatedFuncIfNeed;

@end

NS_ASSUME_NONNULL_END
