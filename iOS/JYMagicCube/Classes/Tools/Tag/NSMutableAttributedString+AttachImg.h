//
//  NSMutableAttributedString+AttachImg.h
//  AlicloudUTDID
//
//  Created by huangshengzhong118 on 2022/12/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSMutableAttributedString (AttachImg)

+ (NSAttributedString *)mc_attachWithImg:(UIImage *)img lineHeight:(CGFloat)lineHeight size:(CGSize)size;

@end

NS_ASSUME_NONNULL_END
