//
//  JYMCError.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXTERN NSString* const JYMagicCubeExceptionName;

typedef NS_ENUM(NSInteger, JYMCErrorCode) {
    JYMCErrorInvalidURL        = 1001, // 无效的 url，比如 url 为 nil
    JYMCErrorVersionNotSupport = 1002, // 样式版本不支持
    JYMCErrorDownloadFail      = 1003, // 下载样式失败
    JYMCErrorCancelled         = 1004, // 取消加载
    JYMCErrorInvalidStyle      = 1005, // 无效的样式，比如样式为空，或者样式中的元素无法识别
    JYMCErrorInvalidData       = 1006, // 无效的数据，比如数据为空，或者数据格式不正确
    JYMCErrorCountingOut       = 3001, // 倒计时已过期
    JYMCErrorJSActionException = 4001, // js方法异常
    JYMCErrorJSLoadException   = 4002, // js加载异常
    
    JYMCErrorAlertShowException     = 1000001, // 已有弹框，弹框失败
    JYMCErrorAlertAlreadyShow       = 1000002, // 弹框已经展示
};

@interface JYMCError : NSError

/// 返回 code 对应的描述信息
- (NSString *)errorCodeDesctription;

/// 创建 Error
/// @param code 错误码
+ (instancetype)errorWithCode:(JYMCErrorCode)code;

/// 创建 Error
/// @param code 错误码
/// @param localizedDescription 错误描述
+ (instancetype)errorWithCode:(JYMCErrorCode)code localizedDescription:(NSString * _Nullable)localizedDescription;

@end

NS_ASSUME_NONNULL_END
