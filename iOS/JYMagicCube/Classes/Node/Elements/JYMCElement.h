//
//  JYMCElement.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/9.
//

#import <Foundation/Foundation.h>
#import "JYMCLayout.h"
#import "JYMCAction.h"
#import "JYMCViewStyle.h"

NS_ASSUME_NONNULL_BEGIN


@class JYMCElement;
@protocol JYMCElementCoupleProtocol <NSObject>
/// 子元素集合
@property (nonatomic, copy) NSArray<JYMCElement *> *children;

@end

@interface JYMCElement : NSObject

@property (nonatomic, copy) NSString *type;
@property (nonatomic, strong) JYMCLayout *layout;
@property (nonatomic, strong) JYMCAction *action;
@property (nonatomic, strong) JYMCViewStyle *style;
@property (nonatomic, strong) JYMCViewStyle *activeStyle;
@property (nonatomic, assign) BOOL useActive;

@property (nonatomic, copy) NSString *mIf;
@property (nonatomic, copy) NSString *mFor;

/// 对应的真实的View类型
@property (nonatomic, strong, nullable) Class viewClass;

@property (nonatomic, copy, nullable) NSString *logic;
@property (nonatomic, assign) BOOL jsLifecycle;

@end

NS_ASSUME_NONNULL_END
