//
//  JYWKJSEngine.swift
//  Pods
//
//  Created by gaoshuaibin091 on 2022/8/10.
//

import Foundation
import UIKit
import JavaScriptCore

@objc(JYWKJSEngine)
public class JYWKJSEngine : NSObject {
    
    @objc public static let sharedInstance = JYWKJSEngine()
    @objc public static let dispatchQueue = DispatchQueue(label: "wukong_js_runtime_queue")
    
    // 目前为单context模式
    private let context = JSContext(virtualMachine:JSVirtualMachine())!
    
    // MARK: - life cycle
    
    private override init() {
        super.init()
        registerGlobalExports()
    }
    
    // MARK: - Public

    /// 运行js
    /// - Parameters:
    ///   - content: js内容
    ///   - delegate: 代理
    /// - Returns: js上下文（虚拟上下文）
    @objc public func runJS(_ content: String, delegate: JYWKJSContextDelegate?, error: UnsafeMutablePointer<NSError?>?) -> JYWKJSContext? {
        guard content.count > 0 else {
            error?.pointee = NSError(domain: "JYWKJSContext exception",
                                     code: -1,
                                     userInfo: [NSLocalizedDescriptionKey:"js script is empty"])
            return nil
        }
        
        let script = JSStringCreateWithCFString(content as CFString)
        let contextRef = context.jsGlobalContextRef
        var exceptionRef: JSValueRef?
        guard let result = JSEvaluateScript(contextRef, script, nil, nil, 0, &exceptionRef),
              JSValueIsObject(context.jsGlobalContextRef, result),
              let mainObject = JSValue(jsValueRef: result, in: context)  else {
            
            var errorDesc : String
            if exceptionRef != nil {
                errorDesc = JSValue(jsValueRef: exceptionRef, in: context).toString()
            } else {
                errorDesc = "main object is empty"
            }
            error?.pointee = NSError(domain: "JYWKJSContext exception", code: -1, userInfo: [NSLocalizedDescriptionKey:errorDesc])
            return nil
        }
    
        let virtualContext = JYWKJSContext(mainObject, delegate: delegate)
        return virtualContext
    }
    
    // MARK: - Private
    
    /// 注册全局对象
    func registerGlobalExports() {
        /// 注册官方的全局对象
        context.setObject(JYWKCommonExport(), forKeyedSubscript: "Wukong" as NSCopying & NSObjectProtocol)
        context.setObject(JYWKConsoleExport(), forKeyedSubscript: "console" as NSCopying & NSObjectProtocol)
        /// 注册自定义的全局对象
        JYWKAdapterManager.sharedInstance.needRegisteredGlobalObjs.forEach { (key, obj) in
            context.setObject(obj, forKeyedSubscript: key as NSCopying & NSObjectProtocol)
        }
    }
}
