//
//  NSMutableAttributedString+AttachImg.m
//  AlicloudUTDID
//
//  Created by huangshengzhong118 on 2022/12/12.
//

#import "NSMutableAttributedString+AttachImg.h"

@implementation NSMutableAttributedString (AttachImg)

+ (NSAttributedString *)mc_attachWithImg:(UIImage *)img lineHeight:(CGFloat)lineHeight size:(CGSize)size {
    NSTextAttachment *textAttachment = [[NSTextAttachment alloc] init];
    textAttachment.image = img;
    CGRect frame = CGRectMake(0, (lineHeight - size.height) / 2.0, size.width, size.height);
    textAttachment.bounds = frame;
    return [NSAttributedString attributedStringWithAttachment:textAttachment];
}

@end
