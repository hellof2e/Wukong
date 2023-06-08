//
//  JYMCAlertConfig.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2023/2/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, JYMCAlertPostion) {
    JYMCAlertPostionCenter      = 1001, // 居中
    JYMCAlertPostionBottom      = 1002, // 底部
    JYMCAlertPostionTop         = 1003, // 置顶
};

@interface JYMCAlertConfig : NSObject

/// 弹框位置
@property (nonatomic, assign) JYMCAlertPostion alertPostion;

/// 黑色背景透明度（0~1）
@property (nonatomic, assign) CGFloat alertBgAlpha;

/// 禁止黑色背景关闭事件
@property (nonatomic, assign) BOOL disableBgCloseAction;

@end

NS_ASSUME_NONNULL_END
