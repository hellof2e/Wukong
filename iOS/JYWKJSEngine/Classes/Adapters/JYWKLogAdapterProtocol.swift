//
//  JYWKLogAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2023/5/18.
//

import Foundation

@objc public protocol JYWKLogAdapterProtocol: NSObjectProtocol {
    @objc  func log(_ content: String?)
    @objc  func info(_ content: String?)
    @objc  func warn(_ content: String?)
    @objc  func error(_ content: String?)
    @objc  func table(_ content: Any?)
}
