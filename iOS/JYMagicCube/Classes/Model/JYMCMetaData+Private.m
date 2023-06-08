//
//  JYMCMetaData+Private.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/11/1.
//

#import "JYMCMetaData+Private.h"

@interface JYMCMetaData ()

@property (nonatomic, copy, readwrite)   NSURL *styleUrl;
@property (nonatomic, copy, readwrite)   NSDictionary *originData;
@property (nonatomic, strong, readwrite) JYMCStyleMetaData *styleMetaData;
@property (nonatomic, copy, readwrite)   NSDictionary *transData;
@property (nonatomic, strong, readwrite) JYWKJSContext *jsContext;

@end

@implementation JYMCMetaData (Private)

+ (instancetype)metaDataWithStyleURL:(NSURL *)styleUrl originData:(NSDictionary *)originData {
    JYMCMetaData* metaData = [[JYMCMetaData alloc] init];
    metaData.styleUrl = styleUrl;
    metaData.originData = originData;
    
    return  metaData;
}

@end
