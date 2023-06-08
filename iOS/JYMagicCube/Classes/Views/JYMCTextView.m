//
//  JYMCTextView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/5.
//

#import "JYMCTextView.h"
#import "JYMCTextElement.h"
#import "JYMCDataParser.h"
#import "JYMCLayoutTrans.h"
#import "JYMCTextContent.h"
#import "JYMCStateInfo.h"
#import "UIImage+TextTag.h"
#import "NSMutableAttributedString+AttachImg.h"

#import <YogaKit/UIView+Yoga.h>
#import <YYCategories/YYCategories.h>
#import <SDWebImage/SDWebImageDownloader.h>

@interface JYMCTextView ()

@property (nonatomic, strong) UILabel *label;

@end

@implementation JYMCTextView

#pragma mark - Public

- (void)applyElement:(JYMCTextElement *)element {
    [super applyElement:element];
    
    self.label.numberOfLines = element.numberOfLines;
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if (self.element.layout.textAlign.length > 0 && self.element.layout.width.length > 0) {
        self.label.textAlignment = NSTextAlignmentFromString(self.element.layout.textAlign);
        self.label.yoga.width = YGPercentValue(100);
    }

    if ([self.element isKindOfClass:[JYMCTextElement class]]) {
        JYMCTextElement *textElement = (JYMCTextElement *)self.element;
        NSAttributedString *textString = [self attWithElement:textElement info:info];
        self.label.attributedText = textString;
    }
    
    /// ⭐️⭐️⭐️ 这里必须要将 label 设置为 dirty，这样父视图刷新布局时，才会重新计算 label 的宽度 ⭐️⭐️⭐️
    [self.label.yoga markDirty];
}

#pragma mark - Private

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.label];
    [self.label configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
    }];
}

- (void)downloadImgWithContent:(JYMCTextContent *)content url:(NSString *)parseUrl element:(JYMCTextElement *)textElement info:(JYMCStateInfo *)info {
    [[SDWebImageDownloader sharedDownloader] downloadImageWithURL:[NSURL URLWithString:parseUrl] completed:^(UIImage * _Nullable image, NSData * _Nullable data, NSError * _Nullable error, BOOL finished) {
        content.downFinished = YES;
        content.tagImg = image;
        NSAttributedString *att = [self attWithElement:textElement info:info];
        self.label.attributedText = att;
    }];
}

- (NSAttributedString *)attWithElement:(JYMCTextElement *)textElement info:(JYMCStateInfo *)info {
    NSMutableAttributedString *textString = [[NSMutableAttributedString alloc] init];
    
    UIImage *tagImg = nil;
    CGSize tagSize = CGSizeZero;
    for (JYMCTextContent *content in textElement.text) {
        if ([content tagContent]) {// 图片标签
            NSString *parseUrl = [JYMCDataParser getStringValueWithString:content.url info:info];
            if (parseUrl.length) {
                CGFloat width = [JYMCDataParser getFloatPixelWithString:content.width];
                CGFloat height = [JYMCDataParser getFloatPixelWithString:content.height];
                
                tagSize = CGSizeMake(width, height);
                tagImg = content.tagImg ? content.tagImg : [UIImage createImageWithColor:[UIColor clearColor] frame:CGRectMake(0, 0, width, height)];
                if (!content.downFinished) [self downloadImgWithContent:content url:parseUrl element:textElement info:info];
                continue;
            }
        }
        // 文本
        NSString *text = [JYMCDataParser getStringValueWithString:content.content info:info];
        if (text.length > 0) {
            NSMutableAttributedString *tempString = [[NSMutableAttributedString alloc] initWithString:text];
            NSRange range = NSMakeRange(0, tempString.length);
            
            // 字体颜色
            UIColor *textColor = [JYMCDataParser getColorValueWithString:content.color info:info];
            if (textColor) {
                [tempString addAttribute:NSForegroundColorAttributeName value:textColor range:range];
            }
            
            // 字体
            NSString *fontWeight = [JYMCDataParser getStringValueWithString:content.fontWeight info:info];
            CGFloat fontSize = [JYMCDataParser getFloatPixelWithString:content.fontSize info:info];
            if (fontSize > 0) {
                NSString *fontFamily = content.fontFamily.isNotBlank ? content.fontFamily : textElement.fontFamily;
                UIFont *font = [JYMCDataParser getFontValueWithFontName:fontFamily fontSize:fontSize fontWeight:fontWeight];
                if (font) {
                    [tempString addAttribute:NSFontAttributeName value:font range:range];
                    // 添加标签
                    if (tagImg && !CGSizeEqualToSize(CGSizeZero, tagSize)) {// 依赖于后续keyword字体大小，居中
                        NSAttributedString *imgAtt = [NSMutableAttributedString mc_attachWithImg:tagImg lineHeight:font.capHeight size:tagSize];
                        if (imgAtt) [textString appendAttributedString:imgAtt];
                        tagImg = nil;
                        tagSize = CGSizeZero;
                    }
                }
            }
            
            // 线条
            NSString *decorationLine = [JYMCDataParser getStringValueWithString:content.decorationLine info:info];
            if (decorationLine.length > 0 && ![decorationLine isEqualToString:@"none"]) {
                NSUnderlineStyle lineStyle = [JYMCDataParser getUnderlineStyleValueWithString:content.decorationStyle info:info];
                UIColor *decorationColor =  [JYMCDataParser getColorValueWithString:content.decorationColor info:info];
                
                if ([decorationLine isEqualToString:@"underline"]) {
                    [tempString addAttribute:NSUnderlineStyleAttributeName value:@(lineStyle) range:range];
                    if (decorationColor) {
                        [tempString addAttribute:NSUnderlineColorAttributeName value:decorationColor range:range];
                    }
                } else if ([decorationLine isEqualToString:@"line-through"]) {
                    [tempString addAttribute:NSStrikethroughStyleAttributeName value:@(lineStyle) range:range];
                    if (decorationColor) {
                        [tempString addAttribute:NSStrikethroughColorAttributeName value:decorationColor range:range];
                    }
                }
            }
            
            [textString appendAttributedString:tempString];
        }
    }
    
    return textString;
}

#pragma mark - Getter

- (UILabel *)label {
    if (!_label) {
        _label = [[UILabel alloc] init];
    }
    return _label;
}

@end
