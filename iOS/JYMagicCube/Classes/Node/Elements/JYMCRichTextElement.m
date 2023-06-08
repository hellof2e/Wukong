//
//  JYMCRichTextElement.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/2/9.
//

#import "JYMCRichTextElement.h"
#import "JYMCTextContent.h"
#import "JYMCTextElement.h"
#import <MJExtension/MJExtension.h>


@interface JYMCRichTextElement ()
@property (nonatomic, copy) NSString* specialFontFamily;
@property (nonatomic, copy) NSString* generalFontFamily;
@end

@implementation JYMCRichTextElement

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

+ (NSDictionary *)mj_objectClassInArray {
    return @{@"keywords": [JYMCTextContent class]};
}

- (NSString *)fontFamily {
    if (_specialFontFamily.length > 0) {
        return _specialFontFamily;
    }
    
    return _generalFontFamily;
}

@end
