//
//  JYMCStateInfo.m
//  JYMCStateInfo
//
//  Created by huangshengzhong118 on 2022/9/20.
//

#import "JYMCStateInfo.h"

@implementation JYMCItemScopeStateInfo
- (id)init {
    self = [super init];
    if (self) {
        _listItemIndex = -1;
    }
    return self;
}

@end

@interface JYMCDataStateInfo : NSObject
@property (nonatomic, copy) NSDictionary *data;
@end

@implementation JYMCDataStateInfo
+ (instancetype)stateInfoWithData:(NSDictionary *)data {
    JYMCDataStateInfo* info = [[JYMCDataStateInfo alloc] init];
    info.data = data;
    return info;
}
@end

@interface JYMCStateInfo () {
    NSMutableArray* _exposeInfos;
}
@property (nonatomic, strong) JYMCItemScopeStateInfo* scopeState;
@property (nonatomic, strong) JYMCDataStateInfo* dataState;
@property (nonatomic, strong) NSMutableArray* exposeInfos;
@end

@implementation JYMCStateInfo
+ (instancetype)stateInfoWithData:(NSDictionary *)data {
    JYMCStateInfo *elementInfo = [[JYMCStateInfo alloc] init];
    elementInfo.exposeInfos = [[NSMutableArray alloc] init];
    elementInfo.dataState = [JYMCDataStateInfo stateInfoWithData:data];;
    return elementInfo;
}

- (void)updateScopeState:(NSDictionary *)listItem listItemIndex:(NSInteger)listItemIndex {
    [self updateScopeState:listItem listItemIndex:listItemIndex clearExposes:NO];
}

- (void)updateScopeState:(NSDictionary *)listItem listItemIndex:(NSInteger)listItemIndex clearExposes:(BOOL)isClearExposes {
    JYMCItemScopeStateInfo* info = [[JYMCItemScopeStateInfo alloc] init];
    info.listItem = listItem;
    info.listItemIndex = listItemIndex;
    self.scopeState = info;
    if (isClearExposes) {
        self.exposeInfos = [[NSMutableArray alloc] init];
    }
}

- (void)updateData:(NSDictionary *)data {
    self.dataState.data = data;
}

#pragma mark - getter
- (NSDictionary *)data {
    return self.dataState.data;
}

- (NSDictionary *)listItem {
    return self.scopeState.listItem;
}

- (NSInteger)listItemIndex {
    return self.scopeState != nil ? self.scopeState.listItemIndex : -1;
}


#pragma mark
- (id)copy {
    JYMCStateInfo* info = [[JYMCStateInfo alloc] init];
    info.scopeState = self.scopeState;
    info.dataState = self.dataState;
    info.exposeInfos = self.exposeInfos;
    info.customerFactories = self.customerFactories;
    return info;
}


@end
