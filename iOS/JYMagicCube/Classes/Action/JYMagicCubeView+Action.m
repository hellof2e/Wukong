//
//  JYMagicCubeView+Action.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/6/27.
//

#import "JYMagicCubeView+Action.h"
#import "JYMCStyleMetaData.h"
#import "JYMCActionInternalContext.h"
#import "JYMCLoadingSession.h"
#import "JYMCElementView.h"
#import "JYMagicCubeView_js.h"
#import "JYMagicCubeView_private.h"

@import JYWKJSEngine;

@interface JYMagicCubeView ()
@property (nonatomic, strong) JYWKJSContext *jsContext;
@property (nonatomic, strong) JYMCLoadingSession *loadingSession;
@property (nonatomic, strong) JYMCStyleMetaData *currentStyleMetaData;
@property (nonatomic, strong) JYMCElementView *contentView;

@end

@implementation JYMagicCubeView (Action)

- (BOOL)mc_didStartHandleAction:(JYMCActionInternalContext *)context
{
    JYMCActionContext *externalContext = [[JYMCActionContext alloc] init];
    externalContext.url = context.url;
    
    // 判断接入方是否处理点击回调
    if ([self.delegate respondsToSelector:@selector(magicCubeView:didStartInterceptHandleAction:)]) {
        return [self.delegate magicCubeView:self didStartInterceptHandleAction:externalContext];
    }
    
    return NO;
}

- (BOOL)mc_didStartHandleTrack:(JYMCTrackContext *)context
{
    if ([self.delegate respondsToSelector:@selector(magicCubeView:didStartHandleTrack:)]) {
        return [self.delegate magicCubeView:self didStartHandleTrack:context];
    }
    return NO;
}

- (void)mc_didActionFinished:(JYMCActionContext *)context {
    // 如果loadingSession不为空，表示本次为加载渲染，纯计算高度loadingSession为空
    if (self.loadingSession && [self.delegate respondsToSelector:@selector(magicCubeView:didActionFinished:)]) {
        [self.delegate magicCubeView:self didActionFinished:context];
    }
}

- (void)mc_didActionTimerTick:(JYMCActionContext *)context {
    [self.contentView refreshLayout];
}

- (void)mc_didStartJSAction:(JYMCActionInternalContext *)context {
    [self callJS:context arguments:context.js.args];
}

@end
