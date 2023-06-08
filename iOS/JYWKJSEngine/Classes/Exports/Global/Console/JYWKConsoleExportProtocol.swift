//
//  JYWKConsoleExportProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/17.
//

import Foundation
import JavaScriptCore

@objc protocol JYWKConsoleExportProtocol: JSExport {
    func log(_ content: String?)
    func info(_ content: String?)
    func warn(_ content: String?)
    func error(_ content: String?)
    func table(_ content: Any?)
}
