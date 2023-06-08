//
//  JYMCMetaData.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/10/31.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class JYMCStyleMetaData, JYWKJSContext;

@interface JYMCMetaData : NSObject

#pragma mark -- 源数据
/// 样式链接
@property (nonatomic, copy, readonly) NSURL *styleUrl;
/// 数据
@property (nonatomic, copy, readonly) NSDictionary *originData;
/// 新数据
@property (nonatomic, copy, readonly) NSDictionary *transData;


@end

NS_ASSUME_NONNULL_END
