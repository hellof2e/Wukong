//
//  JYMCPerformanceRecord.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/19.
//

#import "JYMCPerformanceRecord.h"
#import "JYMCError.h"
#import "JYMCTracker.h"

NSString* const kJYMCPRLoad         = @"load";
NSString* const kJYMCPRLoadNet      = @"load_net";
NSString* const kJYMCPRLoadCache    = @"load_cache";

NSString* const kJYMCPRParse        = @"parse";
NSString* const kJYMCPRParseVM      = @"parse_vm";
NSString* const kJYMCPRParseNode    = @"parse_node";
NSString* const kJYMCPRParseView    = @"parse_view";

NSString* const kJYMCPRJS           = @"load_js";

NSString* const kJYMCPRFirst        = kJYMCPRLoad;
NSString* const kJYMCPRLast         = kJYMCPRParse;

NSString* const kJYMCPRStartStep    = @"start";
NSString* const kJYMCPREndStep      = @"end";

NSString* const kJYMCPRStateKey     = @"state";

NSString* const kJYMCPRStateStart   = @"start";
NSString* const kJYMCPRStateFail    = @"fail";
NSString* const kJYMCPRStateSuccess = @"success";

@interface JYMCPerformanceRecord ()
@property (nonatomic, strong) NSMutableDictionary *records;
@end

@implementation JYMCPerformanceRecord

#pragma mark - Life Cycle
- (id)init {
    self = [super init];
    if (self) {
        _sessionId = [NSUUID UUID].UUIDString ?: @"";
    }
    return self;
}

+ (instancetype)recodeWithStyleUrl:(NSString *)styleUrl {
    return [self recodeWithStyleUrl:styleUrl channel:nil];
}

+ (instancetype)recodeWithStyleUrl:(NSString *)styleUrl channel:(NSString *)channel {
    JYMCPerformanceRecord *instance = [[JYMCPerformanceRecord alloc] init];
    instance.channel = channel ?: @"iOS";
    instance.styleUrl = styleUrl;
    
    NSMutableDictionary *recodes = [instance records];
    recodes[@"channel"]  = instance.channel;
    recodes[@"styleUrl"] = styleUrl ?: @"";
    recodes[@"session"]  = instance.sessionId;
    
    return instance;
}

#pragma mark - public
- (void)recodeStart:(NSString *)name {
    NSMutableDictionary* recode = [self findItemRecode:name];
    recode[kJYMCPRStartStep] = @([self timestamp]);
    recode[@"during"] = @(-1);
    self.records[kJYMCPRStateKey] = [self currentState:name stateName:kJYMCPRStateStart];
}

- (void)recodeEnd:(NSString *)name error:(NSError *)error {
    [self recodeEnd:name error:error isFinished:NO];
}

- (void)recodeEnd:(NSString *)name error:(NSError * _Nullable )error isFinished:(BOOL)isFinished {
    if (!error) {
        [self _recodeSuccess:name];
    } else {
        [self _recodeFail:name error:error];
    }
    
    if (isFinished) {
        [self recodeFinish];
    }
}

#pragma mark - private
- (void)recodeFinish {
    NSNumber* state = [self currentState:kJYMCPRLast stateName:kJYMCPRStateSuccess];
    if ([self.records[kJYMCPRStateKey] intValue] >= [state intValue]) {
        NSNumber* first = [self findItemRecode:kJYMCPRFirst][kJYMCPRStartStep];
        NSNumber* last = [self findItemRecode:kJYMCPRLast][kJYMCPREndStep];
        self.records[@"total_during"] = @(last.longLongValue - first.longLongValue);
    }
    [JYMCTracker trackPerformance:[self toInfoParams]];
}

- (void)_recodeFail:(NSString *)name error:(NSError *)error {
    NSMutableDictionary* recode = [self findItemRecode:name];
    recode[kJYMCPREndStep] = @([self timestamp]);
    self.records[kJYMCPRStateKey] = [self currentState:name stateName:kJYMCPRStateFail];
    if (error) {
        if ([error isKindOfClass:[JYMCError class]]) {
            self.records[@"fail_reason"] = [(JYMCError *)error errorCodeDesctription];
        }
        if (error.localizedDescription.length > 0) {
            self.records[@"fail_desc"] = error.localizedDescription;
        }
    }
    NSNumber* start = recode[kJYMCPRStartStep];
    NSNumber* end   = recode[kJYMCPREndStep];
    recode[@"during"] = @(end.longLongValue - start.longLongValue);
}

- (void)_recodeSuccess:(NSString *)name {
    NSMutableDictionary* recode = [self findItemRecode:name];
    recode[kJYMCPREndStep] = @([self timestamp]);
    self.records[kJYMCPRStateKey] = [self currentState:name stateName:kJYMCPRStateSuccess];
    NSNumber* start = recode[kJYMCPRStartStep];
    NSNumber* end   = recode[kJYMCPREndStep];
    recode[@"during"] = @(end.longLongValue - start.longLongValue);
}

- (NSMutableDictionary *)findItemRecode:(NSString *)name {
    NSMutableDictionary* item = self.records[name];
    if (!item) {
        item = [[NSMutableDictionary alloc] init];
        self.records[name] = item;
    }
    return item;
}

- (long long)timestamp {
    return floor(CACurrentMediaTime() * 1000 + 0.5);
}

- (NSNumber *)currentState:(NSString *)name stateName:(NSString *)stateName {
    static NSDictionary* s_config = nil;
    if (!s_config) {
        s_config = @{
            kJYMCPRLoad     :@{
                kJYMCPRStateStart:@(100),
                kJYMCPRStateFail:@(198),
                kJYMCPRStateSuccess:@(199)},
            kJYMCPRLoadCache:@{
                kJYMCPRStateStart:@(110),
                kJYMCPRStateFail:@(118),
                kJYMCPRStateSuccess:@(119)},
            kJYMCPRLoadNet  :@{
                kJYMCPRStateStart:@(120),
                kJYMCPRStateFail:@(128),
                kJYMCPRStateSuccess:@(129)},
            kJYMCPRJS       :@{
                kJYMCPRStateStart:@(130),
                kJYMCPRStateFail:@(138),
                kJYMCPRStateSuccess:@(139)},
            kJYMCPRParse    :@{
                kJYMCPRStateStart:@(500),
                kJYMCPRStateFail:@(598),
                kJYMCPRStateSuccess:@(599)},
            kJYMCPRParseVM  :@{
                kJYMCPRStateStart:@(510),
                kJYMCPRStateFail:@(518),
                kJYMCPRStateSuccess:@(529)},
            kJYMCPRParseNode:@{
                kJYMCPRStateStart:@(520),
                kJYMCPRStateFail:@(528),
                kJYMCPRStateSuccess:@(529)},
            kJYMCPRParseView:@{
                kJYMCPRStateStart:@(530),
                kJYMCPRStateFail:@(538),
                kJYMCPRStateSuccess:@(539)},
        };
    }
    
    NSNumber* stateCode = @(-1);
    NSDictionary* stateDic = s_config[name];
    if (stateDic) {
        stateCode = stateDic[stateName];
    }
    return stateCode?:@(-1);
}

- (NSDictionary *)toInfoParams {
    NSDictionary *info = [self parseToInfoParams:self.records perKey:nil];
    return info;
}

- (NSDictionary *)parseToInfoParams:(NSDictionary *)recode perKey:(NSString *)perKey {
    NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
    [recode enumerateKeysAndObjectsUsingBlock:^(id _Nonnull subKey, id  _Nonnull obj, BOOL * _Nonnull stop) {
        NSString *realKey = subKey;
        if (perKey.length > 0) {
            realKey = [NSString stringWithFormat:@"%@_%@", perKey, realKey];
        }
        if ([obj isKindOfClass:[NSDictionary class]]) {
            [info addEntriesFromDictionary:[self parseToInfoParams:obj perKey:realKey]];
        } else if ([obj isKindOfClass:[NSArray class]]) {
            
        } else {
            info[realKey] = obj;
        }
    }];
    info[@"isPreloadRecord"] = @(self.isPreloadRecord);
    return info.copy;
}

#pragma mark - getter
- (NSMutableDictionary *)records {
    if (!_records) {
        _records = [[NSMutableDictionary alloc] init];
    }
    return _records;
}

@end
