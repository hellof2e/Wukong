//
//  JYMCPerformanceRecord.h
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/19.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 加载样式
FOUNDATION_EXTERN NSString* const kJYMCPRLoad;

/// 从网络加载样式
FOUNDATION_EXTERN NSString* const kJYMCPRLoadNet;

/// 解析渲染
FOUNDATION_EXTERN NSString* const kJYMCPRParse;

/// 前置JS运行
FOUNDATION_EXTERN NSString* const kJYMCPRJS;

/// 解析渲染-ViewModel
FOUNDATION_EXTERN NSString* const kJYMCPRParseVM;

/// 解析渲染-虚拟节点
FOUNDATION_EXTERN NSString* const kJYMCPRParseNode;

/// 解析渲染-view渲染
FOUNDATION_EXTERN NSString* const kJYMCPRParseView;


@interface JYMCPerformanceRecord : NSObject

@property (nonatomic, copy) NSString *sessionId;
@property (nonatomic, copy) NSString *channel;
@property (nonatomic, copy, nullable) NSString *styleUrl;
@property (nonatomic, assign) BOOL isPreloadRecord;

+ (instancetype)recodeWithStyleUrl:(NSString * _Nullable)styleUrl;
+ (instancetype)recodeWithStyleUrl:(NSString * _Nullable)styleUrl channel:(NSString * _Nullable)channel;
- (void)recodeStart:(NSString *)name;
- (void)recodeEnd:(NSString *)name error:(NSError * _Nullable)error;
- (void)recodeEnd:(NSString *)name error:(NSError * _Nullable)error isFinished:(BOOL)isFinished;

@end

NS_ASSUME_NONNULL_END
