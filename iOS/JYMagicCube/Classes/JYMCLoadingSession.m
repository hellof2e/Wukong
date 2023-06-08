//
//  JYLoadingSession.m
//  JYMagicCube
//
//  Created by zhangxinling10588 on 2022/5/19.
//

#import "JYMCLoadingSession.h"
#import "JYMCDataParser.h"
#import <YYCategories/YYCategories.h>

@implementation JYMCLoadingSession
- (BOOL)isEqual:(JYMCLoadingSession *)object {
    return [self.performanceRecord.sessionId isEqualToString:object.performanceRecord.sessionId];
}

+ (instancetype)sessionWithStyleURL:(NSURL * _Nullable)url {
    JYMCLoadingSession *loadingSession = [[JYMCLoadingSession alloc] init];
    loadingSession.performanceRecord = [JYMCPerformanceRecord recodeWithStyleUrl:url.absoluteString];
    return loadingSession;
}
@end
