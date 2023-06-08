//
//  JYMCViewStyle.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/4.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
/// 圆角类型
typedef NS_ENUM(NSInteger, JYMCCornerRadiusType) {
    JYMCCornerRadiusTypeNone        = 0, //无圆角
    JYMCCornerRadiusTypeAll         = 1, //全圆角
    JYMCCornerRadiusTypePart        = 2, //部分圆角
};


@interface JYMCViewStyle : NSObject

/// 背景色
@property (nonatomic, copy) NSString *background;
/// 边框颜色
@property (nonatomic, copy) NSString *borderColor;

/// 边框样式
@property (nonatomic, copy) NSString *borderStyle;
/// 边框宽度，带单位的数值字符串
@property (nonatomic, copy) NSString *borderWidth;
/// 圆角度数，带单位的数值字符串/* 使用 radiusType & cornerRadiusArr 代替 */
@property (nonatomic, copy) NSString *cornerRadius;

///透明度
@property (nonatomic, copy) NSString *opacity;

#pragma mark -- 自定义
///圆角类型
@property (nonatomic, assign, readonly) JYMCCornerRadiusType radiusType;
///各圆角数据
@property (nonatomic, copy, nullable, readonly) NSArray *cornerRadiusArr;

@end

NS_ASSUME_NONNULL_END
