package com.hellobike.magiccube.demo.preview.controller

import android.content.Context
import android.content.SharedPreferences

class PreviewCache(private val context: Context) {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences("preview", Context.MODE_PRIVATE)
    }

    fun saveIp(ip: String) {
        sp.edit().putString("preview_ip", ip).apply()
    }

    fun savePort(port: String) {
        sp.edit().putString("preview_port", port).apply()
    }

    fun getIp(): String = sp.getString("preview_ip", "") ?: ""

    fun getPort(): String = sp.getString("preview_port", "7788") ?: "7788"

}