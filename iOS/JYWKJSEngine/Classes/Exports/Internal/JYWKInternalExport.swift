//
//  JYWKInternalExport.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/17.
//

import Foundation
import JavaScriptCore

@objc class JYWKInternalExport: NSObject, JYWKInternalExportProtocol {
    
    weak var delegate: JYWKInternalExportDelegate?
    
    
    // MARK: - Life cycle
    
    @available(*, unavailable)
    override init() {
        fatalError("init() isn't designed initializer")
    }
    
    /// 初始化
    /// - Parameter delegate: 代理
    init(_ delegate: JYWKInternalExportDelegate?) {
        super.init()
        self.delegate = delegate
    }
    
    
    // MARK: - JYWKInternalExportProtocol
    
    func ajax(_ value: JSValue?) {
        guard let value = value else {
            return
        }
        
        var type = value.objectForKeyedSubscript("type").toString() ?? "POST"
        if type == "undefined" {
            type = "POST"
        }
        let url = value.objectForKeyedSubscript("url").toString()
        let params = value.objectForKeyedSubscript("data").toDictionary() as? Dictionary<String, Any>
        let dispatchQueue = JYWKJSEngine.dispatchQueue
        if let adapter = JYWKAdapterManager.sharedInstance.eventAdapter {
            adapter.request(type: type, url: url, params: params) { responseData in
                dispatchQueue.async {
                    value.invokeMethod("success", withArguments: [responseData as Any])
                }
            } failure: { error in
                dispatchQueue.async {
                    value.invokeMethod("error", withArguments: nil)
                }
            }
        }
    }
    
    func setState(_ params: Dictionary<String, Any>?) {
        self.delegate?.setState(params)
    }
    
    func callNative(_ params: Dictionary<String, Any>?) {
        guard let params = params, !params.isEmpty else {
            JYWKAdapterManager.sharedInstance.logAdapter?.error("js callNative: 参数不能为空")
            return
        }
        self.delegate?.callNative(params)
    }

    func setTimeout(_ value: JSValue?) {
        guard let value = value else {
            return
        }
        
        let delayMillis = value.objectForKeyedSubscript("delayMillis").toInt32()
        let dispatchQueue = JYWKJSEngine.dispatchQueue
        if let adapter = JYWKAdapterManager.sharedInstance.eventAdapter,
           let delegate = (self.delegate as? JYWKJSContext)?.delegate {
            adapter.timeout(delegate: delegate, interval: delayMillis) {
                dispatchQueue.async {
                    value.invokeMethod("callback", withArguments: [])
                }
            }
        }
    }
    
    func showWKDialog(_ value: JSValue?) {
        guard let value = value,
              let params = value.objectForKeyedSubscript("params").toDictionary() as? Dictionary<String, Any> else {
            return
        }
        
        if let adapter = JYWKAdapterManager.sharedInstance.eventAdapter,
           let delegate = (self.delegate as? JYWKJSContext)?.delegate {
            DispatchQueue.main.async {
                adapter.showDSLAlert(delegate: delegate, params: params, show: {
                    JYWKJSEngine.dispatchQueue.async {
                        value.invokeMethod("onShow", withArguments: [])
                    }
                }, dismiss: {
                    JYWKJSEngine.dispatchQueue.async {
                        value.invokeMethod("onDismiss", withArguments: [])
                    }
                }) { response in
                    JYWKJSEngine.dispatchQueue.async {
                        value.invokeMethod("onEvent", withArguments: [response as Any])
                    }
                }
            }
        }
    }
    
    func dismiss() {
        guard let adapter = JYWKAdapterManager.sharedInstance.eventAdapter else {
            return
        }
        
        DispatchQueue.main.async {
            adapter.dismissDSLAlert()
        }
    }
    
}
