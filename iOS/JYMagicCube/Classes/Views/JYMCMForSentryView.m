//
//  JYMCMForSentryView.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/30.
//

#import "JYMCMForSentryView.h"
#import "JYMCJSExpressionParser.h"
#import "JYMCElement.h"
#import "JYMCLayout.h"
#import "JYMCElementView+Layout.h"
#import "JYMCDataParser.h"
#import "JYMCStateInfo.h"

#import <YYCategories/YYCategories.h>
#import <YogaKit/UIView+Yoga.h>

@interface JYMCMForSentryView ()

/// mFor 视图的 element
@property (nonatomic, strong) JYMCElement *mForViewElement;

/// mFor 管理的所有视图
@property (nonatomic, strong) NSMutableArray *mForViews;

/// mFor 所有数据
@property (nonatomic, copy) NSArray *mForFullDatas;

@end

@implementation JYMCMForSentryView

#pragma mark - Public

- (instancetype)init {
    self = [super init];
    if (self) {
        self.hidden = YES;
        self.yoga.isIncludedInLayout = NO;
    }
    return self;
}

- (__kindof JYMCElement *)element {
    return _mForViewElement;
}

- (void)applyElement:(__kindof JYMCElement *)element {
    _mForViewElement = element;
}

- (void)updateInfo:(JYMCStateInfo *)info {
    self.mForFullDatas = [JYMCDataParser getArrayValueWithString:self.element.mFor info:info];
    if (self.mForFullDatas.count == 0) {
        [self removeAllMForViews];
        return;
    }
    
    NSArray* displayableDatas = [self findDisplayableDatas:info];
    
    NSInteger difference = displayableDatas.count - self.mForViews.count;
    if (difference > 0) {
        // add
        for (NSUInteger i = 0; i < difference; i ++) {
            JYMCElementView *view = [[self.viewClass alloc] init];
            [view applyElement:self.element];
            [self.superview insertSubview:view belowSubview:self];
            [self.mForViews addObject:view];
        }
    } else if (difference < 0) {
        // remove
        NSUInteger gap = ABS(difference);
        while (gap > 0) {
            [self.mForViews.lastObject removeFromSuperview];
            [self.mForViews removeLastObject];
            --gap;
        }
    }
    
    [displayableDatas enumerateObjectsUsingBlock:^(id _Nonnull item, NSUInteger idx, BOOL * _Nonnull stop) {
        if (idx < self.mForViews.count) {
            JYMCElementView *view = self.mForViews[idx];
            [info updateScopeState:item listItemIndex:idx];
            [view updateInfo:info];
        }
    }];
}

#pragma mark - Private

/// 过滤数据，找出需要展示的数据，支持If功能
- (NSArray *)findDisplayableDatas:(JYMCStateInfo *)info {
    
    NSMutableArray* datas = [[NSMutableArray alloc] init];
    NSString* ifExp = self.element.mIf;
    if (ifExp.length == 0) { //没有if表达式，展示全部数据
        [self.mForFullDatas enumerateObjectsUsingBlock:^(id _Nonnull item, NSUInteger idx, BOOL * _Nonnull stop) {
           if ([item isKindOfClass:[NSDictionary class]]) {
               [datas addObject:item];
           }
       }];
    } else {
        [self.mForFullDatas enumerateObjectsUsingBlock:^(id _Nonnull item, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([item isKindOfClass:[NSDictionary class]]) {
                BOOL ifResult = [JYMCJSExpressionParser getMIFValueWithString:ifExp info:info];
                if (ifResult) {
                    [datas addObject:item];
                }
            }
        }];
    }
    
    return [datas copy];
}

- (void)removeAllMForViews {
    if (self.mForViews.count == 0) {
        return;
    }
    
    [self.mForViews enumerateObjectsUsingBlock:^(JYMCElementView * _Nonnull view, NSUInteger idx, BOOL * _Nonnull stop) {
        [view removeFromSuperview];
    }];
    [self.mForViews removeAllObjects];
}

- (BOOL)isSentryView {
    return YES;
}

#pragma mark - Getter

- (NSMutableArray *)mForViews {
    if (!_mForViews) {
        _mForViews = [NSMutableArray array];
    }
    return _mForViews;
}

@end
