//
//  JYWKCacheAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation

@objc public protocol JYWKCacheAdapterProtocol: NSObjectProtocol {
    /// 缓存数据到本地(异步)
    @objc func cacheItem(fileName: String, key: String, value: Any)
    /// 读取本地缓存(同步)
    @objc func readItem(fileName: String, key: String) -> Any?
}
