package com.hellobike.magiccube.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hellobike.magiccube.model.TypeName
import com.hellobike.magiccube.model.TypeNames
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.MagicCard
import com.hellobike.magiccube.v2.OnLoadStateListener
import com.hellobike.magiccube.v2.click.LoadingHandler
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.configs.MagicInfo
import com.hellobike.magiccube.v2.click.OnCardClickHandler
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.preload.IMetaData
import com.hellobike.magiccube.v2.reports.Codes
import com.hellobike.magiccube.v2.reports.session.SessionResult
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MagicBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
    }

    private val cusMap = ConcurrentHashMap<String, BaseCustomWidget>()

    private val card = MagicCard(this)

    private val loading = LoadingHandler(context)

    /**
     * 注册自定义视图模板，clazz必须实现MagicCubeCustomerTemplate接口，并绑定TypeName注解，否则不生效
     * */
    fun registerCustomerTemplate(factory: BaseCustomWidget) {
        if (factory::class.java.isAnnotationPresent(TypeName::class.java)) {
            val type = factory::class.java.getAnnotation(TypeName::class.java)!!.name
            cusMap[type] = factory
        } else if (factory::class.java.isAnnotationPresent(TypeNames::class.java)) {
            val types = factory::class.java.getAnnotation(TypeNames::class.java)!!.names
            for (type in types) {
                cusMap[type] = factory
            }
        }
    }


    private fun defaultMagicInfo(): MagicInfo {
        return MagicInfo.Builder().build()
    }

    suspend fun loadByUrlSuspend(
        url: String?,
        data: HashMap<String, Any?>?,
        magicInfo: MagicInfo? = null
    ) = suspendCoroutine<Int> {
        var resumed = false
        loadByUrl(url, data, magicInfo, object : OnLoadStateListener {
            override fun onLoadSuccess() {
                try {
                    if (!resumed) {
                        it.resume(Codes.SUCCESS)
                    }
                    resumed = true
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }

            override fun onLoadFailed(code: Int, msg: String?) {
                try {
                    if (!resumed) {
                        it.resume(code)
                    }
                    resumed = true
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        })
    }


    fun loadByUrl(
        url: String?,
        data: HashMap<String, Any?>?,
        magicInfo: MagicInfo? = null,
        listener: OnLoadStateListener? = null
    ) {
        val session = SessionResult()
        val metaData: IMetaData? = magicInfo?.metaData
        val config = MagicConfig.Builder()
            .injectTemplateFactory(cusMap)
            .injectOnCardClickListener(OnCardClickHandler(magicInfo))
            .bindOnCubeCountDownListener(magicInfo?.onCubeCountDownListener)
            .injectMagicParams(magicInfo)
            .injectLoadingHandler(loading)
            .injectStyleUrl(url)
            .injectSession(session)
            .injectData(data)
            .build()

        var resumed = false
        card.setOnLoadStateListener(object : OnLoadStateListener {
            override fun onLoadSuccess() {
                if (!resumed) {
                    listener?.onLoadSuccess()
                }
                resumed = true
            }

            override fun onLoadFailed(code: Int, msg: String?) {
                if (!resumed) {
                    listener?.onLoadFailed(code, msg)
                    loge("渲染失败: [$code, $msg]")
                }
                resumed = true
            }
        })

        if (metaData != null) {
            card.loadByMetaData(metaData, config)
        } else {
            card.loadByUrl(url, data, config, magicInfo?.reload ?: false)
        }
    }


    /**
     * 从视图中移除
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!UIUtils.contextIsValid(context)) {
            card.onDestroy()
        }
    }

    /**
     * 视图焦点改变
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus && !UIUtils.contextIsValid(context)) {
            card.onDestroy()
        }
    }

    /**
     * 视图可见性
     */
    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (!UIUtils.contextIsValid(context)) {
            card.onDestroy()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (!UIUtils.contextIsValid(context)) {
            card.onDestroy()
        }
    }

    fun release() {
        card.onDestroy()
    }
}