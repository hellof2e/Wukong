//
//  JYMCWebSocket.m
//  JYMagicCubeDebugPlugin
//
//  Created by gaoshuaibin091 on 2023/3/30.
//

#import "JYMCWebSocket.h"
#import <AFNetworking/AFNetworking.h>
#import <SocketRocket/SRWebSocket.h>

static int const kHeartbeatDuration = 3 * 60;
static NSString * const kDefaultWebSocketUrl = @"ws://";

@interface JYMCWebSocket () <SRWebSocketDelegate>
@property (nonatomic, strong) SRWebSocket *socket;
@property (nonatomic, strong) NSTimer *heatBeat;
@property (nonatomic, assign) NSTimeInterval reConnectTime;
@property (nonatomic, strong) NSString *serverIpString;
@property (nonatomic, assign) BOOL autoReconnect;
@end

@implementation JYMCWebSocket

- (instancetype)initWithServerIp:(NSString *__nullable)serverIp {
    if (self = [super init]) {
        if (!serverIp) {
            self.serverIpString = kDefaultWebSocketUrl;
        }else{
            self.serverIpString = serverIp;
        }
        [self addNoti];
    }
    return self;
}

#pragma mark - Public

- (void)connectWebSocket {
    self.autoReconnect = YES;
    [self initWebSocket];
}

- (void)closeWebSocket {
    self.autoReconnect = NO;
    [self close];
}

- (void)sendMsg:(NSString *)msg {
    if (self.socket && self.socket.readyState == SR_OPEN) {
        // 只有在socket状态为SR_OPEN 时，才可以发送消息
        // 在socket状态不为SR_OPEN，可以将消息放进队列里，在websocket连上时，再发送
        [self.socket sendString:msg error:nil];
    }
}

#pragma mark - WebSocket

- (void)initWebSocket {
    if (_socket) {
        return;
    }
    
    NSURL *url = [NSURL URLWithString:self.serverIpString];
    // 请求
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:10];
    // 初始化请求
    _socket = [[SRWebSocket alloc] initWithURLRequest:request];
    // 代理协议
    _socket.delegate = self;
    // 直接连接
    [_socket open];
}

#pragma mark - Notification

- (void)addNoti {
    // 监听网络变化
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleNetWorkStatusChanged) name:@"MC_NOTICE_NETWORK_STATUS_CHANGED" object:nil];
}

// 网络变化
- (void)handleNetWorkStatusChanged {
    // 断网时，关闭websocket
    if(![AFNetworkReachabilityManager sharedManager].reachable){
        [self close];
    }else{
        // 网络连上时，重新连接websocket
        if ((self.socket.readyState == SR_OPEN || self.socket.readyState == SR_CONNECTING) && self.socket) {
            return;
        }
        [self reConnect];
    }
}

#pragma mark - Heart Timer

// 保活机制-探测包
- (void)startHeartbeat {
    self.heatBeat = [NSTimer scheduledTimerWithTimeInterval:kHeartbeatDuration target:self selector:@selector(heartbeatAction) userInfo:nil repeats:YES];
    [self.heatBeat setFireDate:[NSDate distantPast]];
    [[NSRunLoop currentRunLoop] addTimer:_heatBeat forMode:NSRunLoopCommonModes];
}

// 断开连接时销毁心跳
- (void)destoryHeartbeat{
    [self.heatBeat invalidate];
    self.heatBeat = nil;
}

// 发送心跳
- (void)heartbeatAction {
    if (self.socket.readyState == SR_OPEN) {
        [self.socket sendString:@"heart" error:nil];
    }
}

// 重连机制
- (void)reConnect{
    if (!self.autoReconnect) {
        return;
    }
    
    //每隔一段时间重连一次
    // 重连间隔时间 可以根据业务调整
    if (_reConnectTime > 60) {
        _reConnectTime = 60;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(_reConnectTime * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        self.socket = nil;
        [self initWebSocket];
    });
    
    if (_reConnectTime == 0) {
        _reConnectTime = 2;
    }else{
        _reConnectTime *= 2;
    }
}

- (void)resetConnectTime {
    self.reConnectTime = 0;
}

// 关闭Socket
- (void)close {
    [self destoryHeartbeat];
    [self.socket close];
    self.socket = nil;
    [self resetConnectTime];
}

#pragma mark - SRWebSocketDelegate

// 收到服务器消息是回调
- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message{
    NSLog(@"JYMCWebSocket didReceiveMessage：%@", message);
    if ([message isKindOfClass:[NSString class]]) {
        NSString *msg = (NSString *)message;
        if ([self.delegate respondsToSelector:@selector(mc_webSocketDidReceiveMessage:)]) {
            [self.delegate mc_webSocketDidReceiveMessage:msg];
        }
    }
}

// 连接成功
- (void)webSocketDidOpen:(SRWebSocket *)webSocket{
    NSLog(@"JYMCWebSocket DidOpen");
    [self resetConnectTime];
    [self startHeartbeat];
    
    // 下面逻辑，根据业务情况处理
    if (self.socket != nil) {
        // 只有 SR_OPEN 开启状态才能调 send 方法啊，不然要崩
        if (_socket.readyState == SR_OPEN) {
//            NSString *jsonString = @"{\"sid\": \"13b313a3-fea9-4e28-9e56-352458f7007f\"}";
//            [_socket sendString:jsonString error:nil];  //发送数据包
        } else if (_socket.readyState == SR_CONNECTING) {
            NSLog(@"正在连接中，重连后其他方法会去自动同步数据");
            // 每隔2秒检测一次 socket.readyState 状态，检测 10 次左右
            // 只要有一次状态是 SR_OPEN 的就调用 [ws.socket send:data] 发送数据
            // 如果 10 次都还是没连上的，那这个发送请求就丢失了，这种情况是服务器的问题了，小概率的
            // 代码有点长，我就写个逻辑在这里好了
        } else if (_socket.readyState == SR_CLOSING || _socket.readyState == SR_CLOSED) {
            // websocket 断开了，调用 reConnect 方法重连
        }
    }
}


// 连接失败的回调
- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error{
    NSLog(@"JYMCWebSocket didFailWithError %@", error);
    // 1.判断当前网络环境，如果断网了就不要连了，等待网络到来，在发起重连
    // 2.判断调用层是否需要连接，例如用户都没在聊天界面，连接上去浪费流量
    
    if (error.code == 50 || ![AFNetworkReachabilityManager sharedManager].reachable) {
        // 网络异常不重连
        return;
    }
    [self reConnect];
}

// 连接断开的回调
- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean{
    NSLog(@"JYMCWebSocket Close code %ld reason %@", (long)code, reason);
    // 连接断开时，自动重连
    // 是否重连可根据具体业务处理
    if (![AFNetworkReachabilityManager sharedManager].reachable) {
        return;
    }
    [self reConnect];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)pongPayload {
    NSLog(@"JYMCWebSocket Pong");
}

#pragma mark - 其他

- (void)dealloc {
    NSLog(@"LFC: dealloc: %@", self);
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
