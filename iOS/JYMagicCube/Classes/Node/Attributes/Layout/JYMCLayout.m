//
//  JYMCLayout.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/9.
//

#import "JYMCLayout.h"

@implementation JYMCLayout

- (instancetype)init {
    if (self = [super init]) {
        _flex = -1;
        _clipChildren = YES;
    }
    return self;
}

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    return @{@"flexDirection": @"flex-direction",
             @"justifyContent": @"justify-content",
             @"alignContent": @"align-content",
             @"alignItems": @"align-items",
             @"alignSelf": @"align-self",
             @"flexWrap": @"flex-wrap",
             @"flexGrow": @"flex-grow",
             @"flexShrink": @"flex-shrink",
             
             @"minWidth": @"min-width",
             @"minHeight": @"min-height",
             @"maxWidth": @"max-width",
             @"maxHeight": @"max-height",
             
             @"marginLeft": @"margin-left",
             @"marginRight": @"margin-right",
             @"marginTop": @"margin-top",
             @"marginBottom": @"margin-bottom",
             @"marginHorizontal": @"margin-horizontal",
             @"marginVertical": @"margin-vertical",
             
             @"paddingLeft": @"padding-left",
             @"paddingRight": @"padding-right",
             @"paddingTop": @"padding-top",
             @"paddingBottom": @"padding-bottom",
             @"paddingHorizontal": @"padding-horizontal",
             @"paddingVertical": @"padding-vertical",
             
             @"textAlign": @"text-align",
             @"clipChildren": @"clip-children",
             @"aspectRatio":@"aspect-ratio"
    };
}

@end
