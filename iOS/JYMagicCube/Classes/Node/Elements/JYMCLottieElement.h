//
//  JYMCLottieElement.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/18.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCLottieElement : JYMCElement

/// lottie动图json地址
@property (nonatomic, copy) NSString *src;
/// 显示模式
@property (nonatomic, copy) NSString *fit;

@end

NS_ASSUME_NONNULL_END

