//
//  JYWKBaseExport.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/11.
//

import Foundation
import JavaScriptCore

@objc class JYWKCommonExport: NSObject, JYWKCommonExportProtocol {
    
    // MARK: - cache
    
    func cacheItem(_ fileName: String?, _ key: String?, _ value: String?) {
        guard let fileName = fileName,
              let key = key,
              let value = value,
              let adapter = JYWKAdapterManager.sharedInstance.cacheAdapter else {
            return
        }
        adapter.cacheItem(fileName: fileName, key: key, value: value)
    }
    
    func readItem(_ fileName: String?, _ key: String?) -> String? {
        guard let fileName = fileName,
              let key = key,
              let adapter = JYWKAdapterManager.sharedInstance.cacheAdapter else {
            return nil
        }
        return adapter.readItem(fileName: fileName, key: key) as? String
    }
    
    
    // MARK: - Toast
    
    func toast(_ content: String?) {
        guard let content = content,
              let adapter = JYWKAdapterManager.sharedInstance.toastAdapter else {
            return
        }
        DispatchQueue.main.async {
            adapter.showToast(content)
        }
    }
    
    
    // MARK: - UserInfo
    
    func getUserInfo() -> Dictionary<String, Any>? {
        guard let adapter = JYWKAdapterManager.sharedInstance.localAdapter else {
            return nil
        }
        return adapter.userInfo()
    }
    
    
    // MARK: - DeviceInfo
    
    func getDeviceInfo() -> Dictionary<String, Any> {
        // platform 固定为 iOS
        let platform = "iOS"
        let defaultInfo = ["platform":platform]
        guard let adapter = JYWKAdapterManager.sharedInstance.localAdapter else {
            return defaultInfo
        }
        
        if var deviceInfo = adapter.deviceInfo() {
            deviceInfo["platform"] = platform
            return deviceInfo
        }
        return defaultInfo
    }
    
    
    // MARK: - Location
    
    func getLocation() -> Dictionary<String, Any>? {
        guard let adapter = JYWKAdapterManager.sharedInstance.localAdapter else {
            return nil
        }
        return adapter.location()
    }
    
    
    // MARK: - Route
    
    func navigator(_ url: String?) {
        guard let url = url,
              let adapter = JYWKAdapterManager.sharedInstance.eventAdapter else {
            return
        }
        DispatchQueue.main.async {
            adapter.navigator(url)
        }
    }
    
    
    // MARK: - Track
    
    func trackClick(_ params: Dictionary<String, Any>?) {
        guard let params = params else {
            return
        }
        if let adapter = JYWKAdapterManager.sharedInstance.trackAdapter {
            adapter.trackClick(params)
        }
    }
    
    func trackCustom(_ params: Dictionary<String, Any>?) {
        guard let params = params else {
            return
        }
        if let adapter = JYWKAdapterManager.sharedInstance.trackAdapter {
            adapter.trackCustom(params)
        }
    }
    
    func trackExpose(_ params: Dictionary<String, Any>?) {
        guard let params = params else {
            return
        }
        if let adapter = JYWKAdapterManager.sharedInstance.trackAdapter {
            adapter.trackExpose(params)
        }
    }
    
    
    // MARK: - Preload
    
    func preloadDSLStyle(_ url:String?) {
        guard let url = url,
              let adapter = JYWKAdapterManager.sharedInstance.eventAdapter else {
            return
        }
        DispatchQueue.main.async {
            adapter.preloadDSLStyle(url)
        }
    }
    
}
