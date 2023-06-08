//
//  JYDemoSampleViewController.h
//  JYMagicCube_Example
//
//  Created by 姜灿明 on 2021/5/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol JYDemoPreviewBaseViewControllerProtocol <NSObject>
@optional
- (void)previewLoadSuccessWithStyle:(NSString *)style data:(NSDictionary *)data;
- (void)previewLoadSuccessWithStyleURL:(NSURL *)styleURL data:(NSDictionary *)data;
@end

@interface JYDemoPreviewBaseViewController : UIViewController<JYDemoPreviewBaseViewControllerProtocol>
@property (nonatomic, strong, readonly) UIView *contentView;
@property (nonatomic, strong, readonly) UIButton *alertButton;
- (void)logError:(NSString *)log;
- (void)logTips:(NSString *)log;
- (void)repeatLoad;
@end

NS_ASSUME_NONNULL_END
