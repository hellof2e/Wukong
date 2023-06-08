//
//  JYMCLayout.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCLayout : NSObject

@property (nonatomic, copy) NSString *flexDirection;
@property (nonatomic, copy) NSString *justifyContent;
@property (nonatomic, copy) NSString *alignContent;
@property (nonatomic, copy) NSString *alignItems;
@property (nonatomic, copy) NSString *alignSelf;
@property (nonatomic, copy) NSString *flexWrap;
@property (nonatomic, assign) CGFloat flex;
@property (nonatomic, assign) CGFloat flexGrow;
@property (nonatomic, assign) CGFloat flexShrink;

/// 以下属性皆为 “带单位的数值字符串”，例如 “6px”、"10rpx"

@property (nonatomic, copy) NSString *marginLeft;
@property (nonatomic, copy) NSString *marginTop;
@property (nonatomic, copy) NSString *marginRight;
@property (nonatomic, copy) NSString *marginBottom;
@property (nonatomic, copy) NSString *marginHorizontal;
@property (nonatomic, copy) NSString *marginVertical;

@property (nonatomic, copy) NSString *paddingLeft;
@property (nonatomic, copy) NSString *paddingTop;
@property (nonatomic, copy) NSString *paddingRight;
@property (nonatomic, copy) NSString *paddingBottom;
@property (nonatomic, copy) NSString *paddingHorizontal;
@property (nonatomic, copy) NSString *paddingVertical;

@property (nonatomic, copy) NSString *width;
@property (nonatomic, copy) NSString *height;
@property (nonatomic, copy) NSString *minWidth;
@property (nonatomic, copy) NSString *minHeight;
@property (nonatomic, copy) NSString *maxWidth;
@property (nonatomic, copy) NSString *maxHeight;

@property (nonatomic, copy) NSString *position;
@property (nonatomic, copy) NSString *left;
@property (nonatomic, copy) NSString *right;
@property (nonatomic, copy) NSString *top;
@property (nonatomic, copy) NSString *bottom;

/// 文本对齐方式
@property (nonatomic, copy) NSString *textAlign;
/// 是否裁切，默认YES
@property (nonatomic, assign) BOOL clipChildren;

/// 宽高比
@property (nonatomic, assign) NSString *aspectRatio;
@end

NS_ASSUME_NONNULL_END
