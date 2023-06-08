//
//  JYMCTextElement.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCTextElement.h"
#import "JYMCTextContent.h"
#import <MJExtension/MJExtension.h>

@interface JYMCTextElement ()
@property (nonatomic, copy) NSString* specialFontFamily;
@property (nonatomic, copy) NSString* generalFontFamily;
@end

@implementation JYMCTextElement

+ (NSDictionary *)mj_replacedKeyFromPropertyName {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    NSDictionary *replacedKeysDic = [super mj_replacedKeyFromPropertyName];
    if (replacedKeysDic) {
        [dic addEntriesFromDictionary:replacedKeysDic];
    }
    [dic setObject:@"max-rows" forKey:@"numberOfLines"];
    [dic setObject:@"font-family" forKey:@"generalFontFamily"];
    [dic setObject:@"i-font-family" forKey:@"specialFontFamily"];
    return dic;
}

- (NSString *)fontFamily {
    if (_specialFontFamily.length > 0) {
        return _specialFontFamily;
    }
    
    return _generalFontFamily;
}

+ (NSDictionary *)mj_objectClassInArray {
    return @{@"text": [JYMCTextContent class]};
}

@end
