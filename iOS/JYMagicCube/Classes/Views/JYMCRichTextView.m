//
//  JYMCRichTextView.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/2/9.
//

#import "JYMCRichTextView.h"

#import "JYMCRichTextElement.h"
#import "JYMCTextElement.h"
#import "JYMCDataParser.h"
#import <YogaKit/UIView+Yoga.h>
#import <YYCategories/YYCategories.h>
#import "JYMCLayoutTrans.h"
#import "JYMCTextContent.h"
#import "JYMCStateInfo.h"

@interface JYMCRichTextView ()

@property (nonatomic, strong) UILabel *label;

@end

@implementation JYMCRichTextView

#pragma mark - Public

- (void)applyElement:(JYMCRichTextElement *)element {
    [super applyElement:element];
    
    self.label.numberOfLines = element.numberOfLines;
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if (self.element.layout.textAlign.length > 0 && self.element.layout.width.length > 0) {
        self.label.textAlignment = NSTextAlignmentFromString(self.element.layout.textAlign);
        self.label.yoga.width = YGPercentValue(100);
    }

    if ([self.element isKindOfClass:[JYMCRichTextElement class]]) {
        JYMCRichTextElement *textElement = (JYMCRichTextElement *)self.element;
        
        NSString *allText = [JYMCDataParser getStringValueWithString:textElement.text.content info:info];
        NSMutableAttributedString *allAttributeString = [self updateGlobalTextWithText:allText element:textElement info:info];
        
        if (allAttributeString) {
            for (JYMCTextContent *content in textElement.keywords) {
                NSString *text = [JYMCDataParser getStringValueWithString:content.content info:info];
                if (text.length == 0) continue;
                
                NSRange range = [allText rangeOfString:text];
                [self updateAttributeString:allAttributeString content:content info:info range:range];
            }
        }
        
        self.label.attributedText = allAttributeString;
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

- (NSMutableAttributedString *)updateGlobalTextWithText:(NSString *)text element:(JYMCRichTextElement *)textElement info:(JYMCStateInfo *)info {
    if (text.length == 0) return nil;
    NSMutableAttributedString *allAttributeString = [[NSMutableAttributedString alloc] initWithString:text];
    NSRange range = NSMakeRange(0, text.length);
    
    [self updateAttributeString:allAttributeString content:textElement.text info:info range:range];
    return allAttributeString;
}

- (void)updateAttributeString:(NSMutableAttributedString *)allAttributeString
                      content:(JYMCTextContent *)content
                         info:(JYMCStateInfo *)info
                        range:(NSRange)range {
    if (range.location == NSNotFound ||
        range.length == 0 ||
        (range.location + range.length > allAttributeString.string.length)) {
        return;
    }
    // 字体颜色
    UIColor *textColor = [JYMCDataParser getColorValueWithString:content.color info:info];
    // 字体
    NSString *fontWeight = [JYMCDataParser getStringValueWithString:content.fontWeight info:info];
    CGFloat fontSize = [JYMCDataParser getFloatPixelWithString:content.fontSize info:info];
    
    if (textColor) [allAttributeString addAttribute:NSForegroundColorAttributeName value:textColor range:range];
    if (fontSize > 0) {
        JYMCRichTextElement *textElement = (JYMCRichTextElement *)self.element;
        NSString *fontFamily = content.fontFamily.isNotBlank ? content.fontFamily : textElement.fontFamily;
        UIFont *font = [JYMCDataParser getFontValueWithFontName:fontFamily fontSize:fontSize fontWeight:fontWeight];
        if (font) [allAttributeString addAttribute:NSFontAttributeName value:font range:range];
    }
    
    // 线条
    NSString *decorationLine = [JYMCDataParser getStringValueWithString:content.decorationLine info:info];
    if (decorationLine.length > 0 && ![decorationLine isEqualToString:@"none"]) {
        NSUnderlineStyle lineStyle = [JYMCDataParser getUnderlineStyleValueWithString:content.decorationStyle
                                                                                 info:info];
        UIColor *decorationColor =  [JYMCDataParser getColorValueWithString:content.decorationColor
                                                                       info:info];
        
        if ([decorationLine isEqualToString:@"underline"]) {
            [allAttributeString addAttribute:NSUnderlineStyleAttributeName value:@(lineStyle) range:range];
            if (decorationColor) {
                [allAttributeString addAttribute:NSUnderlineColorAttributeName value:decorationColor range:range];
            }
        } else if ([decorationLine isEqualToString:@"line-through"]) {
            [allAttributeString addAttribute:NSStrikethroughStyleAttributeName value:@(lineStyle) range:range];
            if (decorationColor) {
                [allAttributeString addAttribute:NSStrikethroughColorAttributeName value:decorationColor range:range];
            }
        }
    }
}

#pragma mark - Getter

- (UILabel *)label {
    if (!_label) {
        _label = [[UILabel alloc] init];
    }
    return _label;
}

@end
