//
//  JYMCElementView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView.h"
#import "JYMCElement.h"
#import "JYMCElementView+Style.h"
#import "JYMCElementView+Layout.h"
#import "JYMCElementView+Action.h"
#import "JYMCStateInfo.h"

#import <YYCategories/YYCategories.h>

@class JYMCTrackActionExposeInfo;

@interface JYMCElementView ()

@property (nonatomic, strong) JYMCElement *internalElement;
@property (nonatomic, assign) NSTimeInterval actionTimeInterval;

@end

@implementation JYMCElementView

- (id)init {
    self = [super init];
    if (self ){
        _actionTimeInterval = 0.0;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self style_layoutSubViews];
}

#pragma mark - Public

- (void)applyElement:(__kindof JYMCElement *)element {
    if ([self isSentryView]) return;
    _internalElement = element;
    [self layout_applyLayout:element.layout];
}

- (void)updateInfo:(JYMCStateInfo *)info {
    if ([self isSentryView]) return;
    self.stateInfo = info.copy;
    [self style_applyStyle:self.internalElement.style info:self.stateInfo];
    [self action_applyAction:self.internalElement.action info:self.stateInfo];
}

- (CGFloat)heightForWidth:(CGFloat)width {
    self.left = 0;
    self.top = 0;
    return [self layout_heightForWidth:width];
}

- (CGFloat)widthForHeight:(CGFloat)height {
    self.left = 0;
    self.top = 0;
    return [self layout_widthForHeight:height];
}

- (void)refreshLayout {
    self.left = 0;
    self.top = 0;
    [self layout_refrehLayout];
}

#pragma mark - Private
/// 是否哨兵View
- (BOOL)isSentryView {
    return NO;
}

#pragma mark - Getter

- (__kindof JYMCElement *)element {
    return _internalElement;
}
@end
