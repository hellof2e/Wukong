//
//  JYMCRegExpParser.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/9/30.
//

#import "JYMCRegExpParser.h"


@implementation JYMCRegExpParser

/// 提取fb正则表达式
/// @example '$fb{5-4=1}' 匹配出5-4=1
+ (NSRegularExpression *)fbRegExp {
    
    /// 提取fs正则表达式， ext: $fb{5-4=1} 匹配出5-4=1
    static NSString * const s_jymc_fb_pattern = @"(?<=\\$fb\\{).*(?=\\})";
    static NSRegularExpression * s_jymc_fb_expression = nil;
    if (!s_jymc_fb_expression) {
        s_jymc_fb_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_fb_pattern
                                                                         options:kNilOptions
                                                                           error:nil];
    }
    
    return s_jymc_fb_expression;
}

/// 提取fs正则表达式
+ (NSRegularExpression *)fsRegExp {
    
    /// 提取fs正则表达式, 如 $fs{'adc' + 'abc'}' 匹配出'adc' + 'abc'
    static NSString * const s_jymc_fs_pattern = @"(?<=\\$fs\\{).*(?=\\})";
    static NSRegularExpression * s_jymc_fs_expression = nil;
    if (!s_jymc_fs_expression) {
        s_jymc_fs_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_fs_pattern
                                                                         options:kNilOptions
                                                                           error:nil];
    }
    
    return s_jymc_fs_expression;
}

/// 提取函数正则表达式
+ (NSRegularExpression *)funcRegExp {
    
    /// 函数匹配，如$f{clickEvent(${name}, world)} -> clickEvent(${name}, world)
    static NSString * const s_jymc_func_pattern = @"(?<=\\$f\\{).*(?=\\})";
    static NSRegularExpression * s_jymc_func_expression = nil;
    if (!s_jymc_func_expression) {
        s_jymc_func_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_func_pattern
                                                                           options:kNilOptions
                                                                             error:nil];
    }
    
    return s_jymc_func_expression;
}

/// 提取keypath正则表达式
+ (NSRegularExpression *)keyPathRegExp {
    
    /// 匹配keyPath
    static NSString * const s_jymc_keypath_pattern = @"(?<=\\$\\{).*?(?=\\})";
    static NSRegularExpression * s_jymc_keypath_expression = nil;
    if (!s_jymc_keypath_expression) {
        s_jymc_keypath_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_keypath_pattern
                                                                           options:kNilOptions
                                                                             error:nil];
    }
    
    return s_jymc_keypath_expression;
}

/// 占位表达式
+ (NSRegularExpression *)allExpRegExp {
    
    /// 提取所有表达式，'$f{${listitem.value} - ${listitem[0].value} - ${listitem} - ${value}}' 匹配出所有的表达式(包含${})：${listitem.value} ${listitem.0.value} ${listitem} ${value}
    /// 表达式'$f{${listitem.size()}}，匹配出${listitem.size()}
    static NSString * const s_jymc_all_expression_pattern = @"(\\$\\{[\\w\\.\\[\\]()]+\\})";
    static NSRegularExpression * s_jymc_expr_expression = nil;
    if (!s_jymc_expr_expression) {
        s_jymc_expr_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_all_expression_pattern
                                                                           options:kNilOptions
                                                                             error:nil];
    }
    
    return s_jymc_expr_expression;
}


+ (NSRegularExpression *)parseFuncRegExp {
    static NSString * const s_jymc_parse_func_pattern = @"^(\\w+)\\((.*)\\)$";
    static NSRegularExpression * s_jymc_parse_func_expression = nil;
    if (!s_jymc_parse_func_expression) {
        s_jymc_parse_func_expression = [NSRegularExpression regularExpressionWithPattern:s_jymc_parse_func_pattern
                                                                                 options:kNilOptions
                                                                                   error:nil];
    }
    
    return s_jymc_parse_func_expression;
}

+ (NSString *)parse:(NSString *)string byRegExp:(NSRegularExpression *)expression
{
    if (string.length == 0) {
        return nil;
    }
        
    NSRange range = [expression rangeOfFirstMatchInString:string options:NSMatchingReportProgress range:NSMakeRange(0, string.length)];
    if (range.location != NSNotFound && range.length > 0) {
        return [string substringWithRange:range];
    }
    
    return nil;
}


+ (NSArray<NSString *> *)parseAll:(NSString *)string byRegExp:(NSRegularExpression *)expression {
    
    NSMutableArray<NSString *> *result = nil;
    do {
        if (0 == string.length) break;
        
        NSArray<NSTextCheckingResult *> *checkingResults = [self matchesString:string byRegExp:expression];
        if (0 == checkingResults.count) break;
        
        result = @[].mutableCopy;
        [checkingResults enumerateObjectsUsingBlock:^(NSTextCheckingResult * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *text = [string substringWithRange:obj.range];
            if (text) [result addObject:text];
        }];
    } while (0);
    
    return result.copy;
}

+ (NSArray<NSTextCheckingResult *> *)matchesString:(NSString *)string byRegExp:(NSRegularExpression *)expression {
    if (string.length == 0) {
        return nil;
    }
    
    return [expression matchesInString:string
                               options:NSMatchingReportProgress
                                 range:NSMakeRange(0, string.length)];
}

@end
