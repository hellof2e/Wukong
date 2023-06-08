//
//  JYWKJSContext.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/10.
//

import Foundation
import JavaScriptCore
import UIKit

@objc(JYWKJSContext)
public class JYWKJSContext: NSObject {
    
    var mainObject: JSValue
    @objc public weak var delegate: JYWKJSContextDelegate?
    @objc public weak var customData: AnyObject?
    
    @available(*, unavailable)
    override init() {
        fatalError("init() isn't designed initializer")
    }
    
    /// 初始化
    /// - Parameters:
    ///   - mainObject: 全局对象
    ///   - delegate: 代理
    init(_ mainObject: JSValue, delegate: JYWKJSContextDelegate?) {
        self.delegate = delegate
        self.mainObject = mainObject
        super.init()
        registerInternelObjects()
    }
    
    // MARK: - Public
    
    /// 同步绑定数据
    /// - Parameters:
    ///   - properties: 熟悉清单
    @objc public func bindingProperties(_ properties: Dictionary<String, Any>?) {
        properties?.forEach({ (key: String, value: Any) in
            mainObject.setValue(value, forProperty: key)
        })
    }
    
    @objc public func readProperty(_ property: String!) -> JSValue? {
        return mainObject.forProperty(property)
    }
    
    /// 同步执行JS方法，保持与调用方同队列
    /// @param method The name of the method to be invoked.
    /// @param arguments The arguments to pass to the method.
    /// @param error The error to pass to the method.
    /// @result The return value of the method call.
    @objc public func nativeCallJSFunc(_ name: String, arguments: [Any], error: UnsafeMutablePointer<NSError?>?) -> JSValue? {
        guard mainObject.hasProperty(name),
              let method = mainObject.forProperty(name),
              JSObjectIsFunction(mainObject.context.jsGlobalContextRef, method.jsValueRef) else {
            JYWKAdapterManager.sharedInstance.logAdapter?.error("JYWKJSContext exception: can't find the \(name)")
            return nil
        }
        
        var jsArgs = getJsArgs(arguments)
        var exceptionRef: JSValueRef?
        let value = JSObjectCallAsFunction(mainObject.context.jsGlobalContextRef,
                                           method.jsValueRef,
                                           mainObject.jsValueRef,
                                           jsArgs.count,
                                           &jsArgs,
                                           &exceptionRef)
        if exceptionRef != nil {
            guard let exceptionValue = JSValue(jsValueRef: exceptionRef, in: mainObject.context).toString() else {
                return nil
            }
            error?.pointee = NSError(domain: "JYWKJSContext exception", code: -1, userInfo: [NSLocalizedDescriptionKey:exceptionValue])
        }
        return JSValue(jsValueRef: value, in: mainObject.context)
    }
    
    // MARK: - Private
    
    func registerInternelObjects() {
        /// 注册 wk 对象
        let wk = JYWKInternalExport(self)
        mainObject.setObject(wk, forKeyedSubscript: "wk" as NSCopying & NSObjectProtocol)
        /// 注册自定义内部对象
        JYWKAdapterManager.sharedInstance.needRegisteredInternalObjs.forEach { (key, obj) in
            mainObject.setObject(obj, forKeyedSubscript: key as NSCopying & NSObjectProtocol)
        }
    }
    
    /// 获取c的API需要的jsValue数组
    func getJsArgs(_ args: [Any]) -> [JSValueRef?] {
        var jsArgs = [JSValueRef?]()
        args.forEach { item in
            if let jsValueRef = getJSArg(item) {
                jsArgs.append(jsValueRef)
            }
        }
        return jsArgs
    }
    
    func getJSArg(_ arg: Any) -> JSValueRef? {
        let contextRef = mainObject.context.jsGlobalContextRef
        var jsValueRef: JSValueRef?
        switch arg.self {
        case is String:        jsValueRef = JSValueMakeString(contextRef, JSStringCreateWithCFString((arg as! String) as CFString))
        case is [Any]:         jsValueRef = JSValue(object: arg, in: mainObject.context).jsValueRef
        case is [String: Any]: jsValueRef = JSValue(object: arg, in: mainObject.context).jsValueRef
        case is Int:           jsValueRef = JSValueMakeNumber(contextRef, Double(arg as! Int))
        case is Double:        jsValueRef = JSValueMakeNumber(contextRef, arg as! Double)
        case is Bool:          jsValueRef = JSValueMakeBoolean(contextRef, arg as! Bool)
        case is NSNull:        jsValueRef = JSValueMakeNull(contextRef)
        default:               jsValueRef = JSValueMakeUndefined(contextRef)
        }
        return jsValueRef
    }
}

extension JYWKJSContext: JYWKInternalExportDelegate {
    
    func setState(_ params: Dictionary<String, Any>?) {
        guard let delegate = delegate,
              delegate.responds(to: #selector(delegate.wkjs_setState(_:jsContext:))) else {
            return
        }
        return delegate.wkjs_setState(params, jsContext: self)
    }
    
    func callNative(_ params: Dictionary<String, Any>) {
        guard let delegate = delegate,
              delegate.responds(to: #selector(delegate.wkjs_callNative(_:jsContext:))) else {
            return
        }
        return delegate.wkjs_callNative(params, jsContext: self)
    }
    
}
