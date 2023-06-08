//
//  JYWKNetworkParameter.swift
//  JYWKJSEngine
//
//  Created by gaoshuaibin091 on 2022/8/20.
//

import Foundation

class JYWKNetworkParameter: NSObject {
    var type: String? = "POST"
    var url: String?
    var data: Dictionary<String, Any>?
    var error: (() -> Void)?
    var success: ((Dictionary<String, Any>) -> Void)?
}
