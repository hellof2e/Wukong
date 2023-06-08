//
//  JYMCRichTextElement.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/2/9.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN
@class JYMCTextContent;

@interface JYMCRichTextElement : JYMCElement

/// 文本内容
@property (nonatomic, strong) JYMCTextContent *text;
/// 替换富文本内容
@property (nonatomic, copy) NSArray<JYMCTextContent *> *keywords;
/// 文本最大行数
@property (nonatomic, assign) NSInteger numberOfLines;

/// 字体名字
@property (nonatomic, copy) NSString *fontFamily;

@end

NS_ASSUME_NONNULL_END
