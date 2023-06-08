//
//  JYMCImageView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/5.
//

#import "JYMCImageView.h"
#import "JYMCDataParser.h"
#import "JYMCImageElement.h"
#import "JYMCStateInfo.h"

#import <YogaKit/UIView+Yoga.h>
#import <SDWebImage/UIImageView+WebCache.h>

@interface JYMCImageView ()

@property (nonatomic, strong) UIImageView *imageView;

@end

@implementation JYMCImageView

#pragma mark - Public

- (void)applyElement:(JYMCImageElement *)element {
    [super applyElement:element];
    
    self.imageView.contentMode = [JYMCDataParser getViewContentModeValueWithString:element.fit];
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if ([self.element isKindOfClass:[JYMCImageElement class]]) {
        JYMCImageElement *imageElement = (JYMCImageElement *)self.element;
        NSString *imageUrl = [JYMCDataParser getStringValueWithString:imageElement.src info:info];
        if (imageUrl.length > 0) {
            self.imageView.hidden = NO;
            [self.imageView sd_setImageWithURL:[NSURL URLWithString:imageUrl]];
            [self resetAspectRatio:imageUrl];
        } else {
            self.imageView.hidden = YES;
        }
    }
}

#pragma mark - Private
- (void)resetAspectRatio:(NSString *)url {
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

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.imageView];
    [self.imageView configureLayoutWithBlock:^(YGLayout * _Nonnull layout) {
        layout.isEnabled = YES;
        layout.width = YGPercentValue(100);
        layout.height = YGPercentValue(100);
    }];
    self.imageView.clipsToBounds = YES;
}

#pragma mark - Getter

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
    }
    return _imageView;
}

@end
