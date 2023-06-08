//
//  JYMCProgressElement.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/18.
//

#import "JYMCElement.h"

NS_ASSUME_NONNULL_BEGIN

@interface JYMCProgressElement : JYMCElement

/// 当前进度。范围 0～100。默认0，不大于maxProgress
@property (nonatomic, copy) NSString* progress;

/// 最大进度。默认值为100
@property (nonatomic, copy) NSString* maxProgress;

/// 进度条颜色，默认值: "#ffff00"
@property (nonatomic, copy) NSString* progressColor;


@end

NS_ASSUME_NONNULL_END
