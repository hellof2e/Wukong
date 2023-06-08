//
//  JYMCTextContent.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCTextContent.h"

@interface JYMCTextContent ()
@property (nonatomic, copy) NSString* specialFontFamily;
@property (nonatomic, copy) NSString* generalFontFamily;
@end

@implementation JYMCTextContent

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    return @{@"fontSize": @"font-size",
             @"fontWeight": @"font-weight",
             @"decorationLine": @"text-decoration-line",
             @"decorationStyle": @"text-decoration-style",
             @"decorationColor": @"text-decoration-color",
             @"generalFontFamily": @"font-family",
             @"specialFontFamily": @"i-font-family"
            
    };
}

- (NSString *)fontFamily {
    if (_specialFontFamily.length > 0) {
        return _specialFontFamily;
    }
    
    return _generalFontFamily;
}

- (BOOL)tagContent {
    BOOL result = NO;
    if (self.url.length > 0 &&
        self.width.length > 0 &&
        self.height.length > 0) {
        result = YES;
    }
    return result;
}

- (NSString *)color {
    return _color ?: @"#000000";
}

@end
