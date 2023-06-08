//
//  JYMCContainerView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/5.
//

#import "JYMCContainerView.h"
#import "JYMCDataParser.h"
#import "JYMCContainerElement.h"
#import "JYMCCustomerElement.h"
#import "JYMCCustomerView.h"
#import "JYMCMForSentryView.h"
#import <YogaKit/UIView+Yoga.h>
#import <SDWebImage/UIImageView+WebCache.h>
#import "JYMCJSExpressionParser.h"
#import "JYMCIfSentryView.h"
#import "JYMCStateInfo.h"
#import "JYMCElementView+Style.h"
#import "JYMCError.h"

@interface JYMCContainerView ()

@property (nonatomic, strong) UIImageView *backgroundImageView;

@end

@implementation JYMCContainerView

#pragma mark - Public

- (void)applyElement:(JYMCContainerElement *)element {
    [super applyElement:element];
    self.clipsToBounds = element.layout.clipChildren;
    
    for (JYMCElement *childElement in element.children) {
        
        Class aClass = childElement.viewClass;
        if (aClass == NULL) {
            NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 类型的元素找不到对应 UI 组件", childElement.type];
            @throw [[NSException alloc] initWithName:JYMagicCubeExceptionName reason:reason userInfo:@{}];
        }
        
        if ([aClass isSubclassOfClass:[JYMCElementView class]]) {
            if (childElement.mFor.length > 0) {
                JYMCMForSentryView *mForView = [[JYMCMForSentryView alloc] init];
                mForView.viewClass = aClass;
                [mForView applyElement:childElement];
                [self addSubview:mForView];
                continue;
            }

            if (childElement.mIf.length > 0) {
                JYMCIfSentryView *ifView = [[JYMCIfSentryView alloc] init];
                ifView.ifClass = aClass;
                [ifView applyElement:childElement];
                [self addSubview:ifView];
                continue;
            }

            if ([childElement isKindOfClass:[JYMCCustomerElement class]]) {
                JYMCCustomerView *customerView = [[JYMCCustomerView alloc] init];
                [customerView applyElement:childElement];
                [self addSubview:customerView];
                continue;
            }
                        
            JYMCElementView *view = [[aClass alloc] init];
            [view applyElement:childElement];
            [self addSubview:view];
        }
    }
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if ([self.element isKindOfClass:[JYMCContainerElement class]]) {
        JYMCContainerElement *containerElement = (JYMCContainerElement *)self.element;
        NSString *imageUrl = [JYMCDataParser getStringValueWithString:containerElement.backgroundImage
        info:info];
        [self setBackgroundImageURL:imageUrl];
    }
    
    // 更新数据
    for (UIView *subview in self.subviews) {
        if ([subview isKindOfClass:[JYMCElementView class]]) {
            JYMCElementView *mcView = (JYMCElementView *)subview;
            if (![mcView isSentryView] && (mcView.element.mFor.length > 0 || mcView.element.mIf.length > 0)) {
                continue;
            }
            [mcView updateInfo:info];
        }
    }
}

- (void)setBackgroundImageURL:(NSString *)imageUrl {
    if (imageUrl.length > 0) {
        self.backgroundImageView.hidden = NO;
        self.backgroundImageView.contentMode = UIViewContentModeScaleAspectFill;
        [self.backgroundImageView sd_setImageWithURL:[NSURL URLWithString:imageUrl]];
    } else {
        self.backgroundImageView.hidden = YES;
    }
}

#pragma mark - Private

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    self.clipsToBounds = YES;
    [self addSubview:self.backgroundImageView];
    [self.backgroundImageView configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
        layout.width = YGPercentValue(100);
        layout.height = YGPercentValue(100);
        layout.position = YGPositionTypeAbsolute;
    }];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.backgroundImageView.frame = self.bounds;
}

#pragma mark - 触摸高亮
- (void)style_applyActiveStyle {
    [super style_applyActiveStyle];
    JYMCContainerElement *containerElement = (JYMCContainerElement *)self.element;
    NSString *imageUrl = [JYMCDataParser getStringValueWithString:containerElement.activeBackgroundImage
                                                             info:self.stateInfo];
    [self setBackgroundImageURL:imageUrl];
    for (UIView *subview in self.subviews) {
        if ([subview isKindOfClass:[JYMCElementView class]]) {
            JYMCElementView *mcView = (JYMCElementView *)subview;
            if (mcView.gestureRecognizers.count == 0 && mcView.element.useActive) {
                [mcView style_applyActiveStyle];
            }
        }
    }
}

- (void)style_applyNormalStyle {
    [super style_applyNormalStyle];
    
    JYMCContainerElement *containerElement = (JYMCContainerElement *)self.element;
    NSString *imageUrl = [JYMCDataParser getStringValueWithString:containerElement.backgroundImage
                                                             info:self.stateInfo];
    [self setBackgroundImageURL:imageUrl];
    
    for (UIView *subview in self.subviews) {
        if ([subview isKindOfClass:[JYMCElementView class]]) {
            JYMCElementView *mcView = (JYMCElementView *)subview;
            if (mcView.gestureRecognizers.count == 0 && mcView.element.useActive) {
                [mcView style_applyNormalStyle];
            }
        }
    }
}

#pragma mark - getters

- (UIImageView *)backgroundImageView {
    if (!_backgroundImageView) {
        _backgroundImageView = [[UIImageView alloc] init];
        _backgroundImageView.hidden = YES;
        _backgroundImageView.backgroundColor = [UIColor clearColor];
    }
    return _backgroundImageView;
}

@end
