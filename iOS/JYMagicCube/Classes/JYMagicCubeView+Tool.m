//
//  JYMagicCubeView+Tool.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/24.
//

#import "JYMagicCubeView+Tool.h"
#import "JYMCElementView.h"
#import "JYMCElement.h"
#import "JYMCDataParser.h"
#import "JYMCStyleMetaData.h"
#import "JYMagicCubeMacro.h"
#import "JYMCElementView+Action.h"

@interface JYMagicCubeView ()
@property (nonatomic, strong) JYMCElementView *contentView;
@property (nonatomic, strong) JYMCStyleMetaData *currentStyleMetaData;
@end

@implementation JYMagicCubeView (Tool)

- (BOOL)isSameStyle:(NSString *)styleContent {
    JYMCStyleMetaData *styleMetaData = [JYMCStyleMetaData new];
    styleMetaData.content = styleContent;
    return [styleMetaData isEqual:self.currentStyleMetaData];
}

- (void)jsTimeoutWithInterval:(NSTimeInterval)interval callback:(dispatch_block_t)callback {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSTimeInterval i = interval / 1000.0;
        [self performSelector:@selector(jsTimeoutAction:) withObject:callback afterDelay:i inModes:@[NSRunLoopCommonModes]];
    });
}

- (void)destructAllRes {
    [self cancelJSTimeout];
}

- (void)trackExpose {
    dispatch_main_async_safe(^{
        [self.contentView action_applyexpose];
    });
}

- (CGFloat)widthInStyle {
    return [JYMCDataParser getFloatPixelWithString:self.contentView.element.layout.width];
}

#pragma mark - Private

- (void)jsTimeoutAction:(dispatch_block_t)block {
    !block ?: block();
}

- (void)cancelJSTimeout {
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

+ (CGFloat)parseValue:(NSString *)value {
    return [JYMCDataParser getFloatPixelWithString:value];
}

@end
