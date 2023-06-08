//
//  JYMCTextContent.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCTextContent : NSObject

/// 文本内容
@property (nonatomic, copy) NSString *content;
/// 字体颜色
@property (nonatomic, copy) NSString *color;
/// 字体粗细
@property (nonatomic, copy) NSString *fontWeight;
/// 字体大小，带单位的数值字符串
@property (nonatomic, copy) NSString *fontSize;
/// 线条类型：下划线/删除线/none
@property (nonatomic, copy) NSString *decorationLine;
/// 线条样式：单线条/双线条
@property (nonatomic, copy) NSString *decorationStyle;
/// 线条颜色
@property (nonatomic, copy) NSString *decorationColor;

/// 字体名字
@property (nonatomic, copy) NSString *fontFamily;

#pragma mark -- 标签
/// 标签url
@property (nonatomic, copy) NSString *url;
/// 标签宽度
@property (nonatomic, copy) NSString *width;
/// 标签高度
@property (nonatomic, copy) NSString *height;

#pragma mark -- Local
/// 下载好标签图片
@property (nonatomic, strong, nullable) UIImage *tagImg;
/// 标识下载结束
@property (nonatomic, assign) BOOL downFinished;

- (BOOL)tagContent;

@end

NS_ASSUME_NONNULL_END
