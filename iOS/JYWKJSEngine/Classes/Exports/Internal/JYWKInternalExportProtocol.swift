//
//  JYWKInternalExportProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/17.
//

import Foundation
import JavaScriptCore

@objc protocol JYWKInternalExportProtocol: JSExport {
    /// 网络请求
    func ajax(_ value: JSValue?)
    /// 状态变更
    func setState(_ params: Dictionary<String, Any>?)
    /// callNative
    func callNative(_ params: Dictionary<String, Any>?)
    /// 计时器
    func setTimeout(_ value: JSValue?)
    
    /// dsl弹框
    func showWKDialog(_ value: JSValue?)
    /// dsl关闭弹框
    func dismiss()
}
