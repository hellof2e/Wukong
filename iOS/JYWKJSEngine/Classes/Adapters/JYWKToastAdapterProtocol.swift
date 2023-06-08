//
//  JYWKToastAdapterProtocol.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation

@objc public protocol JYWKToastAdapterProtocol: NSObjectProtocol {
    @objc func showToast(_ content: String)
}
