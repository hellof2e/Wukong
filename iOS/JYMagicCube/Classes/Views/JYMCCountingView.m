//
//  JYMCCountingView.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/24.
//

#import "JYMCCountingView.h"
#import "JYMCCountingElement.h"
#import <YogaKit/UIView+Yoga.h>
#import "JYMCDataParser.h"
#import "JYMCCountingTimer.h"
#import "UIResponder+JYMagicCube.h"
#import "JYMCError.h"
#import "JYMCStateInfo.h"
#import "JYMCJSExpressionParser.h"

@interface JYMCCountingView ()
@property (nonatomic, strong) JYMCCountingTimer* timer;
@property (nonatomic, assign) NSInteger startTimerCount;
@end

@implementation JYMCCountingView
- (void)dealloc {
    [self.timer stopTimer];
    self.timer = nil;
}

- (void)didMoveToSuperview {
    [super didMoveToSuperview];
    if (!self.superview) {
        [self.timer stopTimer];
        self.timer = nil;
    }
}

- (void)updateInfo:(JYMCStateInfo *)info {
    self.stateInfo = info.copy;
    [self.timer stopTimer];
    self.timer = nil;

    self.timer = [JYMCCountingTimerFactory createTimer:self.element info:info];
    NSDictionary* mData = [self mergeTimerData];
    [self.stateInfo updateData:mData];
    [super updateInfo:self.stateInfo];
    self.startTimerCount = self.startTimerCount + 1;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self startTimer];
    });
}


- (NSDictionary *)mergeTimerData {
    NSDictionary* newData = self.stateInfo.data;
    if (!self.timer.isListItem) {
        NSMutableDictionary* data = [self.stateInfo.data mutableCopy];
        data[@"MC_TIMER"] = self.timer.infoData;
        newData = data.copy;
    } else {
        //listItem
        NSMutableDictionary* data = [self.stateInfo.listItem mutableCopy];
        data[@"MC_TIMER"] = self.timer.infoData;
        self.stateInfo.scopeState.listItem = data;
    }
    return newData;
}


- (void)startTimer {
    if (self.startTimerCount <= 0) {
        return;
    }
    self.startTimerCount = 0;
    __weak typeof(self) wself = self;
    BOOL ret = [self.timer startWithFinishBlock:^{
        [wself timerDidFinish:nil];
    } stepBlock:^{
        [wself timerTick];
    }];
    
    if (!ret) {
        JYMCError *error = [JYMCError errorWithCode:JYMCErrorCountingOut localizedDescription:@"倒计时过期"];
        [self timerDidFinish:error];
    }
}

- (void)timerDidFinish:(NSError *)error {
    JYMCCountingElement* element = (JYMCCountingElement*)self.element;
    if (element.action.countingFinishEvent.length > 0 && error == nil) {
        JYMCActionInternalContext *context = [[JYMCActionInternalContext alloc] init];
        context.js = [JYMCJSExpressionParser getJSActionWithString:element.action.countingFinishEvent info:self.stateInfo];
        [self mc_didStartJSAction:context];
    } else {
        JYMCActionContext *externalContext = [[JYMCActionContext alloc] init];
        externalContext.scopeData = self.stateInfo.listItem;
        externalContext.actionName = JYMCActionCounterName;
        externalContext.error = error;
        [self mc_didActionFinished:externalContext];

    }
}

- (void)timerTick {
    NSDictionary* newData = [self mergeTimerData];
    [self.stateInfo updateData:newData];
    [super updateInfo:self.stateInfo];
    JYMCActionContext *externalContext = [[JYMCActionContext alloc] init];
    externalContext.scopeData = self.stateInfo.listItem;
    externalContext.actionName = JYMCActionCounterName;
    
    JYMCCountingElement* element = (JYMCCountingElement*)self.element;
    if (element.needRefreshContent) {
        [self mc_didActionTimerTick:externalContext];
    }
}


@end
