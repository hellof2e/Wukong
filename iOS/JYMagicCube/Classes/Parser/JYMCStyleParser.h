//
//  JYMCStyleParser.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import <UIKit/UIKit.h>

@class JYMCError;
@class JYMCElement;
@class JYMCStyleMetaData;
@protocol JYMCCustomerFactoryProtocol;

NS_ASSUME_NONNULL_BEGIN

@interface JYMCStyleParser : NSObject

- (nullable JYMCElement *)parseStyle:(JYMCStyleMetaData *)style error:(JYMCError * _Nullable * _Nullable)error;

@end

@interface JYMCStyleParser2 : NSObject
+ (nullable JYMCElement *)parseStyle:(JYMCStyleMetaData *)style
                               error:(JYMCError * _Nullable * _Nullable)error;

+ (Class)MCGetElementViewClass:(NSString *)elementType;
+ (Class)MCGetElementClass:(NSString *)elementType;

@end

NS_ASSUME_NONNULL_END
