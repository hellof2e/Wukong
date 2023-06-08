//
//  JYWKAdapterManager.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation
import JavaScriptCore

@objc(JYWKAdapterManager)
public class JYWKAdapterManager : NSObject {
    
    @objc public var logAdapter:   JYWKLogAdapterProtocol?
    @objc public var toastAdapter: JYWKToastAdapterProtocol?
    @objc public var trackAdapter: JYWKTrackAdapterProtocol?
    @objc public var localAdapter: JYWKLocalAdapterProtocol?
    @objc public var cacheAdapter: JYWKCacheAdapterProtocol?
    @objc public var eventAdapter: JYWKEventAdapterProtocol?
    
    @objc public static
    let sharedInstance = JYWKAdapterManager()
    
    /// 需要注册的全局对象集合
    var needRegisteredGlobalObjs: [String:Any] = [:]
    /// 需要注册的内部对象集合
    var needRegisteredInternalObjs: [String:Any] = [:]
    
    /// 注册全局对象，在 js 中使用时需要通过 "key.funcName()" 方式使用
    /// - Parameters:
    ///   - key: 对象名称
    ///   - value: 全局对象（需要遵循自定义扩展的 JSExport 协议）
    @objc public func registerGlobalObj(key: String, value: Any) {
        guard !key.isEmpty else {
            return
        }
        needRegisteredGlobalObjs[key] = value
    }
    
    /// 注册内部对象，在 js 中使用时需要通过 "this.key.funcName()" 方式使用
    /// - Parameters:
    ///   - key: 对象名称
    ///   - value: 内部对象（需要遵循自定义扩展的 JSExport 协议）
    @objc public func registerInternalObj(key: String, value: Any) {
        guard !key.isEmpty else {
            return
        }
        needRegisteredInternalObjs[key] = value
    }
    
}
