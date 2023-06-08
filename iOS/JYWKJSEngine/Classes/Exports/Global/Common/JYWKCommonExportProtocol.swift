//
//  JYWKCommonExportProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/11.
//

import Foundation
import JavaScriptCore

@objc protocol JYWKCommonExportProtocol: JSExport {
    
    // MARK: - Cache
    
    /// 缓存数据到本地(异步)
    func cacheItem(_ fileName: String?, _ key: String?, _ value: String?)
    /// 读取本地缓存(同步)
    func readItem(_ fileName: String?, _ key: String?) -> String?
    
    
    // MARK: - Toast
    
    func toast(_ content: String?)
    
    
    // MARK: - UserInfo
    
    func getUserInfo() -> Dictionary<String, Any>?
    
    
    // MARK: - DeviceInfo
    
    func getDeviceInfo() -> Dictionary<String, Any>
    
    
    // MARK: - Location
    
    func getLocation() -> Dictionary<String, Any>?
    
    
    // MARK: - Route
    
    func navigator(_ url: String?)
    
    
    // MARK: - Track
    
    func trackClick(_ params: Dictionary<String, Any>?)
    func trackCustom(_ params: Dictionary<String, Any>?)
    func trackExpose(_ params: Dictionary<String, Any>?)
    
    
    // MARK: - Preload
    
    func preloadDSLStyle(_ url: String?)
    
}
