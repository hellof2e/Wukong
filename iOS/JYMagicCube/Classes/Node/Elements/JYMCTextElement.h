//
//  JYMCTextElement.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN
@class JYMCTextContent;

@interface JYMCTextElement : JYMCElement

/// 文本内容
@property (nonatomic, copy) NSArray<JYMCTextContent *> *text;

/// 文本最大行数
@property (nonatomic, assign) NSInteger numberOfLines;

/// 字体名字
@property (nonatomic, copy) NSString *fontFamily;

@end

NS_ASSUME_NONNULL_END
