package com.hellobike.magiccube.share

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.hellobike.magiccube.utils.UIUtils


object WKLifecycleCore : Application.ActivityLifecycleCallbacks {

    private var hasInit = false
    private var lifecycleDataMap: HashMap<Activity, HashMap<String, ILifecycleData>>? = null

    fun <T : ILifecycleData> getLifecycleData(context: Activity, key: String, newInstance: (context: Context) -> T): T {
        synchronized(this) {
            init(context)

            val lifecycleDataMap = lifecycleDataMap ?: HashMap()
            var container = lifecycleDataMap[context]
            if (container == null) {
                container = HashMap()
                lifecycleDataMap[context] = container
            }

            val obj = container[key] as? T ?: newInstance(context)

            container[key] = obj

            this.lifecycleDataMap = lifecycleDataMap
            return obj
        }
    }

    private fun init(activity: Activity) {
        if (!hasInit) {
            activity.application.registerActivityLifecycleCallbacks(this)
            hasInit = true
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
        if (!UIUtils.contextIsValid(activity)) {
            releaseAllLifecycleData(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (!UIUtils.contextIsValid(activity)) {
            releaseAllLifecycleData(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        releaseAllLifecycleData(activity)
    }

    private fun releaseAllLifecycleData(activity: Activity) {
        synchronized(this) {
            val container = lifecycleDataMap?.remove(activity)
            container?.forEach {
                it.value.onDestroy()
            }
            if (lifecycleDataMap.isNullOrEmpty()) {
                lifecycleDataMap = null
                activity.application.unregisterActivityLifecycleCallbacks(this)
                hasInit = false
            }
        }
    }
}