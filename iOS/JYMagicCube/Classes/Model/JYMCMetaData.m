//
//  JYMCMetaData.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/10/31.
//

#import "JYMCMetaData.h"

@interface JYMCMetaData ()

@property (nonatomic, copy, readwrite)   NSURL *styleUrl;
@property (nonatomic, copy, readwrite)   NSDictionary *originData;
@property (nonatomic, strong, readwrite) JYMCStyleMetaData *styleMetaData;
@property (nonatomic, copy, readwrite)   NSDictionary *transData;
@property (nonatomic, strong, readwrite) JYWKJSContext *jsContext;

@end

@implementation JYMCMetaData


@end
