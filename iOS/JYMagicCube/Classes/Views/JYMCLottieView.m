//
//  JYMCLottieView.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/1/18.
//

#import "JYMCLottieView.h"
#import "JYMCLottieElement.h"
#import "JYMCDataParser.h"
#import "JYMCStateInfo.h"
#import <YYCategories/YYCategories.h>
#import <YogaKit/UIView+Yoga.h>
#import <Lottie/LOTAnimationView.h>

@interface JYMCLottieView ()

@property (nonatomic, strong) LOTAnimationView *lottieView;

@end

@implementation JYMCLottieView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.lottieView];
    [self.lottieView configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
        layout.width = YGPercentValue(100);
        layout.height = YGPercentValue(100);
    }];
}

#pragma mark - Public

- (void)applyElement:(JYMCLottieElement *)element {
    [super applyElement:element];
    
    self.lottieView.contentMode = [JYMCDataParser getViewContentModeValueWithString:element.fit];
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if ([self.element isKindOfClass:[JYMCLottieElement class]]) {
        JYMCLottieElement *lottieElement = (JYMCLottieElement *)self.element;
        NSString *url = [JYMCDataParser getStringValueWithString:lottieElement.src info:info];
        [self resetAspectRatio:url];
        [self loadLottieWithUrl:[NSURL URLWithString:url]];
    }
}

- (void)loadLottieWithUrl:(NSURL *)url {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(void) {
        NSData *animationData = [NSData dataWithContentsOfURL:url];
        if (!animationData) {
            return;
        }
        
        NSError *error;
        NSDictionary *animationJSON = [NSJSONSerialization JSONObjectWithData:animationData options:0 error:&error];
        if (error || !animationJSON) {
            return;
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.lottieView setAnimationFromJSON:animationJSON];
            [self.lottieView play];
        });
    });
}

- (void)resetAspectRatio:(NSString *)url {
    if (url.length == 0) {
        return;
    }
    if (self.element.layout.aspectRatio.length == 0 && (self.element.layout.width.length == 0 || self.element.layout.height.length == 0)) {
        CGFloat aspectRatio = [JYMCDataParser aspectRatioWithURLString:url];
        if (aspectRatio > 0.0) {
            [self configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
                layout.aspectRatio = aspectRatio;
            }];
            [self.yoga markDirty];
        }
    }
}

#pragma mark - Getter

- (LOTAnimationView *)lottieView {
    if (!_lottieView) {
        _lottieView = [[LOTAnimationView alloc] init];
        _lottieView.backgroundColor = [UIColor clearColor];
        _lottieView.loopAnimation = YES;
    }
    return _lottieView;
}

@end
