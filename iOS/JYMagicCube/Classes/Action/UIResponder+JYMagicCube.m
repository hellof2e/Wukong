//
//  UIResponder+JYMagicCube.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/22.
//

#import "UIResponder+JYMagicCube.h"

@implementation UIResponder (JYMagicCube)

- (BOOL)mc_didStartHandleAction:(JYMCActionInternalContext *)context {
    return [[self nextResponder] mc_didStartHandleAction:context];
}

- (BOOL)mc_didStartHandleTrack:(JYMCTrackContext *)context {
    return [[self nextResponder] mc_didStartHandleTrack:context];
}

- (void)mc_didActionFinished:(JYMCActionContext *)context {
    [[self nextResponder] mc_didActionFinished:context];
}

- (void)mc_didActionTimerTick:(JYMCActionContext *)context {
    [[self nextResponder] mc_didActionTimerTick:context];
}

- (void)mc_didStartJSAction:(JYMCActionInternalContext *)context {
    [[self nextResponder] mc_didStartJSAction:context];
}

@end
