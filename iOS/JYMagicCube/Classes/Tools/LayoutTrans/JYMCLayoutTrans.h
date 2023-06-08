//
//  JYMCLayoutTrans.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/2/8.
//

#import <Foundation/Foundation.h>
#import <Yoga/YGEnums.h>

NS_ASSUME_NONNULL_BEGIN

YGFlexDirection YGFlexDirectionFromString(NSString *str);
YGJustify YGJustifyFromString(NSString *str);
YGAlign YGAlignFromString(NSString *str);
YGWrap YGWrapFromString(NSString *str);

YGPositionType YGPositionTypeFromString(NSString *str);

/// 获取Text视图的对齐方式
/// @param str 文本字符串
NSTextAlignment NSTextAlignmentFromString(NSString *str);

NS_ASSUME_NONNULL_END
