//
//  JYMagicCubeView.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/23.
//

#import <UIKit/UIKit.h>
#import <JYMagicCube/JYMCActionContext.h>
#import <JYMagicCube/JYMCTrackContext.h>

@protocol JYMCCustomerFactoryProtocol;
@class JYMagicCubeView, JYMCMetaData;

NS_ASSUME_NONNULL_BEGIN

typedef void(^JYMagicCubeViewLoadCompletionBlock)(NSURL * _Nullable styleUrl, NSError * _Nullable error);
typedef void(^JYMagicCubeViewPreoadCompletionBlock)(CGFloat height);

@protocol JYMagicViewDelegate <NSObject>

@optional

/// 加载并渲染样式数据成功
/// @param mcView 动态化视图
- (void)magicCubeViewDidLoadSuccess:(JYMagicCubeView *)mcView;

/// 加载并渲染样式数据失败
/// @param mcView 动态化视图
/// @param error 错误信息
- (void)magicCubeView:(JYMagicCubeView *)mcView didLoadFailWithError:(NSError *)error;

/// 开始拦截交互事件
/// @param mcView 动态化视图
/// @param actionContext 交互事件上下文
/// @return 接入方是否拦截交互事件. YES：接入方处理，NO: 内部统一处理
- (BOOL)magicCubeView:(JYMagicCubeView *)mcView didStartInterceptHandleAction:(JYMCActionContext *)actionContext;

/// 同步数据回调
/// 一些场景需要通知接入方更新数据，如简单数据请求
/// @param mcView 动态化视图
/// @param data 需要同步的数据
/// @param needRefresh YES：需要外部更新UI，NO：不需要外部更新UI(内部会自动刷新)
- (void)magicCubeView:(JYMagicCubeView *)mcView syncData:(NSDictionary *)data needRefresh:(BOOL)needRefresh;

/// 开始处理埋点
/// @param mcView 动态化视图
/// @param context 埋点上下文
/// @return：是否由接入方处理该埋点. YES：接入方处理，NO: 内部统一处理
- (BOOL)magicCubeView:(JYMagicCubeView *)mcView didStartHandleTrack:(JYMCTrackContext *)context;

/// 事件结束
- (void)magicCubeView:(JYMagicCubeView *)mcView didActionFinished:(JYMCActionContext *)context;

/// js call native
- (void)magicCubeView:(JYMagicCubeView *)mcView jsCallNative:(NSDictionary<NSString *,id> *)params;

@end

@interface JYMagicCubeView : UIView

/// 当前样式的 url
@property (nonatomic, strong, readonly) NSURL *url;

/// 动态化视图代理
@property (nonatomic, weak) id<JYMagicViewDelegate> delegate;

/// 加载并渲染样式数据
/// @param url 样式链接
/// @param data 样式对应的数据
- (void)loadWithStyleURL:(NSURL *)url
                    data:(nullable NSDictionary *)data;

/// 加载并渲染样式数据
/// @param url 样式链接
/// @param data 样式对应的数据
/// @param placeholder 占位视图（自动铺满整个 mcView）
- (void)loadWithStyleURL:(NSURL *)url
                    data:(nullable NSDictionary *)data
             placeholder:(nullable UIView *)placeholder;

/// 加载并渲染样式数据
/// @param url 样式链接
/// @param data 样式对应的数据
/// @param placeholder 占位视图（自动铺满整个 mcView）
/// @param completionBlock 结束回调
- (void)loadWithStyleURL:(NSURL *)url
                    data:(nullable NSDictionary *)data
             placeholder:(nullable UIView *)placeholder
              completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;

/// 加载并渲染样式数据
/// @param style 样式（json 格式）
/// @param data 样式对应的数据
/// @param completionBlock 结束回调
- (void)loadWithStyle:(NSString *)style
                 data:(NSDictionary *)data
           completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;

/// 加载并渲染样式数据
/// @param metaData 预加载后的样式
/// @param completionBlock 结束回调
- (void)loadWithMetaData:(JYMCMetaData *)metaData
              completion:(nullable JYMagicCubeViewLoadCompletionBlock)completionBlock;

/// 根据宽度估算高度（需要在 loadWithStyleURL 成功后使用，否则返回 0）
/// @param width 预期宽度
- (CGFloat)heightForWidth:(CGFloat)width;

/// 计算视图高度（样式必须已在本地缓存，否则返回 0）
/// ⚠️ 当 style 中包含自定义视图时，使用该方法计算高度（在注册自定义视图后再调用）
/// @param url 已缓存样式的链接，否则取到的高度为 0
/// @param data 数据
/// @param width 视图宽度
- (CGFloat)calculateHeightWithURL:(NSURL *)url
                             data:(NSDictionary *)data
                            width:(CGFloat)width;

/// 根据宽度估算高度（预加载成功后调用有效）
/// @param width 预期宽度
/// @param metaData 预加载后的样式数据
+ (CGFloat)heightForWidth:(CGFloat)width metaData:(JYMCMetaData *)metaData;

/// 注册一个自定义视图工厂
/// @param factory 自定义视图工厂
/// @param type 自定义元素名称
- (void)registerCustomerFactory:(id<JYMCCustomerFactoryProtocol>)factory type:(NSString *)type;

@end

NS_ASSUME_NONNULL_END
