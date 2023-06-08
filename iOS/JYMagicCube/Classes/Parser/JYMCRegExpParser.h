//
//  JYMCRegExpParser.h
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/9/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCRegExpParser : NSObject

/// 提取fb正则表达式，如 '$fb{5-4=1}' 匹配出5-4=1
+ (NSRegularExpression *)fbRegExp;

/// 提取fs正则表达式
+ (NSRegularExpression *)fsRegExp;

/// 提取函数正则表达式
+ (NSRegularExpression *)funcRegExp;

/// 解析函数正则表达式，例如：$f{clickEvent(${name}, world)} 匹配出 clickEvent(${name}, world)
+ (NSRegularExpression *)parseFuncRegExp;

/// 提取keypath正则表达式，如 ${listitem.value} 匹配出 listitem.value
+ (NSRegularExpression *)keyPathRegExp;

/// 提取所有表达式，'$f{${listitem.value} - ${listitem[0].value} - ${listitem} - ${value}}' 匹配出所有的表达式(包含${})：${listitem.value} ${listitem.0.value} ${listitem} ${value}
+ (NSRegularExpression *)allExpRegExp;


+ (NSString *)parse:(NSString *)string byRegExp:(NSRegularExpression *)expression;


+ (NSArray<NSString *> *)parseAll:(NSString *)string byRegExp:(NSRegularExpression *)expression;


+ (NSArray<NSTextCheckingResult *> *)matchesString:(NSString *)string byRegExp:(NSRegularExpression *)expression;
@end

NS_ASSUME_NONNULL_END
