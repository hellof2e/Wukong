//
//  JYWKJSContextDelegate.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/24.
//

import Foundation

@objc public protocol JYWKJSContextDelegate: NSObjectProtocol {
    @objc func wkjs_setState(_ params: Dictionary<String, Any>?, jsContext:JYWKJSContext)
    @objc func wkjs_callNative(_ params: Dictionary<String, Any>, jsContext:JYWKJSContext)
}
