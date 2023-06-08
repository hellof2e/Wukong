//
//  JYMCLottieView.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/18.
//

#import "JYMCProgressView.h"
#import <YogaKit/UIView+Yoga.h>
#import "JYMCProgressElement.h"
#import "JYMCDataParser.h"
#import "JYMCElementView+Style.h"
#import "JYMCStateInfo.h"

@interface JYMCProgressView ()

@property (nonatomic, strong) UIView *progressView;

@property (nonatomic, strong) CAGradientLayer *gradientLayer;
@end

@implementation JYMCProgressView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.progressView];
    [self.progressView configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
        layout.width = YGPercentValue(50);
        layout.height = YGPercentValue(100);
    }];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    if (_gradientLayer.superlayer) {
        _gradientLayer.frame = self.progressView.bounds;
    }

    // 重置
    self.progressView.layer.mask = nil;
    self.progressView.layer.masksToBounds = NO;
    
    void (^UpdateCornerRadiusBlock)(NSArray *) = ^(NSArray *radius){
        self.progressView.layer.mask = [JYMCElementView style_createShaperWithBounds:self.bounds radius:radius];
        self.progressView.layer.masksToBounds = YES;
    };
    if (self.element.style.radiusType == JYMCCornerRadiusTypePart) {// 部分圆角
        NSArray *cornerRadius = [JYMCElementView cornerRadiiWithArray:self.element.style.cornerRadiusArr info:self.stateInfo];
        UpdateCornerRadiusBlock(cornerRadius);
    } else if (self.element.style.radiusType == JYMCCornerRadiusTypeAll) {
        CGFloat cornerRadius = [JYMCElementView cornerRadiusWithString:self.element.style.cornerRadiusArr.firstObject info:self.stateInfo];
        if (cornerRadius > 0) {
            UpdateCornerRadiusBlock(@[@(cornerRadius), @(cornerRadius), @(cornerRadius), @(cornerRadius)]);
        }
    }
}

#pragma mark - Public
- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    JYMCProgressElement* element = (JYMCProgressElement *)self.element;
    CGFloat progress = [JYMCDataParser getFloatWithString:element.progress info:info];
    CGFloat maxProgress = [JYMCDataParser getFloatWithString:element.maxProgress info:info];
    self.progressView.yoga.width = YGPercentValue(100 * progress / maxProgress);
    
    // 颜色
    [self setProgressColor:element.progressColor info:info];
    
    // 圆角
    CGFloat cornerRadius = [JYMCDataParser getFloatPixelWithString:element.style.cornerRadius info:info];
    if (cornerRadius > 0) {
        if (cornerRadius != self.progressView.layer.cornerRadius) {
            self.progressView.layer.cornerRadius = cornerRadius;
            self.progressView.layer.masksToBounds = YES;
        }
    } else {
        self.progressView.layer.cornerRadius = 0;
    }
}

- (void)setProgressColor:(NSString *)progressColor info:(JYMCStateInfo *)info {
    JYMCGradient* gradient = [JYMCDataParser getGradientColorsWithString:progressColor info:info];
    if (gradient) {
        self.gradientLayer.startPoint = gradient.startPoint;
        self.gradientLayer.endPoint   = gradient.endPoint;
        self.gradientLayer.colors     = gradient.colors;
        self.gradientLayer.locations  = gradient.locations;
        self.gradientLayer.type       = gradient.type;
        if (self.gradientLayer.superlayer == nil) {
            [self.progressView.layer insertSublayer:self.gradientLayer atIndex:0];
        }
        
    } else {
        if (_gradientLayer.superlayer) {
            [_gradientLayer removeFromSuperlayer];
        }
        UIColor *backgroundColor = [JYMCDataParser getColorValueWithString:progressColor info:info];
        self.progressView.backgroundColor = backgroundColor ? backgroundColor : [UIColor clearColor];
    }
}


#pragma mark - Getter
- (UIView *)progressView {
    if (!_progressView) {
        _progressView = [[UIView alloc] init];
    }
    
    return _progressView;
}

- (CAGradientLayer *)gradientLayer {
    if (!_gradientLayer) {
        _gradientLayer = [CAGradientLayer layer];
    }
    
    return _gradientLayer;
}

@end
