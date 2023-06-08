//
//  JYWKInternalExportDelegate.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/18.
//

import Foundation

protocol JYWKInternalExportDelegate: NSObjectProtocol {
    func setState(_ params: Dictionary<String, Any>?)
    func callNative(_ params: Dictionary<String, Any>)
}
