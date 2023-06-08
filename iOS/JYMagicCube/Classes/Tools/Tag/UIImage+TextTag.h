//
//  UIImage+TextTag.h
//  AlicloudUTDID
//
//  Created by huangshengzhong118 on 2022/12/12.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (TextTag)

+ (UIImage *)drawAttText:(NSAttributedString *)attrStr
             borderColor:(UIColor *)borderColor
             borderWidth:(CGFloat)borderWidth
                  radius:(CGFloat)radius
                  insets:(UIEdgeInsets)insets;

+ (UIImage *)createImageWithColor:(UIColor *)color
                            frame:(CGRect)rect;

@end

NS_ASSUME_NONNULL_END
