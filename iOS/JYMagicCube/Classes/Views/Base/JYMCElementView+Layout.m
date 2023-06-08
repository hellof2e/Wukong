//
//  JYMCElementView+Layout.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCElementView+Layout.h"
#import "JYMCLayout.h"
#import "JYMCDataParser.h"
#import "JYMCLayoutTrans.h"
#include "JYMCContainerElement.h"
#import <YogaKit/UIView+Yoga.h>

@implementation JYMCElementView (Layout)

#pragma mark - Public

- (void)layout_applyLayout:(JYMCLayout *)config {
    [self configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
        
        // flexDirection 因为默认值与其他平台不同，需要每次进来都赋值一下
        layout.flexDirection = YGFlexDirectionFromString(config.flexDirection);
        if (config.justifyContent.length > 0) layout.justifyContent = YGJustifyFromString(config.justifyContent);
        if (config.alignContent.length > 0) layout.alignContent = YGAlignFromString(config.alignContent);
        if (config.alignItems.length > 0) layout.alignItems = YGAlignFromString(config.alignItems);
        if (config.alignSelf.length > 0) layout.alignSelf = YGAlignFromString(config.alignSelf);
        if (config.flexWrap.length > 0) layout.flexWrap = YGWrapFromString(config.flexWrap);
        if (config.flex >= 0) layout.flex = config.flex;
        if (config.flexGrow > 0) layout.flexGrow = config.flexGrow;
        if (config.flexShrink > 0) layout.flexShrink = config.flexShrink;
        
        if (config.marginLeft.length > 0) layout.marginLeft = YGValueFromString(config.marginLeft);
        if (config.marginTop.length > 0) layout.marginTop = YGValueFromString(config.marginTop);
        if (config.marginRight.length > 0) layout.marginRight = YGValueFromString(config.marginRight);
        if (config.marginBottom.length > 0) layout.marginBottom = YGValueFromString(config.marginBottom);
        if (config.marginVertical.length > 0) layout.marginVertical = YGValueFromString(config.marginVertical);
        if (config.marginHorizontal.length > 0) layout.marginHorizontal = YGValueFromString(config.marginHorizontal);
        
        if (config.paddingLeft.length > 0) layout.paddingLeft = YGValueFromString(config.paddingLeft);
        if (config.paddingTop.length > 0) layout.paddingTop = YGValueFromString(config.paddingTop);
        if (config.paddingRight.length > 0) layout.paddingRight = YGValueFromString(config.paddingRight);
        if (config.paddingBottom.length > 0) layout.paddingBottom = YGValueFromString(config.paddingBottom);
        if (config.paddingVertical.length > 0) layout.paddingVertical = YGValueFromString(config.paddingVertical);
        if (config.paddingHorizontal.length > 0) layout.paddingHorizontal = YGValueFromString(config.paddingHorizontal);
        
        if (config.width.length > 0) layout.width = YGMCValueFromString(config.width);
        if (config.height.length > 0) layout.height = YGMCValueFromString(config.height);
        if (config.minWidth.length > 0) layout.minWidth = YGMCValueFromString(config.minWidth);
        if (config.minHeight.length > 0) layout.minHeight = YGMCValueFromString(config.minHeight);
        if (config.maxWidth.length > 0) layout.maxWidth = YGMCValueFromString(config.maxWidth);
        if (config.maxHeight.length > 0) layout.maxHeight = YGMCValueFromString(config.maxHeight);
        if (config.aspectRatio.length > 0) layout.aspectRatio = [config.aspectRatio floatValue];
        //增加绝对布局支持
        if (config.position.length > 0) layout.position = YGPositionTypeFromString(config.position);
        if (config.left.length > 0) layout.left = YGMCValueFromString(config.left);
        if (config.top.length > 0) layout.top = YGMCValueFromString(config.top);
        if (config.right.length > 0) layout.right = YGMCValueFromString(config.right);
        if (config.bottom.length > 0) layout.bottom = YGMCValueFromString(config.bottom);
    }];
}

- (CGFloat)layout_heightForWidth:(CGFloat)width {
    self.yoga.width = YGPointValue(width);
    [self.yoga applyLayoutPreservingOrigin:YES dimensionFlexibility:YGDimensionFlexibilityFlexibleHeight];
    return self.frame.size.height + self.frame.origin.y;
}

- (CGFloat)layout_widthForHeight:(CGFloat)height {
    self.yoga.height = YGPointValue(height);
    [self.yoga applyLayoutPreservingOrigin:YES dimensionFlexibility:YGDimensionFlexibilityFlexibleWidth];
    return self.frame.size.width + self.frame.origin.x;
}

- (void)layout_refrehLayout {
    self.yoga.width = YGPointValue(self.frame.size.width);
    NSInteger flexible = YGDimensionFlexibilityFlexibleHeight;
    JYMCContainerElement* element = (JYMCContainerElement *)self.element;
    if ([element.matchType isEqualToString:@"all"] && self.frame.size.height > 0) {
        self.yoga.height = YGPointValue(self.frame.size.height);
        flexible |= YGDimensionFlexibilityFlexibleWidth;
    }
    [self.yoga applyLayoutPreservingOrigin:YES dimensionFlexibility:flexible];
}

#pragma mark - Private

YGValue YGValueFromString(NSString *str) {
    CGFloat value = [JYMCDataParser getFloatPixelWithString:str];
    return YGPointValue(value);
}

YGValue YGMCValueFromString(NSString *str) {
    JYMCValue* value = [JYMCDataParser getValueWithString:str];
    if (value.unit == JYMCValueUnitPercent) {
        return YGPercentValue(value.value);
    } else {
        return YGPointValue(value.value);
    }
}


@end
