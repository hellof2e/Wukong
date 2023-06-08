//
//  JYMCActionInternalContext.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/7/5.
//

#import <Foundation/Foundation.h>
#import "JYMCLoadingSession.h"
#import "JYMCJSAction.h"

/// 事件类型
typedef NS_ENUM(NSInteger, JYMCActionClickType) {
    JYMCActionClickTypeNone,   //无事件
    JYMCActionClickTypeJump,   //跳转事件
    JYMCActionClickTypeJS,     //js事件
};

NS_ASSUME_NONNULL_BEGIN

@interface JYMCActionInternalContext : NSObject

/// 当前加载会话
@property (nonatomic, strong) JYMCLoadingSession *loadingSession;
/// 事件类型
@property (nonatomic, assign) JYMCActionClickType type;
/// 跳转URL
@property (nonatomic, copy, nullable) NSString *url;
/// js事件
@property (nonatomic, strong, nullable) JYMCJSAction *js;

@end

NS_ASSUME_NONNULL_END
