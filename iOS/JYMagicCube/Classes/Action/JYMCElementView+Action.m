//
//  JYMCElementView+Action.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView+Action.h"
#import "JYMCAction.h"
#import "JYMCActionClick.h"
#import "JYMCTracker.h"
#import "JYMCTrackActionInfo.h"
#import "JYMCDataParser.h"
#import "UIResponder+JYMagicCube.h"
#import "JYMCElement.h"
#import "JYMCTrackContext.h"
#import "JYMCError.h"
#import "JYMCJSExpressionParser.h"
#import "JYMCActionInternalContext.h"
#import <MJExtension/MJExtension.h>
#import <YYCategories/YYCategories.h>
#import "JYMCStateInfo.h"
#import "JYMCElementView+Style.h"
#import "JYMCCommonUtils.h"


/// 连续点击间隔时间
const NSTimeInterval KJYMCActionTimeInterval = 0.5;

@interface JYMCElementView ()
@property (nonatomic, assign) NSTimeInterval actionTimeInterval;
@end


@implementation JYMCElementView (Action)

#pragma mark - Public

- (void)action_applyAction:(JYMCAction *)action info:(JYMCStateInfo *)info {
    for (UIGestureRecognizer *recognizer in self.gestureRecognizers) {
        [self removeGestureRecognizer:recognizer];
    }
    if (action.click) {
        NSString *url = [JYMCDataParser getStringValueWithString:action.click.url info:info];
        if (url.length > 0) {
            [self handleClickAction:action info:info];
        }
    } else if (action.clickEvent) {
        [self handleClickAction:action info:info];
    }
    
    [self _parseActionExpose:action.expose info:info];
}

#pragma mark - Private

/// 处理点击事件
- (void)handleClickAction:(JYMCAction *)action info:(JYMCStateInfo *)info {
    if (!self.isUserInteractionEnabled) {
        self.userInteractionEnabled = YES;
    }
    
    // 添加手势
    @weakify(self)
    dispatch_block_t tapClick = ^{
        @strongify(self)
        self.actionTimeInterval = CACurrentMediaTime();
        [self _handleClickReport:action.click info:info];
        JYMCActionInternalContext *context = [self createActionContext:action info:info];
        [self _handleClickAction:context info:info];
    };
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithActionBlock:^(id  _Nonnull sender) {
        @strongify(self)
        if (CACurrentMediaTime() - self.actionTimeInterval < KJYMCActionTimeInterval) {
            return;
        }
        tapClick();
        if (self.element.useActive) {
            [self style_applyActiveStyle];
            [self showNormalStyleDelay];
        }
    }];
    [self addGestureRecognizer:tap];
    
    if (self.element.useActive) {
        @weakify(self)
        UILongPressGestureRecognizer *longRest = [[UILongPressGestureRecognizer alloc] initWithActionBlock:^(UILongPressGestureRecognizer* sender) {
            @strongify(self)
            switch (sender.state) {
                case UIGestureRecognizerStateBegan: {
                    [NSObject cancelPreviousPerformRequestsWithTarget:self
                                                             selector:@selector(style_applyNormalStyle)
                                                               object:nil];
                    [self style_applyActiveStyle];
                    break;
                }
                case UIGestureRecognizerStateChanged: {
                    break;
                }
                case UIGestureRecognizerStateEnded: {
                    if ([self isTapHitInView:sender]) {
                        tapClick();
                        [self showNormalStyleDelay];
                    } else {
                        [self style_applyNormalStyle];
                    }
                    break;
                }
                default:
                    [self style_applyNormalStyle];
                    break;
            }
        }];
        longRest.minimumPressDuration = 0.1;
        [self addGestureRecognizer:longRest];
        [tap requireGestureRecognizerToFail:longRest];
    }
}

/// 创建点击事件上下文
/// @param action 事件对象
/// @param info 数据
- (JYMCActionInternalContext *)createActionContext:(JYMCAction *)action
                                              info:(JYMCStateInfo *)info
{
    NSString *url = [JYMCDataParser getStringValueWithString:action.click.url info:info];
    JYMCActionInternalContext *context = [[JYMCActionInternalContext alloc] init];
    context.url = url;
    
    // 解析js事件
    if (action.clickEvent.length > 0) {
        context.js = [JYMCJSExpressionParser getJSActionWithString:action.clickEvent info:info];
    }
    
    return context;
}

/// 处理点击埋点事件
/// @param click click 事件
- (void)_handleClickReport:(JYMCActionClick *)click info:(JYMCStateInfo *)info
{
    if (!click.report) {
        return;
    }
    
    NSDictionary *busInfo = [JYMCDataParser getDictionaryValueWithIdData:click.report.busInfo info:info];
    JYMCTrackContext *trackContext = [[JYMCTrackContext alloc] init];
    trackContext.type = JYMCTrackTypeClick;
    trackContext.busInfo = busInfo;
    
    if ([self mc_didStartHandleTrack:trackContext]) {
        return;
    }
    
    JYMCTrackActionInfo *actionInfo = [[JYMCTrackActionInfo alloc] init];
    actionInfo.busInfo = busInfo;
    [JYMCTracker trackForAction:actionInfo];
}

// 处理跳转事件
- (void)_handleClickAction:(JYMCActionInternalContext *)context info:(JYMCStateInfo *)info
{
    switch (context.type) {
        case JYMCActionClickTypeJump:
            [self handleJumpAction:context];
            break;
        case JYMCActionClickTypeJS:
            [self handleJSAction:context info:info];
            break;
        default:
            return;
    }
}

// 处理js事件
- (void)handleJSAction:(JYMCActionInternalContext *)context info:(JYMCStateInfo *)info
{
    [self mc_didStartJSAction:context];
}

// 处理跳转事件
- (void)handleJumpAction:(JYMCActionInternalContext *)context
{
    if ([self mc_didStartHandleAction:context]) {
        return;
    }
    [JYMCCommonUtils navigator:context.url];
}

- (BOOL)isTapHitInView:(UIGestureRecognizer *)sender {
    CGPoint point = [sender locationInView:self];
    return CGRectContainsPoint(self.bounds, point);
}

- (void)showNormalStyleDelay {
    [self performSelector:@selector(style_applyNormalStyle) withObject:nil afterDelay:0.12];
}

#pragma mark - 曝光交互
- (void)_parseActionExpose:(JYMCActionExpose *)expose info:(JYMCStateInfo *)info {
    JYMCTrackActionExposeInfo *expInfo = [[JYMCTrackActionExposeInfo alloc] init];
    expInfo.busInfo = [JYMCDataParser getDictionaryValueWithIdData:expose.report.busInfo info:info];
    [info.exposeInfos addObject:expInfo];
}

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (self.window) {
        [self action_applyexpose];
    }
}

- (void)action_applyexpose {
    NSMutableArray* loadedItems = [[NSMutableArray alloc] init];
    for (JYMCTrackActionExposeInfo* item in self.stateInfo.exposeInfos) {
        if (nil == item.view.window) {
            continue;
        }
        [JYMCTracker trackForAction:item];
        [loadedItems addObject:item];
    }
    [self.stateInfo.exposeInfos removeObjectsInArray:loadedItems];
}

@end
