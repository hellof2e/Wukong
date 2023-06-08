//
//  JYMCJSAction.h
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/8/26.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JYMCJSAction : NSObject

/// 方法名
@property (nonatomic, copy) NSString *methodName;
/// 参数
@property (nonatomic, strong, nullable) NSArray<id> *args;

@end

NS_ASSUME_NONNULL_END
