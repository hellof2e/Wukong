//
//  JYMCMetaData+Private.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/11/1.
//

#import "JYMCMetaData.h"

NS_ASSUME_NONNULL_BEGIN
@class JYMCStyleMetaData, JYWKJSContext;

@interface JYMCMetaData (Private)

@property (nonatomic, strong) JYMCStyleMetaData *styleMetaData;
@property (nonatomic, copy)   NSDictionary *transData;
@property (nonatomic, strong) JYWKJSContext *jsContext;

+ (instancetype)metaDataWithStyleURL:(NSURL *)styleUrl originData:(NSDictionary *)originData;

@end

NS_ASSUME_NONNULL_END
