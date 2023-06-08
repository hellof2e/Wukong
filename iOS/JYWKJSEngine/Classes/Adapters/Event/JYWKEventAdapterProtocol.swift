//
//  JYWKEventAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation
import Accessibility

/// 一些不经常扩展的事件协议
@objc public protocol JYWKEventAdapterProtocol: NSObjectProtocol {
    /// 通过URL打开一个页面
    @objc func navigator(_ url: String?)
    /// 网络请求
    @objc func request(type: String, url: String?, params: Dictionary<String, Any>?, success: ((Dictionary<String, Any>?) -> Void)?, failure: ((Error) -> Void)?)
    /// 计时器 interval 毫秒
    @objc func timeout(delegate: Any, interval: Int32, callback:(() -> Void)?)
    /// 预加载dsl
    @objc func preloadDSLStyle(_ url: String?)
    
    /// 弹出dsl弹框
    @objc func showDSLAlert(delegate: Any, params: Dictionary<String, Any>, show: (() -> Void)?, dismiss: (() -> Void)?, callback: ((Dictionary<String, Any>?) -> Void)?)
    /// 关闭dsl弹框
    @objc func dismissDSLAlert()
}
