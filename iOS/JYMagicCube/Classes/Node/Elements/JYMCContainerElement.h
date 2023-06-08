//
//  JYMCContainerElement.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCContainerElement : JYMCElement<JYMCElementCoupleProtocol>

/// 背景图片
@property (nonatomic, copy) NSString *backgroundImage;
@property (nonatomic, copy) NSString *activeBackgroundImage;

/// 适配方式
@property (nonatomic, copy) NSString *matchType;
@end

NS_ASSUME_NONNULL_END
