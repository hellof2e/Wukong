//
//  JYWKTrackAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation

@objc public protocol JYWKTrackAdapterProtocol: NSObjectProtocol {
    @objc func trackClick(_ params: Dictionary<String, Any>?)
    @objc func trackCustom(_ params: Dictionary<String, Any>?)
    @objc func trackExpose(_ params: Dictionary<String, Any>?)
}
