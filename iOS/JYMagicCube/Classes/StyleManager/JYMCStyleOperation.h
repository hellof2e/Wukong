//
//  JYMCStyleOperation.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/4/28.
//

#import <Foundation/Foundation.h>

/// 抽象 style operation
@protocol JYMCStyleOperation <NSObject>

- (void)cancel;

@end

/// 让 NSOperation 实现 JYMCStyleOperation 协议
@interface NSOperation (JYMCStyleOperation) <JYMCStyleOperation>

@end
