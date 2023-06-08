//
//  JYMCJSExpressionParser.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/13.
//


#import <Foundation/Foundation.h>
@class JYMCJSAction;
@class JYMCStateInfo;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCJSExpressionParser : NSObject


/// 获取m-if结果
/// @param string m-if字符串
/// @param info 数据源
/// @return m-if表达式结果
+ (BOOL)getMIFValueWithString:(NSString *)string
                         info:(JYMCStateInfo *)info;


/// 是否为js表达式
/// @param string js表达式字符串
+ (BOOL)isStringJSExpression:(NSString *)string;


/// 获取m-if结果
/// @param string m-if字符串
/// @param info 数据源
/// @return m-if表达式结果
+ (NSString *)stringWithExpression:(NSString *)string
                              info:(JYMCStateInfo *)info;

/// 获取js事件对象
/// @param string js表达式字符串
/// @param info 数据源
+ (JYMCJSAction *)getJSActionWithString:(NSString *)string
                                   info:(JYMCStateInfo *)info;

@end

NS_ASSUME_NONNULL_END
