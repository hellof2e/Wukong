//
//  JYMCConfigure+Private.m
//  JYMagicCube
//
//  Created by 张新令 on 2021/3/20.
//

#import "JYMCJSExpression.h"

@import JavaScriptCore;

@interface JYMCJSExpression ()
@property (nonatomic, strong) JSContext* jsContext;
@property (nonatomic, strong) dispatch_queue_t runtimeQueue;
@end

@implementation JYMCJSExpression
+ (instancetype)shareInstance {
    static dispatch_once_t onceToken;
    static JYMCJSExpression* ins = nil;
    dispatch_once(&onceToken, ^{
        ins = [[JYMCJSExpression alloc] init];
    });
    return ins;
}

+ (dispatch_queue_t)runtimeQueue
{
    return [JYMCJSExpression shareInstance].runtimeQueue;
}

+ (JYMCExpressionReturnType)typeWithExpressionDesc:(NSString *)expressionDesc
{
    JYMCExpressionReturnType type = JYMCExpressionReturnUnkownType;
    if ([expressionDesc rangeOfString:@"$fb{"].location != NSNotFound) {
        type = JYMCExpressionReturnBOOLType;
    } else if ([expressionDesc rangeOfString:@"$fs{"].location != NSNotFound) {
        type = JYMCExpressionReturnStringType;
    } else if ([expressionDesc rangeOfString:@"$fi{"].location != NSNotFound) {
        type = JYMCExpressionReturnIntegerType;
    } else if ([expressionDesc rangeOfString:@"$ff{"].location != NSNotFound) {
        type = JYMCExpressionReturnDoubleType;
    } else if ([expressionDesc rangeOfString:@"$fa{"].location != NSNotFound) {
        type = JYMCExpressionReturnArrayType;
    } else if ([expressionDesc rangeOfString:@"$fm{"].location != NSNotFound) {
        type = JYMCExpressionReturnMapType;
    }
    return type;
}

+ (BOOL)fbExpression:(NSString *)expression
{
    JSValue* value = [self evaluateExpression:expression];
    return [self valueToBool:value];
}

+ (NSString *)fsExpression:(NSString *)expression
{
    JSValue* value = [self evaluateExpression:expression];
    return [self valueToString:value];
}

+ (NSInteger)fiExpression:(NSString *)expression
{
    JSValue* value = [self evaluateExpression:expression];
    return [self valueToInteger:value];
}

+ (double)ffExpression:(NSString *)expression
{
    JSValue* value = [self evaluateExpression:expression];
    return [self valueToDouble:value];
}

+ (NSArray *)faExpression:(NSString *)expression {
    JSValue* value = [self evaluateExpression:expression];
    if (value.isArray) {
        return value.toArray;
    }
    return @[];
}

+ (NSDictionary *)fmExpression:(NSString *)expression {
    JSValue* value = [self evaluateExpression:expression];
    if (value.isObject) {
        return value.toDictionary;
    }
    return @{};
}


+ (JSValue *)evaluateExpression:(NSString *)expression
{
    return [[JYMCJSExpression shareInstance].jsContext evaluateScript:expression];
}

+ (BOOL)valueToBool:(JSValue *)value {
    BOOL ret = NO;
    do {
        if (value.isBoolean) {
            ret = value.toBool;
            break;;
        }

        if (value.isString) {
            ret = [value.toString boolValue];
            break;
        }
        
        if (value.isNumber) {
            ret = value.toNumber.boolValue;
            break;
        }
    } while (0);
        
    return ret;
}

+ (NSString *)valueToString:(JSValue *)value {
    NSString* ret = @"";
    do {
        if (value.isBoolean || value.isString || value.isNumber) {
            ret = value.toString;
            break;;
        }

    } while (0);
        
    return ret;
}

+ (NSInteger)valueToInteger:(JSValue *)value {
    NSInteger ret = 0;
    do {
        if (value.isBoolean || value.isNumber) {
            ret = value.toInt32;
            break;;
        }

        if (value.isString) {
            ret = [[value toString] integerValue];
            break;
        }
    } while (0);
        
    return ret;
}

+ (double)valueToDouble:(JSValue *)value {
    double ret = 0.0f;
    do {
        if (value.isBoolean || value.isNumber) {
            ret = value.toDouble;
            break;;
        }

        if (value.isString) {
            ret = [[value toString] doubleValue];
            break;
        }
    } while (0);
        
    return ret;
}


#pragma mark -- getter
- (JSContext *)jsContext {
    if (!_jsContext) {
        _jsContext = [[JSContext alloc] init];
    }
    return _jsContext;;
}

- (dispatch_queue_t)runtimeQueue {
    if (!_runtimeQueue) {
        _runtimeQueue = dispatch_queue_create("com.JYMagicCube.JYMCJSExpression", DISPATCH_QUEUE_CONCURRENT);
    }
    return _runtimeQueue;
}

@end
