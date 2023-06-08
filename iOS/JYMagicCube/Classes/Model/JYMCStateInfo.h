//
//  JYMCElementInfo.h
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/9/20.
//

#import <Foundation/Foundation.h>
#import "JYMCCustomerFactoryProtocol.h"

NS_ASSUME_NONNULL_BEGIN

@class JYMCStateInfo;
@class JYMCTrackActionExposeInfo;

@interface JYMCItemScopeStateInfo : NSObject

/// mFor 对应数据 list 下的 item 数据，如果该值为nil，会取父视图的 listItem
@property (nonatomic, copy, nullable) NSDictionary *listItem;

/// listItem 的下标
@property (nonatomic, assign) NSInteger listItemIndex;

@end

@interface JYMCStateInfo : NSObject
@property (nonatomic, copy, readonly) NSDictionary *data;
@property (nonatomic, strong, readonly) JYMCItemScopeStateInfo* scopeState;
@property (nonatomic, copy) NSDictionary<NSString *,id<JYMCCustomerFactoryProtocol>> *customerFactories;
@property (nonatomic, strong, readonly) NSMutableArray<JYMCTrackActionExposeInfo *>* exposeInfos;

/// mFor 对应数据 list 下的 item 数据，如果该值为nil，会取父视图的 listItem
@property (nonatomic, copy, readonly) NSDictionary *listItem;

/// listItem 的下标
@property (nonatomic, assign, readonly) NSInteger listItemIndex;

+ (instancetype)stateInfoWithData:(NSDictionary *)data;

- (void)updateData:(NSDictionary *)data;
- (void)updateScopeState:(NSDictionary *)listItem listItemIndex:(NSInteger)listItemIndex;
- (void)updateScopeState:(NSDictionary *)listItem listItemIndex:(NSInteger)listItemIndex clearExposes:(BOOL)isClearExposes;
@end

NS_ASSUME_NONNULL_END
