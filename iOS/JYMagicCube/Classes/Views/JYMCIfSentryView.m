//
//  JYMCIfSentryView.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/14.
//

#import "JYMCIfSentryView.h"
#import "JYMCJSExpressionParser.h"
#import "JYMCElement.h"
#import <YogaKit/UIView+Yoga.h>
#import "JYMCElementView+Layout.h"
#import "JYMCStateInfo.h"

@interface JYMCIfSentryView ()

@property (nonatomic, strong) JYMCElementView *ifElementView;
@property (nonatomic, strong) JYMCElement *mIfViewElement;
@end

@implementation JYMCIfSentryView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.hidden = YES;
        self.yoga.isIncludedInLayout = NO;
    }
    return self;
}

- (__kindof JYMCElement *)element {
    return _mIfViewElement;
}

#pragma mark - Public

- (void)applyElement:(__kindof JYMCElement *)element {
    _mIfViewElement = element;
}

- (void)updateInfo:(JYMCStateInfo *)info {
    BOOL ifResult = [JYMCJSExpressionParser getMIFValueWithString:self.element.mIf info:info];
    if (ifResult && !self.ifElementView) {
        [self addMIFView];
    }
    
    if (ifResult && self.ifElementView) {
        [self.ifElementView updateInfo:info];
    }
    
    if (!ifResult && self.ifElementView) {
        [self.ifElementView removeFromSuperview];
        self.ifElementView = nil;
    }
}

#pragma mark - Private

- (void)addMIFView {
    self.ifElementView = [[self.ifClass alloc] init];
    [self.superview insertSubview:self.ifElementView belowSubview:self];
    [self.ifElementView applyElement:self.element];
}

#pragma mark - Override

- (BOOL)isSentryView {
    return YES;
}

@end
