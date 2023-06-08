//
//  JYMCWebSocket.h
//  JYMagicCubeDebugPlugin
//
//  Created by gaoshuaibin091 on 2023/3/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol JYMCWebSocketDelegate <NSObject>
@optional
- (void)mc_webSocketDidReceiveMessage:(NSString *)message;
@end

@interface JYMCWebSocket : NSObject

@property (nonatomic,weak) id <JYMCWebSocketDelegate> delegate;

- (instancetype)initWithServerIp:(NSString *__nullable)serverIp;
- (void)connectWebSocket;
- (void)closeWebSocket;
- (void)sendMsg:(NSString *)msg;

@end

NS_ASSUME_NONNULL_END

