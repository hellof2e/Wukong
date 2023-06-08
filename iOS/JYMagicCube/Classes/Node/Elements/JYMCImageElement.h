//
//  JYMCImageElement.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCImageElement : JYMCElement

/// 图片地址
@property (nonatomic, copy) NSString *src;
/// 显示模式
@property (nonatomic, copy) NSString *fit;

@end

NS_ASSUME_NONNULL_END
