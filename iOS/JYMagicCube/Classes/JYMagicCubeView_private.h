//
//  JYMagicCubeView.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import "JYMagicCubeView.h"

NS_ASSUME_NONNULL_BEGIN

@class JYMCLoadingSession;
@protocol JYMCStyleOperation;
@class JYMCStyleMetaData;
@class JYMCLoadingSession;
@class JYMCError;

@interface JYMagicCubeView(Private)

- (void)loadWithStyleURL:(nonnull NSURL *)url
                    data:(nullable NSDictionary *)data
              completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;

- (void)cancelCurrentOperation;
- (void)updateCurrentOperation:(id<JYMCStyleOperation>)operation;
- (void)resetViewWithPlaceholder:(UIView * _Nullable)placeholder;
- (JYMCLoadingSession *)createLoadingSession:(NSURL * _Nullable)url;
- (void)renderSameStyleWithData:(NSDictionary *)data
                 loadingSession:(JYMCLoadingSession * _Nullable)loadingSession
                     completion:(JYMagicCubeViewLoadCompletionBlock _Nullable)completionBlock;

/// 加载并渲染样式数据
/// @param style 样式
/// @param data 样式数据
/// @param loadingSession 加载上下文
/// @param completionBlock 结束回调
- (void)renderWithStyleV2:(JYMCStyleMetaData *)style
                     data:(NSDictionary *)data
           loadingSession:(nullable JYMCLoadingSession *)loadingSession
               completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;

- (void)loadFinishWithURL:(NSURL * _Nullable)url
                    error:(NSError * _Nullable)error
                    block:(JYMagicCubeViewLoadCompletionBlock _Nullable)block;

- (void)loadWithStyle:(NSString *)style
                 data:(NSDictionary *)data
           completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;


- (void)changeData:(NSDictionary *)data needRefresh:(BOOL)needRefresh;

+ (JYMCError *)_checkStyleLoadParams:(NSURL *)url data:(NSDictionary *)data;

@end

NS_ASSUME_NONNULL_END
