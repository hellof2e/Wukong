//
//  JYMCDemoSampleViewController.m
//  JYMagicCube_Example
//
//  Created by 姜灿明 on 2021/5/4.
//

#import "JYDemoPreviewBaseViewController.h"
#import "JYMCWebSocket.h"
#import "UIDevice+JY.h"
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <net/if.h>
#import <YYCategories/YYCategories.h>
#import <JYMagicCube/JYMagicCube.h>
#import <Masonry/Masonry.h>

@interface JYDemoPreviewBaseViewController () <UITextFieldDelegate>

@property (nonatomic, copy) NSString *host;
@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic, assign) BOOL isLoopPull;
@property (nonatomic, strong) JYMCWebSocket *webSocket;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIView *topView;
@property (nonatomic, strong) UILabel *hostLabel;
@property (nonatomic, strong) UIButton *changeHostButton;
@property (nonatomic, strong) UIButton *previewButton;
@property (nonatomic, strong) UIButton *alertButton;

@end

@implementation JYDemoPreviewBaseViewController
@synthesize host = _host;

- (void)dealloc {
    _isLoopPull = NO;
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    [self setupSubviews];
    [self openWebSocket];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (self.isLoopPull) {
        [self performSelector:@selector(repeatLoad) withObject:nil afterDelay:2];
    }
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self closeWebSocket];
}

#pragma mark - Private

- (void)setupSubviews {
    [self.view addSubview:self.contentView];
    [self.contentView addSubview:self.topView];
    [self.contentView addSubview:self.hostLabel];
    [self.contentView addSubview:self.changeHostButton];
    [self.contentView addSubview:self.previewButton];
    [self.contentView addSubview:self.alertButton];
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.equalTo(self.view);
        make.height.mas_equalTo([UIDevice stautsBarAndNaviBarHeight] + 100);
    }];
    [self.topView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.equalTo(self.contentView);
        make.height.mas_equalTo([UIDevice stautsBarAndNaviBarHeight] + 20);
    }];
    [self.hostLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topView.mas_bottom).offset(10);
        make.left.equalTo(self.contentView).offset(20);
        make.height.mas_equalTo(30);
    }];
    [self.changeHostButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.hostLabel.mas_bottom).offset(10);
        make.left.mas_equalTo(20);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(90);
    }];
    [self.previewButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.changeHostButton.mas_right).offset(20);
        make.centerY.equalTo(self.changeHostButton);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(90);
    }];
    [self.alertButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.previewButton.mas_right).offset(20);
        make.centerY.equalTo(self.changeHostButton);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(90);
    }];
}

/// 根据样式 URL 加载
- (void)loadWithURL {
    //style
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@/style.json", self.host]];
    if (!url) {
        [self logError:[NSString stringWithFormat:@"域名错误:%@", self.host]];
        return;
    }
    dispatch_group_t group = dispatch_group_create();

    dispatch_group_enter(group);
    __block NSString* style = nil;
    [[self.session dataTaskWithURL:url completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error || data.length == 0) {
                [self logError:[NSString stringWithFormat:@"样式获取失败:%@", url.absoluteString]];
            } else {
                style = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            }
            dispatch_group_leave(group);
        });
    }] resume];

    
    dispatch_group_enter(group);
    @weakify(self)
    NSURL *dataURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/data.json", self.host]];
    __block NSDictionary *dic = nil;
    [[self.session dataTaskWithURL:dataURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            @strongify(self)
            if (error || data.length == 0) {
                [self logError:[NSString stringWithFormat:@"数据获取失败:%@", dataURL.absoluteString]];
            }
            else {
                dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingFragmentsAllowed error:nil];
                if (!dic) {
                    [self logError:[NSString stringWithFormat:@"数据JSON解析:%@", dataURL.absoluteString]];
                }
            }
            dispatch_group_leave(group);
        });
    }] resume];

    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        @strongify(self)
        if (style.length > 0 && dic) {
            NSString *md5 = [style md5String];
            NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
            NSString *logic = [style rangeOfString:@"logic"].location != NSNotFound ? @"1" : @"";
            NSURL *styleURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/style.json?md5=%@&supportVersion=%@&logic=%@", self.host, md5, appVersion, logic]];

            if (styleURL) {
                [self previewLoadSuccessWithStyleURL:styleURL data:dic];
            } else if (style.length > 0) {
                [self previewLoadSuccessWithStyle:style data:dic];
            }
        }
        
        if (self.isLoopPull) {
            [self performSelector:@selector(repeatLoad) withObject:nil afterDelay:2];
        }
    });
}

- (NSDictionary *)loadFileWithURL:(NSURL *)url {
    NSData *data = [NSData dataWithContentsOfURL:url];
    NSDictionary *dic = nil;
    if (data.length > 0) {
        dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingFragmentsAllowed error:nil];
    }
    return dic;
}

- (void)closeWebSocket {
    [self.webSocket closeWebSocket];
    self.webSocket = nil;
}

- (void)resetWebSocket {
    [self closeWebSocket];
    [self openWebSocket];
}

- (void)openWebSocket {
    NSString *ip = [NSString stringWithFormat:@"%@/socket/logcat", self.host];
    self.webSocket = [[JYMCWebSocket alloc] initWithServerIp:ip];
    [self.webSocket connectWebSocket];
}

#pragma mark - tools

/// 获取所有相关IP信息
- (NSString *)ipAddressWithIfaName:(NSString *)name {
    if (name.length == 0) return nil;
    NSString *address = nil;
    struct ifaddrs *addrs = NULL;
    if (getifaddrs(&addrs) == 0) {
        struct ifaddrs *addr = addrs;
        while (addr) {
            if ([[NSString stringWithUTF8String:addr->ifa_name] isEqualToString:name]) {
                sa_family_t family = addr->ifa_addr->sa_family;
                switch (family) {
                    case AF_INET: { // IPv4
                        char str[INET_ADDRSTRLEN] = {0};
                        inet_ntop(family, &(((struct sockaddr_in *)addr->ifa_addr)->sin_addr), str, sizeof(str));
                        if (strlen(str) > 0) {
                            address = [NSString stringWithUTF8String:str];
                        }
                    } break;
                        
                    default: break;
                }
                if (address) break;
            }
            addr = addr->ifa_next;
        }
    }
    freeifaddrs(addrs);
    return address;
}

#pragma mark - change host

- (void)changeHost {
    UIAlertController* alertController = [UIAlertController alertControllerWithTitle:@"更换域名"
                                                                             message:@"更换样式、数据的域名和端口"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
    
    [alertController addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.text = self.host;
    }];
    
    UIAlertAction* cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:NULL];
    [alertController addAction:cancel];
    
    __weak typeof(self) weak_self = self;
    UIAlertAction* confirm = [UIAlertAction actionWithTitle:@"确认" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
        __strong typeof(weak_self) strong_self = weak_self;
        strong_self.host = alertController.textFields.firstObject.text;
        strong_self->_hostLabel.text = [NSString stringWithFormat:@"当前域名：%@", self.host];
    }];
    [alertController addAction:confirm];
    
    [self presentViewController:alertController animated:YES completion:NULL];
}

#pragma mark - Public_log

- (void)logError:(NSString *)log {
    NSAttributedString *string = [[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"Error: %@\n", log]
                                                                 attributes:@{NSForegroundColorAttributeName:UIColor.redColor}];
    [self showLog:string];
}

- (void)logTips:(NSString *)log {
    NSAttributedString *string = [[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"Tips: %@\n", log]];
    [self showLog:string];
}

- (void)showLog:(NSAttributedString *)log {
    NSLog(@"%@", log.string);
    [self.webSocket sendMsg:log.string];
}

#pragma mark - preview

- (void)startPreview {
    [self repeatLoad];
    self.isLoopPull = true;
    
    [self.previewButton setTitle:@"关闭预览" forState:UIControlStateNormal];
    [self.previewButton removeTarget:self action:@selector(startPreview) forControlEvents:UIControlEventTouchUpInside];
    [self.previewButton addTarget:self action:@selector(stopPreview) forControlEvents:UIControlEventTouchUpInside];
}

- (void)stopPreview {
    self.isLoopPull = false;

    [self.previewButton setTitle:@"开启预览" forState:UIControlStateNormal];
    [self.previewButton removeTarget:self action:@selector(stopPreview) forControlEvents:UIControlEventTouchUpInside];
    [self.previewButton addTarget:self action:@selector(startPreview) forControlEvents:UIControlEventTouchUpInside];
}

#pragma mark - log style

- (void)repeatLoad {
    [self loadWithURL];
}

#pragma mark - url session

- (NSURLSession *)session {
    if (!_session) {
        NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
        config.timeoutIntervalForRequest = 2;
        config.requestCachePolicy = NSURLRequestReloadIgnoringCacheData;
        _session = [NSURLSession sessionWithConfiguration:config];
    }
    return _session;
}

#pragma mark - gettter / setter

- (void)setHost:(NSString *)host {
    _host = host;
    [[NSUserDefaults standardUserDefaults] setObject:host forKey:@"MagicCube_Demo_Host"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    [self openWebSocket];
}

- (NSString *)host {
    if (!_host) {
        do {
            NSString* host = [[NSUserDefaults standardUserDefaults] stringForKey:@"MagicCube_Demo_Host"];
            if (host.length > 0) {
                _host = host;
                break;
            }

#if TARGET_IPHONE_SIMULATOR
            _host = @"http://127.0.0.1:7788";
#else
            _host = [NSString stringWithFormat:@"http://%@:7788", [self ipAddressWithIfaName:@"en0"]];
#endif
            
        } while (0);
    }
    
    return _host;
}

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
    }
    return _contentView;
}

- (UIView *)topView {
    if (!_topView) {
        _topView = [[UIView alloc] init];
        _topView.backgroundColor = [UIColor colorWithHexString:@"#6202EE"];
        
        UILabel *label = [[UILabel alloc] init];
        label.textColor = [UIColor whiteColor];
        label.text = @"悟空Preview";
        label.font = [UIFont systemFontOfSize:30 weight:UIFontWeightMedium];
        [_topView addSubview:label];
        [label mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(20);
            make.bottom.mas_equalTo(-20);
        }];
    }
    return _topView;
}

- (UILabel *)hostLabel {
    if (!_hostLabel) {
        _hostLabel = [[UILabel alloc] init];
        _hostLabel.textColor = [UIColor blackColor];
        _hostLabel.text = [NSString stringWithFormat:@"当前域名：%@", self.host];
    }
    return _hostLabel;
}

- (UIButton *)changeHostButton {
    if (!_changeHostButton) {
        _changeHostButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [_changeHostButton addTarget:self action:@selector(changeHost) forControlEvents:UIControlEventTouchUpInside];
        [_changeHostButton setTitle:@"更换域名" forState:UIControlStateNormal];
        [_changeHostButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        _changeHostButton.backgroundColor = [UIColor colorWithHexString:@"#D6D7D7"];
        _changeHostButton.layer.cornerRadius = 6;
        _changeHostButton.layer.masksToBounds = YES;
    }
    return _changeHostButton;
}

- (UIButton *)previewButton {
    if (!_previewButton) {
        _previewButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [_previewButton setTitle:@"开启预览" forState:UIControlStateNormal];
        [_previewButton addTarget:self action:@selector(startPreview) forControlEvents:UIControlEventTouchUpInside];
        [_previewButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        _previewButton.backgroundColor = [UIColor colorWithHexString:@"#D6D7D7"];
        _previewButton.layer.cornerRadius = 6;
        _previewButton.layer.masksToBounds = YES;
    }
    return _previewButton;
}

- (UIButton *)alertButton {
    if (!_alertButton) {
        _alertButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [_alertButton setTitle:@"弹出弹框" forState:UIControlStateNormal];
        [_alertButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        _alertButton.backgroundColor = [UIColor colorWithHexString:@"#D6D7D7"];
        _alertButton.layer.cornerRadius = 6;
        _alertButton.layer.masksToBounds = YES;
    }
    return _alertButton;
}

@end
