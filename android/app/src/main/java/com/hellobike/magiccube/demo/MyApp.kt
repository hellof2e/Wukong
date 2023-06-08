package com.hellobike.magiccube.demo

import android.app.Application
import android.widget.Toast
import com.hellobike.magiccube.demo.bridges.MyCustomBridge01
import com.hellobike.magiccube.v2.configs.*

class MyApp: Application() {

    companion object {
        var application: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        val starer = MagicStarer.Builder()
            .bindDebug(true)
            .bindGlobalClickHandler(object : IGlobalClickHandler {
                override fun onCardClick(url: String) {
                    Toast.makeText(this@MyApp, "全局点击事件: $url", Toast.LENGTH_SHORT).show()
                }
            })
            .bindLocationManager(object : ILocationManager {
                override fun getLocationInfo(): HashMap<String, Any?> {
                    return HashMap()
                }
            })
            .bindReportEvent(object : IReportEvent {
                override fun trackClick(params: Map<String, Any?>?) {
                    logd("trackClick >> $params")
                }

                override fun trackCustom(params: Map<String, Any?>?) {
                    logd("trackCustom >> $params")
                }

                override fun trackExpose(params: Map<String, Any?>?) {
                    logd("trackExpose >> $params")
                }
            })
            .addWKJSBridges("bridge01", MyCustomBridge01::class.java)
            .bindUserManager(object : IUserManager {
                override fun hasLogin(): Boolean = true

                override fun getUserInfo(): HashMap<String, Any?> {
                    return HashMap()
                }

                override fun gotoLogin(handler: IUserManager.ILoginHandler) {
                    handler.handleLoginSuccess()
                }
            })
            .bindRouter(object : IRouter {
                override fun navigator(url: String) {
                    Toast.makeText(this@MyApp, "navigator: $url", Toast.LENGTH_SHORT).show()
                }
            })
            .build(this)
        MagicCube.doInit(starer)
    }
}