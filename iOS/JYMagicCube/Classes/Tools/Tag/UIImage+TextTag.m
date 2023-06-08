//
//  UIImage+TextTag.m
//  AlicloudUTDID
//
//  Created by huangshengzhong118 on 2022/12/12.
//

#import "UIImage+TextTag.h"

@implementation UIImage (TextTag)


+ (UIImage *)drawAttText:(NSAttributedString *)attrStr
             borderColor:(UIColor *)borderColor
             borderWidth:(CGFloat)borderWidth
                  radius:(CGFloat)radius
                  insets:(UIEdgeInsets)insets {
    
    CGSize strSize = [attrStr boundingRectWithSize:CGSizeMake(CGFLOAT_MAX, CGFLOAT_MAX)
                                            options:NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading
                                            context:nil].size;
    CGSize drawSize = CGSizeMake(strSize.width + insets.left + insets.right + borderWidth,
                        strSize.height + insets.top + insets.bottom + borderWidth);
    
    UIGraphicsBeginImageContextWithOptions(drawSize, NO, 0);
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    void (^drawBlock)(CGRect rrect,
                      CGFloat radiusTopLeft,
                      CGFloat radiusTopRight,
                      CGFloat radiusBottomLeft,
                      CGFloat radiusBottomRight) = ^(CGRect rrect,
                                                    CGFloat radiusTopLeft,
                                                    CGFloat radiusTopRight,
                                                    CGFloat radiusBottomLeft,
                                                    CGFloat radiusBottomRight){
        CGFloat
        minx = CGRectGetMinX(rrect),
        midx = CGRectGetMidX(rrect),
        maxx = CGRectGetMaxX(rrect);
        CGFloat
        miny = CGRectGetMinY(rrect),
        midy = CGRectGetMidY(rrect),
        maxy = CGRectGetMaxY(rrect);
        CGContextMoveToPoint(ctx, minx, midy);
        CGContextAddArcToPoint(ctx, minx, miny, midx, miny, radiusTopLeft);
        CGContextAddArcToPoint(ctx, maxx, miny, maxx, midy, radiusTopRight);
        CGContextAddArcToPoint(ctx, maxx, maxy, midx, maxy, radiusBottomLeft);
        CGContextAddArcToPoint(ctx, minx, maxy, minx, midy, radiusBottomRight);
        CGContextClosePath(ctx);
        CGContextDrawPath(ctx, kCGPathFillStroke);
    };
    
    CGFloat r = 0, g, b, a;
    if (borderColor) {
        [borderColor getRed:&r green:&g blue:&b alpha:&a];
        CGContextSetLineWidth(ctx, borderWidth);
        CGContextSetRGBStrokeColor(ctx, r, g, b, a);
    } else {
        CGContextSetLineWidth(ctx, 0);
        CGContextSetRGBStrokeColor(ctx, 0, 0, 0, 0);
    }
    CGContextSetRGBFillColor(ctx, 0, 0, 0, 0);
    
    CGRect rrect = CGRectMake(borderWidth / 2,
                              borderWidth / 2,
                              drawSize.width - borderWidth,
                              drawSize.height - borderWidth);
    
    drawBlock(rrect,
              radius,
              radius,
              radius,
              radius);
    
    [attrStr drawInRect:CGRectMake(insets.left + borderWidth / 2,
                                   insets.top + borderWidth / 2,
                                   strSize.width,
                                   strSize.height)];
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}


+ (UIImage *)createImageWithColor:(UIColor *)color frame:(CGRect)rect {
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return theImage;
}

@end
