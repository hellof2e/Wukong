//
//  JYMCDataParser.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2021/8/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

extern NSString *const MCListItemKeyPathPrefix;

@class JYMCStateInfo;

typedef enum : NSUInteger {
    JYMCValueUnitPoint,
    JYMCValueUnitPercent,
} JYMCValueUnit;

typedef enum : NSUInteger {
    JYMCKeyPathTypeNone,        //非keyPath变量
    JYMCKeyPathTypeOrigin,      //原生keyPath变量（非ListItem，非Local）
    JYMCKeyPathTypeListItem,
    JYMCKeyPathTypeLocal,
} JYMCKeyPathType;

@interface JYMCValue : NSObject
@property (assign, nonatomic) CGFloat value;
@property (assign, nonatomic) JYMCValueUnit unit;
@end

@interface JYMCGradient : NSObject
@property (nonatomic, assign) CGPoint startPoint;
@property (nonatomic, assign) CGPoint endPoint;
@property (nonatomic, copy) NSArray<NSNumber *>* locations;
@property (nonatomic, copy) NSArray *colors;
@property (nonatomic, copy) NSString* type;
@end

@interface JYMCDataParser : NSObject

/// 通过字符串获取keyPath
/// - Parameters:
///   - string: 原始字符串
///   - keyPathType: keyPath类型
///   return: 修剪后的keyPath
+ (NSString *)getKeyPathFromString:(NSString *)string keyPathType:(JYMCKeyPathType *)keyPathType;

/// 获取动态类型的数据
/// 如果 string 是 ${keyPath} 格式的数据占位符，则提取出 keyPath，并从 data 获取对应的值返回；如果 string 不是数据占位符，直接返回 string
/// @param string 文本字符串
/// @param info 数据源
+ (id)getDynamicValueWithString:(NSString *)string
                           info:(JYMCStateInfo *)info;

/// 获取 NSString 类型的数据
/// 如果 string 是 ${keyPath} 格式的数据占位符，则提取出 keyPath，并从 data 获取对应的值返回；如果 string 不是数据占位符，直接返回 string
/// @param string 文本字符串
/// @param info 数据源
+ (NSString * _Nullable)getStringValueWithString:(NSString *)string
                                            info:(JYMCStateInfo *)info;

/// 获取 NSArray 类型的数据
/// @param string 文本字符串
/// @param info 数据源
+ (NSArray * _Nullable)getArrayValueWithString:(NSString *)string
                                          info:(JYMCStateInfo *)info;

/// 获取 NSDictionary 类型的数据
/// @param idData 原数据（可能为 ${keyPath} 格式的数据占位符，也可能是 NSDictionary 类型的数据）
/// @param info 数据源
+ (NSDictionary<NSString *, NSString *> * _Nullable)getDictionaryValueWithIdData:(id)idData
                                                                            info:(JYMCStateInfo *)info;
/// 获取 CGFloat 类型的像素数据（不支持动态表达式）
/// 根据 “带单位的数值字符串”（如“6px”、“10rpx”） 获取对应的数值，如果 string 不是带单位的数值字符串，则返回 0
/// @param string 带单位的数值字符串
+ (CGFloat)getFloatPixelWithString:(NSString *)string;

/// 获取 CGFloat 类型的像素数据（支持动态表达式）
+ (CGFloat)getFloatPixelWithString:(NSString *)string info:(JYMCStateInfo *)info;

/// 获取 CGFloat 类型的数据（支持动态表达式）
/// 获取对应的数值，如果 string，则返回 0，按照atof
+ (CGFloat)getFloatWithString:(NSString *)string info:(JYMCStateInfo *)info;

/// 获取 double 类型的数据（支持动态表达式）
+ (double)getDoubleWithString:(NSString *)string info:(JYMCStateInfo *)info;

/// 获取 带单位的数据类型 类型的数据
/// 根据 “带单位的数值字符串”（如“6px”、“10rpx”、"100%"） 获取对应的数值，如果 string 不是带单位的数值字符串，则返回 0
/// @param string 带单位的数值字符串
+ (JYMCValue *)getValueWithString:(NSString *)string;

/// 获取颜色
/// @param string 颜色字符串或数据占位符，如果是数据占位符从 data 中获取颜色字符串并转成对应的颜色
/// @param info 数据源
+ (UIColor * _Nullable)getColorValueWithString:(NSString *)string
                                          info:(JYMCStateInfo *)info;

/// 获取渐变色
+ (JYMCGradient * _Nullable)getGradientColorsWithString:(NSString *)string
                                                   info:(JYMCStateInfo *)info;

/// 获取字体
/// @param fontName 字体名字
/// @param fontSize 字体大小
/// @param fontWeight 字体粗细
+ (UIFont * _Nullable)getFontValueWithFontName:(NSString *)fontName
                                      fontSize:(CGFloat)fontSize
                                    fontWeight:(NSString * _Nullable)fontWeight;

/// 获取下划线风格
/// @param string 文本字符串
/// @param info 数据源
+ (NSUnderlineStyle)getUnderlineStyleValueWithString:(NSString *)string
                                                info:(JYMCStateInfo *)info;

/// 获取视图的 contentMode
/// @param string 文本字符串
+ (UIViewContentMode)getViewContentModeValueWithString:(NSString *)string;

/// 获取寬高比
+ (CGFloat)aspectRatioWithURLString:(NSString *)urlString;

/// 获取query参数
+ (NSDictionary<NSString *, NSString *> *)queryItemsWithURL:(NSURL *)url;

@end

NS_ASSUME_NONNULL_END
