//
//  JYMCJSExpressionParser.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/13.
//


#import "JYMCJSExpressionParser.h"
#import "JYMCDataParser.h"
#import "JYMCJSExpression.h"
#import "JYMCJSExpressionCache.h"
#import "JYMCJSAction.h"
#import "JYMCStateInfo.h"
#import "JYMCRegExpParser.h"

@implementation JYMCJSExpressionParser

#pragma mark - Public
+ (BOOL)isStringJSExpression:(NSString *)string {
    return JYMCExpressionReturnStringType == [JYMCJSExpression typeWithExpressionDesc:string];
}

+ (BOOL)getMIFValueWithString:(NSString *)string
                         info:(JYMCStateInfo *)info {
    if (![string isKindOfClass:NSString.class] || string.length == 0) return YES;
    
    NSArray<NSString *> *allOriginalValue = [JYMCRegExpParser parseAll:string byRegExp:[JYMCRegExpParser allExpRegExp]];
    NSMutableString *newString = string.mutableCopy;
    for (NSString *originalValue in allOriginalValue) {
        NSString *actValue = [JYMCDataParser getStringValueWithString:originalValue info:info];
        [newString replaceOccurrencesOfString:originalValue
                                   withString:actValue?:@"null"
                                      options:NSCaseInsensitiveSearch range:NSMakeRange(0, newString.length)];
    }
    NSString *realMIFString = [JYMCRegExpParser parse:newString byRegExp:[JYMCRegExpParser fbRegExp]];
    if (realMIFString.length == 0) return NO;
    
    id cacheResult = [[JYMCJSExpressionCache shareCache] cacheValueWithJSExpression:realMIFString];
    if (cacheResult && [cacheResult isKindOfClass:NSNumber.class]) {
        return [cacheResult boolValue];
    }
    
    BOOL jsCalResult = [JYMCJSExpression fbExpression:realMIFString];
    [[JYMCJSExpressionCache shareCache] storeValue:@(jsCalResult) expression:realMIFString];
    
    return jsCalResult;
}

+ (NSString *)stringWithExpression:(NSString *)string
                              info:(JYMCStateInfo *)info {
    if (![string isKindOfClass:NSString.class] || string.length == 0) return @"";
    
    NSArray<NSString *> *allOriginalValue = [JYMCRegExpParser parseAll:string byRegExp:[JYMCRegExpParser allExpRegExp]];
    NSMutableString *newString = string.mutableCopy;
    BOOL hasNull = NO;
    for (NSString *originalValue in allOriginalValue) {
        NSString *actValue = [JYMCDataParser getStringValueWithString:originalValue info:info];
        if (!actValue) hasNull = YES;
        [newString replaceOccurrencesOfString:originalValue
                                   withString:actValue?:@"null"
                                      options:NSCaseInsensitiveSearch range:NSMakeRange(0, newString.length)];
    }
    NSString *realString = [JYMCRegExpParser parse:newString byRegExp:[JYMCRegExpParser fsRegExp]];
    if (realString.length == 0) return @"";
    
    id cacheResult = [[JYMCJSExpressionCache shareCache] cacheValueWithJSExpression:realString];
    if (cacheResult && [cacheResult isKindOfClass:NSString.class]) {
        return cacheResult;
    }
    
    NSString* jsCalResult = [JYMCJSExpression fsExpression:realString];
    
    if (hasNull && [jsCalResult isEqualToString:@"null"]) {
        jsCalResult = nil;
    }
    [[JYMCJSExpressionCache shareCache] storeValue:jsCalResult expression:realString];
    
    return jsCalResult;

}

/// 获取js事件对象
+ (JYMCJSAction *)getJSActionWithString:(NSString *)string
                                   info:(JYMCStateInfo *)info
{
    if (string.length == 0) {
        return nil;
    }
        
    // 获取js函数字符串. $f{clickEvent(${name}, world)} -> clickEvent(${name}, world)
    NSString *realString = [JYMCRegExpParser parse:string byRegExp:[JYMCRegExpParser funcRegExp]];
    // 移除空格. clickEvent(${name}, world) -> clickEvent(${name},world)
    realString = [realString stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    // 匹配出函数名和原始参数字符串. clickEvent(${name}, world) -> clickEvent 和 ${name}, world
    NSArray<NSTextCheckingResult *> *matches = [JYMCRegExpParser matchesString:realString byRegExp:[JYMCRegExpParser parseFuncRegExp]];
    if (matches.count == 0) {
        return nil;
    }
    
    JYMCJSAction *jsAction = [[JYMCJSAction alloc] init];
    NSTextCheckingResult *result = matches.firstObject;
    if (result.numberOfRanges == 3) {
        // index = 0: 表示所匹配字符串整体
        // index = 1: 方法名称
        // index = 2: 参数
        NSString *methodName = [realString substringWithRange:[result rangeAtIndex:1]];
        NSString *argsStr = [realString substringWithRange:[result rangeAtIndex:2]];
        NSArray *args = [argsStr componentsSeparatedByString:@","];
        // 解析参数
        NSMutableArray *newArgs = [NSMutableArray array];
        for (NSString *argName in args) {
            id argValue = [JYMCDataParser getDynamicValueWithString:argName info:info];
            if (argValue) {
                [newArgs addObject:argValue];
            }
        }
        jsAction.methodName = methodName;
        jsAction.args = newArgs.copy;
    }
    
    if (jsAction.methodName.length == 0) {
        return nil;
    }
    return jsAction;
}

@end

