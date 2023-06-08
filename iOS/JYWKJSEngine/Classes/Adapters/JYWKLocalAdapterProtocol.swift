//
//  JYWKLocalAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation

@objc public protocol JYWKLocalAdapterProtocol: NSObjectProtocol {
    @objc func userInfo() -> Dictionary<String, Any>?
    @objc func deviceInfo() -> Dictionary<String, Any>?
    @objc func location() -> Dictionary<String, Any>?
}
