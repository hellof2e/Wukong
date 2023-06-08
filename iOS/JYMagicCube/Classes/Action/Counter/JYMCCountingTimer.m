//
//  JYMagicCubeView+Action.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/6/27.
//

#import "JYMCCountingTimer.h"
#import "JYMCCountingElement.h"
#import "JYMCDataParser.h"

@interface JYMCCountingTimer ()
@property (nonatomic, copy) dispatch_block_t stepBlock;
@property (nonatomic, copy) dispatch_block_t finishedBlock;
@property (nonatomic, assign) NSTimeInterval currentValue;
@property (nonatomic, assign) NSTimeInterval step;
@property (nonatomic, assign) NSTimeInterval interval;
@property (nonatomic, assign) NSTimeInterval deadline;
@property (nonatomic, assign) NSTimeInterval clockOffset;
@property (nonatomic, assign) BOOL isListItem;
/// 定时器类型，分为时间，数字
/// 如果是时间类型，step单位为秒
/// 如果是数字类型，step单位为个位数
@property (nonatomic, copy) NSString* countingType; // time、number

/// 倒计时停止位置
@property (nonatomic, assign) NSTimeInterval stop;

@property (nonatomic, copy) NSDictionary* infoData;

@end

@implementation JYMCCountingTimer
- (void)dealloc {
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}


- (void)setElement:(JYMCCountingElement *)element info:(JYMCStateInfo *)info {
    if (element.count.length > 0) {
        _currentValue = [JYMCDataParser getDoubleWithString:element.count info:info];
        JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
        [JYMCDataParser getKeyPathFromString:element.count keyPathType:&keyPathType];
        _isListItem = (keyPathType == JYMCKeyPathTypeListItem);
    }
    
    if (element.stop.length > 0) {
        _stop = [JYMCDataParser getDoubleWithString:element.stop info:info] * 0.001;
    }
    
    if (element.step.length > 0) {
        _step = [JYMCDataParser getDoubleWithString:element.step info:info] * 0.001;
    }
    
    if (element.interval.length > 0) {
        _interval = [JYMCDataParser getDoubleWithString:element.interval info:info] * 0.001;
    }
    
    if (element.deadline.length > 0) {
        _deadline = [JYMCDataParser getDoubleWithString:element.deadline info:info] * 0.001;
        JYMCKeyPathType keyPathType = JYMCKeyPathTypeNone;
        [JYMCDataParser getKeyPathFromString:element.deadline keyPathType:&keyPathType];
        _isListItem = (keyPathType == JYMCKeyPathTypeListItem);
    }
    
    if (element.clockOffset.length > 0) {
        _clockOffset = [JYMCDataParser getDoubleWithString:element.clockOffset info:info] * 0.001;
    }
}

- (NSDictionary *)infoData {
    NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
    
    NSInteger count = _currentValue;
    data[@"hour"] = [NSString stringWithFormat:@"%02ld", count / 3600];
    
    count = count % 3600;
    data[@"minute"] = [NSString stringWithFormat:@"%02ld", count / 60];

    count = count % 60;
    data[@"second"] = [NSString stringWithFormat:@"%02ld", count];

    return data.copy;
}

- (void)stopTimer {
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

- (BOOL)startWithFinishBlock:(dispatch_block_t)finishedBlock stepBlock:(dispatch_block_t)stepBlock {
    _stepBlock = stepBlock;
    _finishedBlock = finishedBlock;
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    return YES;
}

- (void)tick:(id)timer {
}

- (void)stepCallBack {
    if (self.stepBlock) {
        self.stepBlock();
    }
}

- (void)didFinishCallBack {
    [self stopTimer];
    if (self.finishedBlock) {
        self.finishedBlock();
    }
}

- (void)continueFireTick {
    [self performSelector:@selector(tick:) withObject:nil afterDelay:_interval];
}
@end

@interface JYMCNumberCountingTimer : JYMCCountingTimer
@end

@implementation JYMCNumberCountingTimer
- (BOOL)startWithFinishBlock:(dispatch_block_t)finishedBlock  stepBlock:(dispatch_block_t)stepBlock {
    [super startWithFinishBlock:finishedBlock stepBlock:stepBlock];
    
    if (self.currentValue > 0 && self.currentValue >= 0 && self.interval > 0.1) {
        [self performSelector:@selector(tick:) withObject:nil afterDelay:self.interval];
        return YES;
    }
    return NO;
}

- (void)tick:(id)timer {
    self.currentValue = self.currentValue - self.step;
    
    [self stepCallBack];

    if (self.currentValue > self.stop) {
        [self continueFireTick];
    } else {
        [self didFinishCallBack];
    }
}

@end


@interface JYMCTimeCountingTimer : JYMCCountingTimer

@end

@implementation JYMCTimeCountingTimer
- (NSTimeInterval)getNowTime {
    return [[NSDate date] timeIntervalSince1970] - self.clockOffset;
}
- (BOOL)startWithFinishBlock:(dispatch_block_t)finishedBlock  stepBlock:(dispatch_block_t)stepBlock {
    [super startWithFinishBlock:finishedBlock stepBlock:stepBlock];

    NSTimeInterval now = [self getNowTime];
    if (self.deadline > now && self.interval > 0.1) {
        [self performSelector:@selector(tick:) withObject:nil afterDelay:self.interval];
        return YES;
    }
    return NO;
}

- (void)tick:(id)timer {
    [self stepCallBack];
    
    NSTimeInterval now = [self getNowTime];
    CGFloat gap = self.deadline - now;
    if (gap > self.stop) {
        [self continueFireTick];
    } else {
        [self didFinishCallBack];
    }
}

- (NSDictionary *)infoData {
    NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
    
    NSTimeInterval now = [self getNowTime];
    NSInteger count = self.deadline - now;;
    if (count < 0) {
        count = 0;
    }
    data[@"hour"] = [NSString stringWithFormat:@"%02ld", count / 3600];
    
    count = count % 3600;
    data[@"minute"] = [NSString stringWithFormat:@"%02ld", count / 60];

    count = count % 60;
    data[@"second"] = [NSString stringWithFormat:@"%02ld", count];

    return data.copy;
}

@end

@interface JYMCDateTimeCountingTimer : JYMCTimeCountingTimer

@end

@implementation JYMCDateTimeCountingTimer
- (NSDictionary *)infoData {
    NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
    
    NSTimeInterval now = [self getNowTime];
    
    NSInteger count = self.deadline - now;
    if (count < 0) {
        count = 0;
    }
    data[@"day"] = [NSString stringWithFormat:@"%ld", count / (3600 * 24)];
    
    count = count % (3600 * 24);
    data[@"hour"] = [NSString stringWithFormat:@"%02ld", count / 3600];
    
    count = count % 3600;
    data[@"minute"] = [NSString stringWithFormat:@"%02ld", count / 60];

    count = count % 60;
    data[@"second"] = [NSString stringWithFormat:@"%02ld", count];

    return data.copy;
}

@end

@implementation JYMCCountingTimerFactory
+ (JYMCCountingTimer *)createTimer:(JYMCCountingElement *)element
                              info:(JYMCStateInfo *)info {
    JYMCCountingTimer* timer = nil;
    if ([element.countingType isEqualToString:@"time"]) {
        timer = [[JYMCTimeCountingTimer alloc] init];
    } else if ([element.countingType isEqualToString:@"datetime"]) {
        timer = [[JYMCDateTimeCountingTimer alloc] init];
    } else if ([element.countingType isEqualToString:@"number"]) {
        timer = [[JYMCNumberCountingTimer alloc] init];
    }
    if (timer) {
        [timer setElement:element info:info];
    }

    return timer;
}
@end

