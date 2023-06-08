//
//  JYMCJSExpressionCache.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/17.
//

#import <Foundation/Foundation.h>


NS_ASSUME_NONNULL_BEGIN

@interface JYMCJSExpressionCache : NSObject

/// 获取单例
+ (instancetype)shareCache;

/// 查询缓存中的JS表达式结果
/// @param expression 解析后的JS表达式
/// @return 缓存的JS表达式计算结果，nil表示无缓存
- (id)cacheValueWithJSExpression:(NSString *)expression;

/// 存储JS表达式结果
/// @param value JS表达式计算结果
/// @param expression 解析后的JS表达式
- (void)storeValue:(id)value expression:(NSString *)expression;

@end

NS_ASSUME_NONNULL_END

