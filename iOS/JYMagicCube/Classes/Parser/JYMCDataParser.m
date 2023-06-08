//
//  JYMCDataParser.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/20.
//

#import "JYMCDataParser.h"
#import <JYMagicCube/JYMCTracker.h>
#import <YYCategories/YYCategories.h>
#import <MJExtension/MJExtension.h>
#import "JYMCJSExpressionParser.h"
#import "JYMCConfigure+Private.h"
#import "JYMCLocalParameters.h"
#import "JYMCStateInfo.h"
#import "JYMCRegExpParser.h"

NSString * const MCListItemKeyPathPrefix = @"listItem.";
static NSString * const MCListItem = @"listItem";
static NSString * const MCListItemIndex = @"listItemIndex";
static NSString * const MCLocalKeyPathPrefix = @"LOCAL.";
static NSString * const MCFuncSuffix = @"()";
static NSString * const MCBuiltInFuncSize = @"size()";

/// 内置函数类型
typedef enum : NSInteger {
    JYMCBuiltInFuncTypeOther = -1,   // 其他类型函数
    JYMCBuiltInFuncTypeSize  = 0,    // 函数：size(), 返回值类型：NSNumber
} JYMCBuiltInFuncType;


@implementation JYMCValue
@end

@implementation JYMCGradient
@end
@implementation JYMCDataParser

#pragma mark - Public

/// 通过字符串获取keyPath
/// - Parameters:
///   - string: 原始字符串
///   - keyPathType: keyPath类型
///   return: 修剪后的keyPath
+ (NSString *)getKeyPathFromString:(NSString *)string keyPathType:(JYMCKeyPathType *)keyPathType
{
    if (string.length == 0) {
        return nil;
    }
    
    // 如不包含“${”,证明非表达式
    // rangeOfString的性能为正则性能的8倍
    // ${taskStatus} 使用 [JYMCRegExpParser keyPathRegExp]匹配 1000次，耗时3.09ms
    // ${taskStatus} 使用 rangeOfString查找执行 1000次，耗时 0.409ms
    if ([string rangeOfString:@"${"].location == NSNotFound) {
        return nil;
    }
    
    NSString *keyPath = [JYMCRegExpParser parse:string byRegExp:[JYMCRegExpParser keyPathRegExp]];
    if (keyPath.length == 0) {
        return nil;
    }
        
    *keyPathType = JYMCKeyPathTypeOrigin;
    
    if ([keyPath hasPrefix:MCListItemKeyPathPrefix]) {
        *keyPathType = JYMCKeyPathTypeListItem;
        return [keyPath substringFromIndex:MCListItemKeyPathPrefix.length];
    }
    
    if ([keyPath hasPrefix:MCLocalKeyPathPrefix]) {
        *keyPathType = JYMCKeyPathTypeLocal;
        return [keyPath substringFromIndex:MCLocalKeyPathPrefix.length];
    }
    
    return keyPath;
}

+ (NSString *)getStringValueWithString:(NSString *)string
                                  info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return nil;
    }
    
    if ([JYMCJSExpressionParser isStringJSExpression:string]) {
        return [JYMCJSExpressionParser stringWithExpression:string info:info];
    }
    
    JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
    NSString *keyPath = [self getKeyPathFromString:string keyPathType:&keyPathType];
    if (keyPath.length > 0) {
        NSObject *value = [self getDynamicValueWithKeyPath:keyPath info:info isListitemKey:keyPathType == JYMCKeyPathTypeListItem];
        if ([value isKindOfClass:[NSString class]]) {
            return (NSString *)value;
        } else if ([value isKindOfClass:[NSNumber class]]) {
            NSNumber *num = (NSNumber *)value;
            return [JYMCDataParser formatWithDecimalNumber:num];
        }
    } else {
        return string;
    }
    return nil;
}

+ (id)getDynamicValueWithString:(NSString *)string
                           info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return nil;
    }
    
    if ([JYMCJSExpressionParser isStringJSExpression:string]) {
        return [JYMCJSExpressionParser stringWithExpression:string info:info];
    }
    
    JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
    NSString *keyPath = [self getKeyPathFromString:string keyPathType:&keyPathType];
    if (keyPath.length > 0) {
        if (keyPathType == JYMCKeyPathTypeLocal) {
            NSDictionary *params = [self getLocalParameters];
            return [params objectForKey:keyPath];
        } else if ([keyPath isEqualToString:MCListItem]) {
            return info.listItem;
        } else if ([keyPath isEqualToString:MCListItemIndex]) {
            return @(info.listItemIndex);
        } else {
            NSObject *value = [self getDynamicValueWithKeyPath:keyPath info:info isListitemKey:keyPathType == JYMCKeyPathTypeListItem];
            return value;
        }
    } else {
        return string;
    }
    return nil;
}

+ (NSArray *)getArrayValueWithString:(NSString *)string
                                info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return nil;
    }
    
    JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
    NSString *keyPath = [self getKeyPathFromString:string keyPathType:&keyPathType];
    if (keyPath.length > 0) {
        NSObject *value = [self getDynamicValueWithKeyPath:keyPath info:info isListitemKey:keyPathType == JYMCKeyPathTypeListItem];
        if ([value isKindOfClass:[NSArray class]]) {
            NSArray *array = (NSArray *)value;
            if (array.count > 0) {
                return [array copy];
            }
        }
    }
    return nil;
}

+ (NSDictionary<NSString *, NSString *> *)getDictionaryValueWithIdData:(id)idData
                                                                  info:(JYMCStateInfo *)info
{
    if (idData == nil || info.data.count == 0) {
        return nil;
    }
    
    if ([idData isKindOfClass:[NSString class]]) {
        return [self getDictionaryValueWithString:(NSString *)idData info:info];
    } else if ([idData isKindOfClass:[NSDictionary class]]) {
        return [self getDictionaryValueWithDictionary:(NSDictionary *)idData info:info];
    }
    return nil;
}

+ (CGFloat)getFloatPixelWithString:(NSString *)string
{
    if (string.length == 0) {
        return 0;
    }
    
    NSString *valueString = [string stringByReplacingOccurrencesOfString:@" " withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    if (valueString.length == 0) {
        return 0;
    }
    CGFloat value = 0;
    if ([valueString hasSuffix:@"rpx"]) {
        valueString = [string stringByReplacingOccurrencesOfString:@"rpx" withString:@""];
        value = [valueString floatValue] / 750.0 * kScreenWidth;
        
    } else if ([valueString hasSuffix:@"px"]) {
        valueString = [string stringByReplacingOccurrencesOfString:@"px" withString:@""];
        value = [valueString floatValue];
    }
    
    return value;
}

+ (CGFloat)getFloatPixelWithString:(NSString *)string info:(JYMCStateInfo *)info
{
    CGFloat result = 0.0;
    do {
        NSString *valueString = [self getStringValueWithString:string info:info];
        if (valueString.length == 0) {
            break;
        }
        
        result = [self getFloatPixelWithString:valueString];
        
    } while (0);
    
    return result;
}

+ (CGFloat)getFloatWithString:(NSString *)string info:(JYMCStateInfo *)info  {
    
    CGFloat result = 0.0;
    do {
        if (string.length == 0 || info.data.count == 0) {
            break;
        }

        NSObject *value = [self getDynamicValueWithString:string info:info];
        if (!value) {
            break;
        }
        
        if ([value isKindOfClass:[NSString class]]) {
            result = [(NSString *)value floatValue];
            break;
        }

        if ([value isKindOfClass:[NSNumber class]]) {
            result = [(NSNumber *)value floatValue];
        }
        
    } while (0);
    
    return result;
}

+ (double)getDoubleWithString:(NSString *)string info:(JYMCStateInfo *)info  {
    
    CGFloat result = 0.0;
    do {
        if (string.length == 0 || info.data.count == 0) {
            break;
        }

        NSObject *value = [self getDynamicValueWithString:string info:info];
        if (!value) {
            break;
        }
        
        if ([value isKindOfClass:[NSString class]]) {
            result = [(NSString *)value doubleValue];
            break;
        }

        if ([value isKindOfClass:[NSNumber class]]) {
            result = [(NSNumber *)value doubleValue];
        }
        
    } while (0);
    
    return result;
}

/// 获取 带单位的数据类型 类型的数据
/// 根据 “带单位的数值字符串”（如“6px”、“10rpx”、"100%"） 获取对应的数值，如果 string 不是带单位的数值字符串，则返回 0
/// @param string 带单位的数值字符串
+ (JYMCValue *)getValueWithString:(NSString *)string
{
    JYMCValue* value = [[JYMCValue alloc] init];
    value.unit = JYMCValueUnitPoint;
    value.value = 0;
    if (string.length == 0) {
        return value;
    }
    
    NSString *valueString = [string stringByReplacingOccurrencesOfString:@" " withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    valueString = [valueString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    if (valueString.length == 0) {
        return value;
    }
    if ([valueString hasSuffix:@"rpx"]) {
        valueString = [string stringByReplacingOccurrencesOfString:@"rpx" withString:@""];
        value.value = [valueString floatValue] / 750.0 * kScreenWidth;
        value.unit = JYMCValueUnitPoint;
    } else if ([valueString hasSuffix:@"px"]) {
        valueString = [string stringByReplacingOccurrencesOfString:@"px" withString:@""];
        value.value = [valueString floatValue];
        value.unit = JYMCValueUnitPoint;
    } else if ([valueString hasSuffix:@"%"]) {
        valueString = [string stringByReplacingOccurrencesOfString:@"%" withString:@""];
        value.value = [valueString floatValue];
        value.unit = JYMCValueUnitPercent;
    }
    
    return value;
}

+ (UIColor *)getColorValueWithString:(NSString *)string
                                info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return nil;
    }
    
    NSString *colorString = [self getStringValueWithString:string info:info];
    if (colorString.length > 0) {
        return [UIColor colorWithHexString:colorString];
    }
    return nil;
}

+ (JYMCGradient *)getGradientColorsWithString:(NSString *)string
                                         info:(JYMCStateInfo *)info {
    NSString *colorString = [self getStringValueWithString:string info:info];
    if (colorString.length == 0) {
        return nil;
    }
    
    
    JYMCGradient* gradient = nil;
    if ([colorString containsString:@"linear-gradient("]) {
        NSString* color = [colorString stringByReplacingOccurrencesOfString:@"linear-gradient(" withString:@""];
        color = [[color stringByReplacingOccurrencesOfString:@")" withString:@""] stringByTrim];
        NSArray* colors = [color componentsSeparatedByString:@","];
        if (colors.count >= 2) {
            gradient = [[JYMCGradient alloc] init];

            //方向
            gradient = [self direction:colors.firstObject withGradient:gradient];
            
            //colors and locations
            gradient = [self colorsAndroidLoactions:colors
                                         startIndex:1
                                        withGradient:gradient];
            
            gradient.type = kCAGradientLayerAxial;
        }
//    }
//    else if ([colorString containsString:@"radial-gradient("]) {
//        NSString* color = [colorString stringByReplacingOccurrencesOfString:@"radial-gradient(" withString:@""];
//        color = [[color stringByReplacingOccurrencesOfString:@")" withString:@""] stringByTrim];
//        NSArray* colors = [color componentsSeparatedByString:@","];
//        if (colors.count >= 1) {
//            gradient = [[JYMCGradient alloc] init];
//
//
//            //colors and locations
//            gradient = [self colorsAndroidLoactions:colors
//                                         startIndex:0
//                                        withGradient:gradient];
//            gradient.startPoint = CGPointMake(0.5, 0.5);
//            gradient.endPoint   = CGPointMake(1, 1);
//            gradient.type = kCAGradientLayerRadial;
//        }
    }
    
    return gradient;
}

+ (JYMCGradient *)direction:(NSString *)direction withGradient:(JYMCGradient *)gradient {
    const NSDictionary* s_jymc_directionConfigs = @{
        @"to left":@[@(CGPointMake(1, 0.5)),       @(CGPointMake(0, 0.5))],
        @"to right":@[@(CGPointMake(0, 0.5)),      @(CGPointMake(1, 0.5))],
        @"to bottom":@[@(CGPointMake(0.5, 0)),     @(CGPointMake(0.5, 1))],
        @"to top":@[@(CGPointMake(0.5, 1)),        @(CGPointMake(0.5, 0))],
        @"to right bottom":@[@(CGPointMake(0, 0)), @(CGPointMake(1, 1))],
        @"to right top":@[@(CGPointMake(0, 1)),    @(CGPointMake(1, 0))],
        @"to left top":@[@(CGPointMake(1, 1)),     @(CGPointMake(0, 0))],
        @"to left bottom":@[@(CGPointMake(1, 0)),  @(CGPointMake(0, 1))],
    };
    
    NSArray* directions = s_jymc_directionConfigs[direction];
    if (directions.count == 2) {
        gradient.startPoint = [directions.firstObject CGPointValue];
        gradient.endPoint   = [directions.lastObject CGPointValue];
    } else {
        gradient.startPoint = CGPointMake(0.5, 0);
        gradient.endPoint   = CGPointMake(0.5, 1);
    }
    
    return gradient;
}

+ (JYMCGradient *)colorsAndroidLoactions:(NSArray *)colorParagraphs
                              startIndex:(NSUInteger)index
                            withGradient:(JYMCGradient *)gradient {
    
    NSMutableArray* colors = [[NSMutableArray alloc] init];
    NSMutableArray* locations = [[NSMutableArray alloc] init];
    for (NSUInteger i = index; i < colorParagraphs.count; i ++) {
        NSArray* colorPair = [[colorParagraphs[i] stringByTrim] componentsSeparatedByString:@" "];
        UIColor* color = [UIColor colorWithHexString:colorPair.firstObject];
        if (color) {
            [colors addObject:(id)color.CGColor];
        }
        if ([colorPair.lastObject floatValue]) {
            CGFloat loc = [colorPair.lastObject floatValue] * 0.01;
            [locations addObject:@(loc)];
        }
    }
    gradient.colors = colors;
    if (locations.count > 0 && locations.count == colors.count) {
        gradient.locations = locations;
    }
    return gradient;
}

+ (UIFont *)getFontValueWithFontName:(NSString *)fontName
                            fontSize:(CGFloat)fontSize
                          fontWeight:(NSString *)fontWeight
{
    if (fontName.length > 0) {
        // 使用font-family忽略fontWeight
        UIFont *font = [UIFont fontWithName:fontName size:fontSize];
        if (font) return font;
    }

    if (fontWeight.length > 0) {
        if ([fontWeight isEqualToString:@"normal"]) {
            return [UIFont systemFontOfSize:fontSize];
        } else if ([fontWeight isEqualToString:@"regular"]) {
            return [UIFont systemFontOfSize:fontSize weight:UIFontWeightRegular];
        } else if ([fontWeight isEqualToString:@"medium"]) {
            return [UIFont systemFontOfSize:fontSize weight:UIFontWeightMedium];
        } else if ([fontWeight isEqualToString:@"semibold"]) {
            return [UIFont systemFontOfSize:fontSize weight:UIFontWeightSemibold];
        } else if ([fontWeight isEqualToString:@"bold"]) {
            return [UIFont boldSystemFontOfSize:fontSize];
        }
    }
    return [UIFont systemFontOfSize:fontSize];
}

+ (NSUnderlineStyle)getUnderlineStyleValueWithString:(NSString *)string
                                                info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return NSUnderlineStyleSingle;
    }
    
    NSString *decorationStyle = [self getStringValueWithString:string info:info];
    if ([decorationStyle isEqualToString:@"solid"]) {
        return NSUnderlineStyleSingle;
    } else if ([decorationStyle isEqualToString:@"double"]) {
        return NSUnderlineStyleDouble;
    }
    return NSUnderlineStyleSingle;
}

+ (UIViewContentMode)getViewContentModeValueWithString:(NSString *)string
{
    if (string.length == 0) {
        return UIViewContentModeScaleAspectFit;
    }
    
    if ([string isEqualToString:@"fill"]) {
        return UIViewContentModeScaleToFill;
    } else if ([string isEqualToString:@"contain"]) {
        return UIViewContentModeScaleAspectFit;
    } else if ([string isEqualToString:@"cover"]) {
        return UIViewContentModeScaleAspectFill;
    }
    return UIViewContentModeScaleAspectFit;
}

#pragma mark - Private

/// 根据路径从数据源中取值
/// @param keyPath 数据路径
/// @param data 数据源
+ (id)getDynamicValueWithKeyPath:(NSString *)keyPath
                            info:(JYMCStateInfo *)infoData
                   isListitemKey:(BOOL)isListitemKey
{
    NSDictionary* data = isListitemKey ? infoData.listItem : infoData.data;
    if (keyPath.length == 0 || data.count == 0) {
        return nil;
    }
    
    // 支持下角标取数组值
    // etc: apple[0].name ==> apple.0.name
    keyPath = [keyPath stringByReplacingOccurrencesOfString:@"[" withString:@"."];
    keyPath = [keyPath stringByReplacingOccurrencesOfString:@"]" withString:@""];
    
    NSArray *keyPathArray = [keyPath componentsSeparatedByString:@"."];
    NSObject *tempData = data;
    for (NSInteger i = 0; i < keyPathArray.count; i++) {
        NSString *key = keyPathArray[i];
        if (key.length == 0) {
            continue;
        }
        
        if ([key isEqualToString:MCListItemIndex]) {
            return @(infoData.listItemIndex);
        } else if ([key isEqualToString:MCBuiltInFuncSize]) {
            id value = [self execBuiltInFunc:JYMCBuiltInFuncTypeSize value:tempData];
            return value;
        }
        
        NSObject *value = nil;
        if (tempData) {
            if ([tempData isKindOfClass:[NSDictionary class]]) {
                NSDictionary *tempDictionary = (NSDictionary *)tempData;
                value = [tempDictionary objectForKey:key];
            } else if ([tempData isKindOfClass:[NSArray class]]) {
                NSArray *tempArray = (NSArray *)tempData;
                NSScanner *scan = [NSScanner scannerWithString:key];
                NSInteger index = -1;
                if ([scan scanInteger:&index] && [scan isAtEnd]) {
                    if (index >= 0 && index < tempArray.count) {
                        value = tempArray[index];
                    }
                }
            }
        }
                
        if (i == keyPathArray.count - 1) {
            return value;
        } else {
            tempData = value;
        }
    }
    
    return nil;
}

+ (NSDictionary<NSString *, NSString *> *)getDictionaryValueWithString:(NSString *)string
                                                                  info:(JYMCStateInfo *)info
{
    if (string.length == 0 || info.data.count == 0) {
        return nil;
    }
    
    JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
    NSString *keyPath = [self getKeyPathFromString:string keyPathType:&keyPathType];
    if (keyPath.length > 0) {
        NSObject *value = [self getDynamicValueWithKeyPath:keyPath info:info isListitemKey:keyPathType == JYMCKeyPathTypeListItem];
        if ([value isKindOfClass:[NSDictionary class]]) {
            NSDictionary *dic = (NSDictionary *)value;
            NSMutableDictionary *newDic = [NSMutableDictionary dictionary];
            [dic.allValues enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                if ([obj isKindOfClass:[NSString class]]) {
                    [newDic setObject:obj forKey:dic.allKeys[idx]];
                } else if ([obj isKindOfClass:[NSNumber class]]) {
                    NSNumber *num = (NSNumber *)obj;
                    NSString *string = nil;
                    string = [JYMCDataParser formatWithDecimalNumber:num];
                    [newDic setObject:(string?:@"") forKey:dic.allKeys[idx]];
                }
            }];
            
            if (newDic.count > 0) {
                return [newDic copy];
            }
        }
    }
    return nil;
}

+ (NSDictionary<NSString *, NSString *> *)getDictionaryValueWithDictionary:(NSDictionary *)dic
                                                                      info:(JYMCStateInfo *)info
{
    if (dic.count == 0 || info.data.count == 0) {
        return nil;
    }
    
    NSMutableDictionary *newDic = [NSMutableDictionary dictionary];
    [dic.allValues enumerateObjectsUsingBlock:^(id _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[NSString class]]) {
            NSString *string = (NSString *)obj;
            if (string.length > 0) {
                NSString* value = [self getStringValueWithString:string info:info];
                if (value) {
                    [newDic setObject:value forKey:dic.allKeys[idx]];
                }
            }
        }
    }];
    
    if (newDic.count > 0) {
        return [newDic copy];
    }
    return nil;
}

+ (NSDictionary *)getLocalParameters
{
    JYMCLocalParameters *localParameters = [[JYMCLocalParameters alloc] init];
    JYMCLocalParametersBlock parametersHandler = [JYMCConfigure parametersHandler];
    if (parametersHandler) {
        parametersHandler(localParameters);
    }
    return [localParameters mj_keyValues];
}

+ (CGFloat)aspectRatioWithURLString:(NSString *)urlString {
    NSDictionary* queryItems = [self queryItemsWithURL:[NSURL URLWithString:urlString]];
    CGFloat width = [queryItems floatValueForKey:@"mc_width" default:0.0];
    CGFloat height = [queryItems floatValueForKey:@"mc_height" default:0.0];
    
    CGFloat ratio = 0.0;
    if (height > 0.0 && width > 0.0) {
        ratio = width / height;
    }
    return ratio;
}


+ (NSDictionary<NSString *, NSString *> *)queryItemsWithURL:(NSURL *)url {
    if (!url.absoluteString.length) {
        return nil;
    }
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    NSURLComponents* components = [NSURLComponents componentsWithString:url.absoluteString];
    [components.queryItems enumerateObjectsUsingBlock:^(NSURLQueryItem * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (obj.name) {
            [params setObject:obj.value ?: @"" forKey:obj.name];
        }
    }];
    return params.copy;
}

// NSNumber保持精度转换成字符串
+ (NSString *)formatWithDecimalNumber:(NSNumber *)number {
    if (!number) {
        return @"";
    }
    
    if ([number isKindOfClass:[NSString class]]) {
        return (NSString *)number;
    }
    
    double doubleValue = [number doubleValue];
    NSString *doubleStr = [NSString stringWithFormat:@"%f", doubleValue];
    NSDecimalNumber *decimalNumber = [NSDecimalNumber decimalNumberWithString:doubleStr];
    return [decimalNumber stringValue];
}

/// 执行内置函数
/// - Parameters:
///   - funcType: 函数类型
///   - value: 执行函数的对象
+ (id)execBuiltInFunc:(JYMCBuiltInFuncType)funcType value:(id)value
{
    id tempValue = value;
    switch (funcType) {
        case JYMCBuiltInFuncTypeSize:
        {
            if (tempValue == nil) {
                tempValue = @(0);
            } else if ([tempValue isKindOfClass:[NSString class]]) {
                tempValue = @(((NSString *)value).length);
            } else if ([value isKindOfClass:[NSArray class]]) {
                tempValue = @(((NSArray *)value).count);
            } else if ([value isKindOfClass:[NSDictionary class]]) {
                tempValue = @(((NSDictionary *)value).count);
            } else {
                tempValue = @(0);
            }
        }
            break;
        default:
            break;
    }
    return tempValue;
}

@end
