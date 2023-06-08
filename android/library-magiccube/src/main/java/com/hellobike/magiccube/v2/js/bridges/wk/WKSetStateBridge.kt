package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.ext.logw
import com.hellobike.magiccube.v2.js.bridges.model.SetStateFuncArgs
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import org.json.JSONObject

internal class WKSetStateBridge : BaseCardWKBridge(), IWKVoidBridge {

    override fun getKey(): String = "setState"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        // 卡片被销毁
        if (!isValidMagicCard()) return

        val magicCard = getMagicCard() ?: return
        val config = getMagicConfig() ?: return

        val data = getData() ?: return


        val paramJsonObject = args?.getJSObject(0)?.toJSONObject() ?: JSONObject()

        val setStateFuncArgs = SetStateFuncArgs.fromJSONObject(paramJsonObject)

        val jsDataMap = main.getJSData()

        magicCard.getCardContext().runOnUiThread {
            if (!isValidMagicCard()) return@runOnUiThread
            if (setStateFuncArgs.datasetChanged) { // 数据发生改变
                if (jsDataMap != null) {
                    data.resetData(jsDataMap)
                    if (isSameSession()) {
                        val newDataMap = data.originHashMapData()
                        config.magicParams?.onCubeDataChangedListener?.onDataUpdated(newDataMap)
                        magicCard.updateData(config.styleUrl ?: "", newDataMap)
                    } else {
                        logw("无法执行 setState , sessionId 发生改变，可能是卡片被复用导致！")
                    }
                }
            }
        }
    }
}