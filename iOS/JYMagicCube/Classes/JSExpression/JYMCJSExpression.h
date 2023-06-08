//
//  JYMCConfigure+Private.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    JYMCExpressionReturnUnkownType,
    JYMCExpressionReturnBOOLType,
    JYMCExpressionReturnStringType,
    JYMCExpressionReturnIntegerType,
    JYMCExpressionReturnDoubleType,
    JYMCExpressionReturnArrayType,
    JYMCExpressionReturnMapType,
} JYMCExpressionReturnType;

@interface JYMCJSExpression : NSObject

+ (dispatch_queue_t)runtimeQueue;

+ (JYMCExpressionReturnType)typeWithExpressionDesc:(NSString *)expressionDesc;

+ (BOOL)fbExpression:(NSString *)expression;

+ (NSString *)fsExpression:(NSString *)expression;

+ (NSInteger)fiExpression:(NSString *)expression;

+ (double)ffExpression:(NSString *)expression;

+ (NSArray *)faExpression:(NSString *)expression;

+ (NSDictionary *)fmExpression:(NSString *)expression;

@end

NS_ASSUME_NONNULL_END
