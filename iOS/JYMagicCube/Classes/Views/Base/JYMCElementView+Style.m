//
//  JYMCElementView+Style.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView+Style.h"
#import "JYMCViewStyle.h"
#import "JYMCDataParser.h"
#import <objc/runtime.h>
#import <YYCategories/YYCategories.h>
#import "JYMCElement.h"

@interface JYMCElementView ()

/// 边框（实线 & 虚线）
@property (nonatomic, strong) CAShapeLayer *style_borderLayer;
/// 渐变 & 背景
@property (nonatomic, strong) CAGradientLayer *style_gradientLayer;

@end

@implementation JYMCElementView (Style)

#pragma mark - Public
- (void)style_applyStyle:(JYMCViewStyle *)style info:(JYMCStateInfo *)info {
    // 背景色
    [self updateBackGroundColor:style info:info];
    // 边框
    [self updateBorderLayer:style info:info];
    // 透明度
    [self updateAlpha:style info:info];
}

- (void)style_applyActiveStyle:(JYMCViewStyle *)style info:(JYMCStateInfo *)info {
    
    // 背景色
    if (style.background.isNotBlank) {
        [self updateBackGroundColor:style info:info];
    }

    // 边框
    CAShapeLayer *style_borderLayer = objc_getAssociatedObject(self, @selector(style_borderLayer));
    if (style.borderColor.isNotBlank && nil != style_borderLayer.superlayer) {
        UIColor *borderColor = [JYMCDataParser getColorValueWithString:style.borderColor info:info];
        style_borderLayer.strokeColor = borderColor.CGColor;
    }

    // 透明度
    [self updateAlpha:style info:info];
}

- (void)style_applyActiveStyle {
    [self style_applyActiveStyle:self.element.activeStyle info:self.stateInfo];
}

- (void)style_applyNormalStyle {
    [self style_applyStyle:self.element.style info:self.stateInfo];
}

#pragma mark - Life Cycle
- (void)style_layoutSubViews {
    // 渐变圆角由self裁切
    CAGradientLayer *gradientLayer = objc_getAssociatedObject(self, @selector(style_gradientLayer));
    if (gradientLayer.superlayer) {
        gradientLayer.frame = self.bounds;
    }
    
    // 更新圆角 & 边框
    [self updateCornerRadiusAndBorder];
    
    // debug layer 个数
//    CAShapeLayer *layer = objc_getAssociatedObject(self, @selector(style_borderLayer));
//    printf("-----gradientLayer exit:%d  layer exit:%d\n", gradientLayer != nil, layer != nil);
}

#pragma mark -- UI Update
- (void)addGradientIfNeed {
    if (!self.style_gradientLayer.superlayer) {
        [self.layer insertSublayer:self.style_gradientLayer atIndex:0];
    }
}

- (void)addBorderIfNeed {
    if (!self.style_borderLayer.superlayer) {
        [self.layer addSublayer:self.style_borderLayer];
    }
}

- (void)updateBackGroundColor:(JYMCViewStyle *)style info:(JYMCStateInfo *)info {
    // 重置设置
    CAGradientLayer *gradientLayer = objc_getAssociatedObject(self, @selector(style_gradientLayer));
    self.backgroundColor = [UIColor clearColor];// 确保背景色通过 gradientLayer 设置
    gradientLayer.backgroundColor = [UIColor clearColor].CGColor;
    gradientLayer.colors = nil;
    gradientLayer.locations = nil;
    
    JYMCGradient *gradient = [JYMCDataParser getGradientColorsWithString:style.background info:info];
    if (gradient) {
        [self addGradientIfNeed];// 确保添加
        self.style_gradientLayer.startPoint = gradient.startPoint;
        self.style_gradientLayer.endPoint   = gradient.endPoint;
        self.style_gradientLayer.colors     = gradient.colors;
        self.style_gradientLayer.locations  = gradient.locations;
        self.style_gradientLayer.type       = gradient.type;
    } else {
        UIColor *backgroundColor = [JYMCDataParser getColorValueWithString:style.background info:info];
        if (backgroundColor) {
            [self addGradientIfNeed];// 确保添加
            self.style_gradientLayer.backgroundColor = backgroundColor.CGColor;
        }
    }
}

- (void)updateBorderLayer:(JYMCViewStyle *)style info:(JYMCStateInfo *)info {
    // 重置设置
    CAShapeLayer *layer = objc_getAssociatedObject(self, @selector(style_borderLayer));
    layer.lineDashPattern = nil;
    layer.lineWidth = 0;
    layer.borderColor = nil;
    layer.strokeColor = nil;
    
    CGFloat borderWidth = [JYMCDataParser getFloatPixelWithString:style.borderWidth info:info];
    UIColor *borderColor = [JYMCDataParser getColorValueWithString:style.borderColor info:info];
    if (borderWidth > 0 && borderColor) {// 存在border才添加
        [self addBorderIfNeed];// 确保添加
        NSString *borderStyle = [JYMCDataParser getStringValueWithString:style.borderStyle info:info];
        BOOL isDash = [borderStyle isEqualToString:@"dashed"];
        self.style_borderLayer.lineDashPattern = isDash ? @[@4, @3] : nil;
        self.style_borderLayer.lineWidth = borderWidth;
        self.style_borderLayer.fillColor = [UIColor clearColor].CGColor;
        self.style_borderLayer.strokeColor = borderColor.CGColor;
    }
}

- (void)updateAlpha:(JYMCViewStyle *)style info:(JYMCStateInfo *)info {
    if (style.opacity.length > 0) {
        CGFloat opacity = [JYMCDataParser getFloatWithString:style.opacity info:info];
        self.alpha = opacity;
    } else {
        self.alpha = 1;
    }
}

- (void)updateCornerRadiusAndBorder {
    // 重置圆角设置
    CAShapeLayer *layer = objc_getAssociatedObject(self, @selector(style_borderLayer));
    layer.path = nil;
    layer.masksToBounds = NO;
    layer.frame = self.bounds;
    self.layer.mask = nil;
    self.layer.cornerRadius = 0;
    self.layer.masksToBounds = NO;
    
    __block BOOL hasRadius = NO;
    void (^UpdateCornerRadiusBlock)(NSArray *, BOOL) = ^(NSArray *radius, BOOL ignoreSelfSetting) {
        hasRadius = YES;
        if (!ignoreSelfSetting) {
            self.layer.mask = [JYMCElementView style_createShaperWithBounds:self.bounds radius:radius];
            self.layer.masksToBounds = YES;
        }
        
        if (layer) {// 无边框不需设置边框的圆角
            [self addBorderIfNeed];// 确保添加
            self.style_borderLayer.frame = self.bounds;
            self.style_borderLayer.path = [JYMCElementView style_createPathWithBounds:self.bounds radius:radius];
            self.style_borderLayer.masksToBounds = YES;
        }
    };
    
    if (self.element.style.radiusType == JYMCCornerRadiusTypePart) {// 部分圆角
        NSArray *cornerRadius = [JYMCElementView cornerRadiiWithArray:self.element.style.cornerRadiusArr info:self.stateInfo];
        UpdateCornerRadiusBlock(cornerRadius, NO);
    } else {
        CGFloat cornerRadius = [JYMCElementView cornerRadiusWithString:self.element.style.cornerRadiusArr.firstObject info:self.stateInfo];
        UpdateCornerRadiusBlock(@[@(cornerRadius), @(cornerRadius), @(cornerRadius), @(cornerRadius)], cornerRadius == 0);
    }
}

#pragma mark -- Private
+ (CAShapeLayer *)style_createShaperWithBounds:(CGRect)bounds radius:(NSArray *)radius {
    if (CGRectIsEmpty(bounds) || radius.count != 4) return nil;
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    shapeLayer.path = [JYMCElementView style_createPathWithBounds:bounds radius:radius];
    
    return shapeLayer;
}

+ (CGMutablePathRef)style_createPathWithBounds:(CGRect)bounds radius:(NSArray *)radius {
    if (CGRectIsEmpty(bounds) || radius.count != 4) return NULL;
    CGFloat topLeft = [radius[0] floatValue];
    CGFloat topRight = [radius[1] floatValue];
    CGFloat bottomRight = [radius[2] floatValue];
    CGFloat bottomLeft = [radius[3] floatValue];
    
    const CGFloat minX = CGRectGetMinX(bounds);
    const CGFloat minY = CGRectGetMinY(bounds);
    const CGFloat maxX = CGRectGetMaxX(bounds);
    const CGFloat maxY = CGRectGetMaxY(bounds);
    
    const CGFloat topLeftCenterX = minX + topLeft;
    const CGFloat topLeftCenterY = minY + topLeft;
    const CGFloat topRightCenterX = maxX - topRight;
    const CGFloat topRightCenterY = minY + topRight;
    const CGFloat bottomLeftCenterX = minX + bottomLeft;
    const CGFloat bottomLeftCenterY = maxY - bottomLeft;
    const CGFloat bottomRightCenterX = maxX -  bottomRight;
    const CGFloat bottomRightCenterY = maxY - bottomRight;
    
    CGMutablePathRef path = CGPathCreateMutable();
    CGPathAddArc(path, NULL, topLeftCenterX, topLeftCenterY,topLeft, M_PI, 3 * M_PI_2, NO);//顶 左
    CGPathAddArc(path, NULL, topRightCenterX , topRightCenterY, topRight, 3 * M_PI_2, 0, NO);//顶 右
    CGPathAddArc(path, NULL, bottomRightCenterX, bottomRightCenterY, bottomRight,0, M_PI_2, NO);//底 右
    CGPathAddArc(path, NULL, bottomLeftCenterX, bottomLeftCenterY, bottomLeft, M_PI_2,M_PI, NO);//底 左
    CGPathCloseSubpath(path);
    
    return path;
}

+ (CGFloat)cornerRadiusWithString:(NSString *)cornerRadius info:(JYMCStateInfo *)info {
    if (cornerRadius.length == 0) return 0;
    return [JYMCDataParser getFloatPixelWithString:cornerRadius info:info];
}

+ (NSArray *)cornerRadiiWithArray:(NSArray *)cornerRadii info:(JYMCStateInfo *)info {
    NSMutableArray *radius = @[].mutableCopy;
    [cornerRadii enumerateObjectsUsingBlock:^(NSString *obj, NSUInteger idx, BOOL * _Nonnull stop) {
        CGFloat radiu = [self cornerRadiusWithString:obj info:info];
        [radius addObject:@(radiu)];
    }];
    return radius;
}

#pragma mark - Getter
- (CAShapeLayer *)style_borderLayer {
    CAShapeLayer *layer = objc_getAssociatedObject(self, _cmd);
    if (layer == nil) {
        layer = [CAShapeLayer layer];
        layer.fillColor = [UIColor clearColor].CGColor;
        objc_setAssociatedObject(self, _cmd, layer, OBJC_ASSOCIATION_RETAIN);
    }
    return layer;
}

- (CAGradientLayer *)style_gradientLayer {
    CAGradientLayer *layer = objc_getAssociatedObject(self, _cmd);
    if (!layer) {
        layer = [CAGradientLayer layer];
        objc_setAssociatedObject(self, _cmd, layer, OBJC_ASSOCIATION_RETAIN);
    }
    return layer;
}

@end
