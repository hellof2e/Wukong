package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.MagicCard
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.js.bridges.wk.*
import com.hellobike.magiccube.v2.js.bridges.wk.BaseCardWKBridge
import kotlin.jvm.Throws

internal class JSEngineInitializer {

    private var jsEngine: IJsEngine? = null

    private val bridges = WKBridges.createBridges()

    private var magicCard: MagicCard? = null
    private var magicConfig: MagicConfig? = null
    private var logic: String? = null
    private var data: Data? = null
    private var copyDataMap: HashMap<String, Any?>? = null

    companion object {
        fun initJSEngine(jsEngine: IJsEngine, magicConfig: MagicConfig, magicCard: MagicCard) {
            jsEngine.getWkVoidBridges().forEach {
                val value = it.value
                if (value is BaseCardWKBridge) {
                    value.bindMagicConfig(magicConfig)
                    value.bindMagicCard(magicCard)
                }
            }
        }
    }

    fun getData() = data

    @Throws(Throwable::class)
    fun load(loadLogic: Boolean): IJsEngine {
        val magicConfig = this.magicConfig
        val magicCard = this.magicCard
        val logic = this.logic
        val data = this.data

        val jsEngine = this.jsEngine ?: MagicCardJsEngine()

        bridges.forEach {
            jsEngine.registerWkBridge(it)
            if (it is BaseCardWKBridge) {
                if (magicConfig != null) it.bindMagicConfig(magicConfig)
                if (magicCard != null) it.bindMagicCard(magicCard)
                if (data != null) it.bindData(data)
            }
        }

        val copyDataMap = copyDataMap
        if (!logic.isNullOrBlank() && data != null) {
            jsEngine.initLogic(logic, if (!copyDataMap.isNullOrEmpty()) copyDataMap else data.originHashMapData(), loadLogic)
        }
        return jsEngine
    }


    fun installMagicCard(magicCard: MagicCard): JSEngineInitializer {
        this.magicCard = magicCard
        return this
    }

    fun installMagicConfig(magicConfig: MagicConfig): JSEngineInitializer {
        this.magicConfig = magicConfig
        return this
    }

    fun installLogic(logic: String?): JSEngineInitializer {
        this.logic = logic
        return this
    }

    fun installData(data: Data): JSEngineInitializer {
        this.data = data
        tryCatch {
            this.copyDataMap = data.originHashMapData().clone() as? HashMap<String, Any?>
        }
        return this
    }


}