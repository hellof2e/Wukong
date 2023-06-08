//
//  JYMCCustomerView.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/1/17.
//

#import "JYMCCustomerView.h"
#import "JYMCCustomerElement.h"
#import "JYMCDataParser.h"
#import "JYMCStyleManager.h"
#import "JYMCCustomerFactoryProtocol.h"
#import <YogaKit/UIView+Yoga.h>
#import <YYCategories/YYCategories.h>
#import "JYMCLayoutTrans.h"
#import "JYMCStateInfo.h"
#import "JYMCError.h"

@interface JYMCCustomerView ()
@property (nonatomic, strong) JYMCStateInfo *info;
@property (nonatomic, strong) UIView *customView;
@property (nonatomic, assign) CGRect customViewFrame;
@property (nonatomic, weak) id<JYMCCustomerFactoryProtocol> currentFactory;
@end

@implementation JYMCCustomerView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.clipsToBounds = YES;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self resetBounds];
}

- (void)didMoveToWindow {
    [super didMoveToWindow];
    
    if (self.customView == nil || self.currentFactory == nil) {
        return;
    }
    
    if (self.window) {
        if ([self.currentFactory respondsToSelector:@selector(mc_moveInScreenCustomView:type:data:scopeData:)]) {
            [self.currentFactory mc_moveInScreenCustomView:self.customView
                                                      type:self.element.type
                                                      data:self.info.data
                                                 scopeData:self.info.listItem ?: self.info.data];
        }
    } else {
        if (self.viewController.view.window) {
            if ([self.currentFactory respondsToSelector:@selector(mc_moveOutScreenCustomView:type:data:scopeData:)]) {
                [self.currentFactory mc_moveOutScreenCustomView:self.customView
                                                           type:self.element.type
                                                           data:self.info.data
                                                      scopeData:self.info.listItem ?: self.info.data];
            }
        } else {
            if ([self.currentFactory respondsToSelector:@selector(mc_releaseCustomView:type:)]) {
                [self.currentFactory mc_releaseCustomView:self.customView type:self.element.type];
            }
        }
    }
}

#pragma mark - Public

- (void)applyElement:(JYMCCustomerElement *)element {
    [super applyElement:element];
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    
    if (![self.element isKindOfClass:[JYMCCustomerElement class]]) {
        return;
    }
    
    JYMCCustomerElement *element = (JYMCCustomerElement *)self.element;

    // 解决在悟空生命周期内，所注册自定义工场实例重新注册，自定义工场实例发生了变化，需要重新创建自定义view，例如：
    // registerCustomerFactory:type: 在重新加载（样式相同）前，同一个类型注册了不同的的factory
    if (info.customerFactories.count > 0) {
        id<JYMCCustomerFactoryProtocol> factory = info.customerFactories[element.type];
        if (!factory) {
            NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 的工厂没有被注册", element.type];
            @throw [self exceptionWithReason:reason];
        }
        
        if (![factory conformsToProtocol:@protocol(JYMCCustomerFactoryProtocol)]) {
            NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 的工厂没有服从 JYMCCustomerFactoryProtocol 协议", element.type];
            @throw [self exceptionWithReason:reason];
        }
        
        self.info = info;
        if (self.customView && self.currentFactory == factory) {
            [self updateContentViewWithElementV2:element data:info];
        } else {
            [self.customView removeFromSuperview];
            [self createContentViewWithElementV2:element data:info factory:factory];
        }
    } else {
        @throw [self exceptionWithReason:@"未注册自定义视图工厂"];
    }
}

#pragma mark - private

- (void)resetBounds {
    // 只有customView变化的时候才生效
    if ([self isFixedSize]) {
        return;
    }
    
    if (!CGRectEqualToRect(self.customView.frame, self.customViewFrame)){
        CGRect bounds = CGRectMake(0, 0, CGRectGetWidth(self.customView.bounds), CGRectGetHeight(self.customView.bounds));
        if (self.element.layout.width.length > 0) {
            bounds.size.width = CGRectGetWidth(self.bounds);
        }
        if (self.element.layout.height.length > 0) {
            bounds.size.height= CGRectGetHeight(self.bounds);
        }
        self.bounds = bounds;
        self.customViewFrame = self.customView.frame;
        [self.yoga markDirty];
        if (self.superview.isYogaEnabled) {
            [self.superview.yoga applyLayoutPreservingOrigin:YES];
        }
    }
}

/// 是否固定了尺寸
- (BOOL)isFixedSize {
    BOOL isFixed = NO;
    if (self.element.layout.width.length > 0 && self.element.layout.height.length > 0) {
        isFixed = YES;
    } else if (self.element.layout.aspectRatio.length > 0
               && (self.element.layout.width.length > 0 || self.element.layout.height.length > 0)) {
        isFixed = YES;
    }
    return isFixed;
}

- (void)createContentViewWithElementV2:(JYMCCustomerElement *)element
                                  data:(JYMCStateInfo *)info
                               factory:(id<JYMCCustomerFactoryProtocol>)factory
 
{
    if (![factory respondsToSelector:@selector(mc_createCustomViewWithType:data:scopeData:)]) {
        NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 的工厂未实现 mc_createCustomViewWithType:data:scopeData: 协议方法", element.type];
        @throw [self exceptionWithReason:reason];
    }
    
    UIView *contentView = [factory mc_createCustomViewWithType:element.type data:info.data scopeData:info.listItem ?: info.data];
    if (contentView) {
        self.customView = contentView;
        self.currentFactory = factory;
        [self addSubview:self.customView];
        if ([self isFixedSize]) {
            self.customView.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
        } else {
            if (self.element.layout.width.length > 0 ) { // 如果样式包含 width，customView 优先适配当前宽
                self.customView.autoresizingMask = self.customView.autoresizingMask|UIViewAutoresizingFlexibleWidth;
            }
            if (self.element.layout.height.length > 0) { // 如果样式包含 height，customView 优先适配当前高
                self.customView.autoresizingMask = self.customView.autoresizingMask|UIViewAutoresizingFlexibleHeight;
            }
        }
    } else {
        NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 类型的视图为空", element.type];
        @throw [self exceptionWithReason:reason];
    }
}

- (void)updateContentViewWithElementV2:(JYMCCustomerElement *)element data:(JYMCStateInfo *)info
{
    if (![self.currentFactory respondsToSelector:@selector(mc_updateCustomView:type:data:scopeData:)]) {
        NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 类型的视图未实现 mc_updateCustomView:type:data:scopeData: 方法", self.element.type];
        @throw [self exceptionWithReason:reason];
    }
    [self.currentFactory mc_updateCustomView:self.customView type:element.type data:info.data scopeData:info.listItem ?: info.data];
}

- (NSException *)exceptionWithReason:(NSString *)reason {
    if (reason.length == 0) {
        reason = @"";
    }
    return [NSException exceptionWithName:JYMagicCubeExceptionName reason:reason userInfo:@{}];
}

@end
