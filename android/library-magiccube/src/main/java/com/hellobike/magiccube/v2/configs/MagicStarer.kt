package com.hellobike.magiccube.v2.configs

import android.content.Context
import java.lang.RuntimeException


class MagicStarer private constructor(val context: Context, builder: Builder) {
    internal var debug: Boolean = builder.debug
    internal var locationManager: ILocationManager? = builder.locationManager
    internal var clickHandler: IGlobalClickHandler? = builder.clickHandler
    internal var userManager: IUserManager? = builder.userManager
    internal var router: IRouter? = builder.router
    internal var net: INetHandler? = builder.net
    internal var deviceInfo: IDeviceInfo? = builder.deviceInfo
    internal var globalBridges: HashMap<String, Any> = builder.globalBridges
    internal var reportEvent: IReportEvent? = builder.reportEvent
    internal var wkJSBridges: HashMap<String, Class<out ICardJSBridge>> = builder.wkJSBridges

    class Builder {
        internal var debug: Boolean = true
        internal var locationManager: ILocationManager? = null
        internal var clickHandler: IGlobalClickHandler? = null
        internal var userManager: IUserManager? = null
        internal var router: IRouter? = null
        internal var net: INetHandler? = null
        internal var deviceInfo: IDeviceInfo? = null
        internal var globalBridges: HashMap<String, Any> = HashMap()
        internal var reportEvent: IReportEvent? = null
        internal var wkJSBridges: HashMap<String, Class<out ICardJSBridge>> = HashMap()

        fun bindDebug(debug: Boolean): Builder {
            this.debug = debug
            return this
        }
        fun bindLocationManager(locationManager: ILocationManager): Builder {
            this.locationManager = locationManager
            return this
        }

        fun bindGlobalClickHandler(clickHandler: IGlobalClickHandler): Builder {
            this.clickHandler = clickHandler
            return this
        }

        fun bindUserManager(userManager: IUserManager): Builder {
            this.userManager = userManager
            return this
        }

        fun bindRouter(router: IRouter): Builder {
            this.router = router
            return this
        }

        fun bindReportEvent(reportEvent: IReportEvent): Builder {
            this.reportEvent = reportEvent
            return this
        }

        fun bindDeviceInfo(deviceInfo: IDeviceInfo): Builder {
            this.deviceInfo = deviceInfo
            return this
        }

        fun bindNetHandler(net: INetHandler): Builder {
            this.net = net
            return this
        }

        fun addGlobalJSBridges(interfaceName: String, obj: Any): Builder {
            val any = this.globalBridges[interfaceName]
            if (any != null) {
                throw RuntimeException("$interfaceName 已经被注册！${any.javaClass.canonicalName}")
            }
            this.globalBridges[interfaceName] = obj
            return this
        }

        fun addWKJSBridges(methodName: String, clz: Class<out ICardJSBridge>): Builder {
            val clazz = this.wkJSBridges[methodName]
            if (clazz != null) {
                throw RuntimeException("$methodName 已经被注册！${clazz.canonicalName}")
            }
            this.wkJSBridges[methodName] = clz
            return this
        }

        fun build(context: Context): MagicStarer {
            if (this.userManager == null) {
                this.userManager = UserManagerDefault()
            }
            if (this.locationManager == null) {
                this.locationManager = DefaultLocationManager()
            }
            if (this.net == null) {
                this.net = DefaultNetHandler()
            }
            if (this.deviceInfo == null) {
                this.deviceInfo = DefaultDeviceInfo()
            }
            return MagicStarer(context.applicationContext, this)
        }
    }
}