//
//  JYMagicCubeView.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import <UIKit/UIKit.h>
#import "JYMagicCubeView.h"

NS_ASSUME_NONNULL_BEGIN

@class JYMCStyleMetaData;
@class JYMCLoadingSession;
@class JYMCMetaData;

@interface JYMagicCubeView(V2)

- (void)_loadWithMetaData:(JYMCMetaData *)metaData
               completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock;

- (void)_loadWithStyleURLV2:(NSURL *)url
                       data:(NSDictionary *)data
                placeholder:(UIView *)placeholder
                 completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock;

- (void)_ayncRenderWithStyle:(JYMCStyleMetaData *)style
                        data:(NSDictionary *)data
              loadingSession:(JYMCLoadingSession *)loadingSession
                  completion:(JYMagicCubeViewLoadCompletionBlock)completionBlock;

- (void)_asynRenderSameStyleWithData:(NSDictionary *)data
                      loadingSession:(JYMCLoadingSession *)loadingSession
                          completion:(JYMagicCubeViewLoadCompletionBlock _Nullable)completionBlock;
@end

NS_ASSUME_NONNULL_END
