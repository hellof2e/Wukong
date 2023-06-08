//
//  JYMCProload.h
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/9/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class JYMCMetaData;
@class JYMCStyleMetaData;

@interface JYMCPreloadRequest : NSObject

@property (nonatomic, copy)   NSURL *styleURL;
@property (nonatomic, copy)   NSDictionary *data;
@property (nonatomic, weak) id externalModel;
@property (nonatomic, assign) BOOL needCache;

+ (instancetype)requestWithStyleURL:(NSURL *)url data:(NSDictionary *)data;

@end

@interface JYMCPreloadResponse : NSObject

@property (nonatomic, strong, readonly) JYMCPreloadRequest *request;
@property (nonatomic, strong, readonly) JYMCMetaData *metaData;
@property (nonatomic, strong, readonly) NSError *error;

@end

typedef void(^JYMCPreloadCompletionBlock)(NSArray<JYMCPreloadResponse *>* responses);

@interface JYMCPreload : NSObject

/// 预加载
/// @param styleURL 样式链接
/// @param data 数据
/// @param completionBlock 加载完成后，回调block
+ (void)preloadWithStyleURL:(NSURL *)styleURL
                       data:(NSDictionary *)data
                 completion:(nullable void (^)(JYMCPreloadResponse * response))completionBlock;


/// 预加载
/// @param requests 预加载请求组
/// @param completionBlock 加载完成后，回调block
+ (void)preloadWithRequsts:(NSArray<JYMCPreloadRequest *> *)requests
                completion:(nullable JYMCPreloadCompletionBlock)completionBlock;


/// 删除预加载数据
/// @param styleURL 样式链接
/// @param data 数据
+ (void)removePreloadMetaDataWithStyleURL:(NSURL *)styleURL
                                     data:(NSDictionary *)data;


/// 获取预加载数据
/// @param styleURL 样式链接
/// @param data 数据
+ (JYMCMetaData *)fetchPreloadMetaDataWithStyleURL:(NSURL *)styleURL
                                              data:(NSDictionary *)data;

@end

NS_ASSUME_NONNULL_END
