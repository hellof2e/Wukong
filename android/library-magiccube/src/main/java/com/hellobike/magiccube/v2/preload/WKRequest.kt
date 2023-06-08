package com.hellobike.magiccube.v2.preload

import com.hellobike.magiccube.utils.EncryptUtils

class WKRequest(val url: String,val orgData: Map<String, Any?>) {

    var cacheToMemory: Boolean = false
    var tag: Any? = null

    val unitId: String? = try {
        EncryptUtils.encryptMD5ToString(orgData.toString(), url)
    } catch (t: Throwable) {
        null
    }

    var data: HashMap<String, Any?> = if (orgData is HashMap<*, *>) orgData as HashMap<String, Any?> else HashMap(orgData)
}