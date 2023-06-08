//
//  JYWKConsoleExport.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/17.
//

import Foundation

@objc class JYWKConsoleExport: NSObject, JYWKConsoleExportProtocol {
    
    func log(_ content: String?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.logAdapter else {
            return
        }
        adapter.log(content);
    }
    
    func info(_ content: String?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.logAdapter else {
            return
        }
        adapter.info(content)
    }
    
    func warn(_ content: String?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.logAdapter else {
            return
        }
        adapter.warn(content)
    }
    
    func error(_ content: String?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.logAdapter else {
            return
        }
        adapter.log(content)
    }
    
    func table(_ content: Any?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.logAdapter else {
            return
        }
        adapter.table(content)
    }
    
}
