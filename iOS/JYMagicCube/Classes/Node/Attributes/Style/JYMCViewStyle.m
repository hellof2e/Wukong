//
//  JYMCViewStyle.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/4.
//

#import "JYMCViewStyle.h"

@interface JYMCViewStyle ()

///圆角类型
@property (nonatomic, assign, readwrite) JYMCCornerRadiusType radiusType;
///各圆角数据
@property (nonatomic, copy, nullable, readwrite) NSArray *cornerRadiusArr;

@end

@implementation JYMCViewStyle

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    return @{@"cornerRadius": @"border-radius",
             @"borderStyle": @"border-style",
             @"borderWidth": @"border-width",
             @"borderColor": @"border-color",
    };
}

- (void)mj_didConvertToObjectWithKeyValues:(NSDictionary *)keyValues {
    NSArray *tempArr = [self.cornerRadius componentsSeparatedByString:@" "];
    NSMutableArray *arr = @[].mutableCopy;
    
    [tempArr enumerateObjectsUsingBlock:^(NSString *obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSString *valueString = [obj stringByReplacingOccurrencesOfString:@" " withString:@""];
        valueString = [valueString stringByReplacingOccurrencesOfString:@"\r" withString:@""];
        valueString = [valueString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
        if (valueString.length) [arr addObject:valueString];
    }];
    
    if (arr.count == 1) {
        self.radiusType = JYMCCornerRadiusTypeAll;
        self.cornerRadiusArr = arr;
    } else if (arr.count == 4) {
        self.radiusType = JYMCCornerRadiusTypePart;
        self.cornerRadiusArr = arr;
    } else {
        self.radiusType = JYMCCornerRadiusTypeNone;
        self.cornerRadiusArr = nil;
    }
}

@end
