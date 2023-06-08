//
//  JYMCStyleMetaData.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/4/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class JYMCElement;

@interface JYMCStyleMetaData : NSObject <NSSecureCoding>

/// 样式唯一标识
@property (nonatomic, copy) NSString *guid;
/// 样式下载链接
@property (nonatomic, copy) NSString *url;
/// 样式内容
@property (nonatomic, copy) NSString *content;
/// 最低支持 app 版本
@property (nonatomic, copy) NSString *supportedVersion;
/// 唯一标识
@property (nonatomic, copy, readonly) NSString* contentMD5String;
/// 节点数
@property (nonatomic, strong) JYMCElement *rootElement;

/// 是否需要执行js
@property (nonatomic, assign, readonly) BOOL needExecuteScript;

@end

NS_ASSUME_NONNULL_END
