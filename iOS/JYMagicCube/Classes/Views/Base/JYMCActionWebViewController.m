//
//  JYMCActionWebViewController.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/8.
//

#import "JYMCActionWebViewController.h"
#import <WebKit/WebKit.h>
#import <YYCategories/YYCategories.h>

@interface JYMCActionWebViewController ()

@property (nonatomic, strong) WKWebView *webview;

@end

@implementation JYMCActionWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"返回" style:UIBarButtonItemStyleDone target:self action:@selector(close)];
    self.webview = [[WKWebView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:self.webview];
    
    if (self.url.length > 0) {
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:self.url]];
        [self.webview loadRequest:request];
    }
}

- (void)close {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)setUrl:(NSString *)url {
    _url = [url copy];
    
    if (self.webview) {
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
        [self.webview loadRequest:request];
    }
}

@end
