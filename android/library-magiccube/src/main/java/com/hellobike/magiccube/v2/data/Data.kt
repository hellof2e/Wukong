package com.hellobike.magiccube.v2.data

import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.template.Grammar
import org.json.JSONArray
import org.json.JSONObject

/**
 * @param data 数据
 */
open class Data(private val data: Any?) {

    private var mcTimerData: HashMap<String, Any>? = null

    fun originData(): Any? = data

    var scopePrefixName: String? = null
    var scopeIndexPrefixName: String? = null

    fun bindScopePrefixName(scopePrefixName: String?, scopeIndexPrefixName: String?) {
        this.scopePrefixName = scopePrefixName
        this.scopeIndexPrefixName = scopeIndexPrefixName
    }

    fun bindCountDownData(type: String, time: Long) {

        if (mcTimerData == null) mcTimerData = HashMap()

        val timerData = mcTimerData!!

        if (type == Constants.COUNTING_TYPE_DATETIME) { // datetime: 可显示 天，时，分，秒
            val day = time / 86400000 // 24 * 60 * 60 * 1000
            val hour = (time - day * 86400000) / 3600000
            val minute = (time - day * 86400000 - hour * 3600000) / 60000
            val second = (time - day * 86400000 - hour * 3600000 - minute * 60000) / 1000
            timerData["day"] = UIUtils.decimalFormat00(day)
            timerData["hour"] = UIUtils.decimalFormat00(hour)
            timerData["minute"] = UIUtils.decimalFormat00(minute)
            timerData["second"] = UIUtils.decimalFormat00(second)
        } else { // 默认是 time: 可显示 时，分，秒
            val hour = time / 3600000
            val minute = (time - hour * 3600000) / 60000
            val second = (time - hour * 3600000 - minute * 60000) / 1000
            timerData["hour"] = UIUtils.decimalFormat00(hour)
            timerData["minute"] = UIUtils.decimalFormat00(minute)
            timerData["second"] = UIUtils.decimalFormat00(second)
        }
//        logd(timerData.toString())
    }

    open fun parseListItemIndex(): Int = -1

    /**
     * @param template 模版。比如: ${fieldName}、 ${listItem.fieldName} ...
     */
    fun parseFieldTemplate(template: String?): SafeMap {
        if (template.isNullOrBlank()) return Constants.NULL_VALUE

        if (Grammar.isFieldTemplate(template)) {
            // 例如: ${map.iconUrl}  => [map, iconUrl] , [liteItem, map, url]
            // 例如: ${LOCAL.userToken} => [LOCAL, userToken]
            val keys = if (template.contains("[")) { // list[1] => list.0
                Grammar.formatFieldTemplate(template.replace("[", ".").replace("]", "")).split(".")
            } else {
                Grammar.formatFieldTemplate(template).split(".")
            }

//            val keys = Grammar.formatFieldTemplate(template).split(".")

            if (keys.isNullOrEmpty()) {
                return Constants.NULL_VALUE
            }

            return if (keys.getOrNull(0) == "LOCAL") { // 例如: LOCAL.token
                LocalMethodBridge.parseLocalMethod(keys.getOrNull(1) ?: "")
            } else if (keys.size == 1 && !scopeIndexPrefixName.isNullOrBlank() && keys[0] == scopeIndexPrefixName)  {
                SafeMap(parseListItemIndex())
            }
            else {
                parseData(keys)
            }
        }

        return Constants.NULL_VALUE
    }


    private fun parseData(keys: List<String>): SafeMap {
        var dataTemp: Any? = data

        var keyTemp = ""
        for ((index, key) in keys.withIndex()) {

            if (key.isBlank()) continue

            // 有前缀，并且是以 listItem 开头
            if (!scopePrefixName.isNullOrBlank() && key == scopePrefixName && index == 0) continue

            keyTemp = key

            if (keyTemp == Constants.KEY_COUNT_DOWN_TIMER) { // 处理 MC_TIMER 数据
                dataTemp = mcTimerData
                continue
            } else if (keyTemp.endsWith(")")) { // 说明是要处理函数
                if (keyTemp == Constants.KEY_SIZE) { // 处理获取数组大小的逻辑
                    dataTemp = LocalMethodBridge.parseFuncSize(dataTemp)
                    continue
                }
            }

            dataTemp = if (dataTemp is Map<*, *>) {
                dataTemp[keyTemp]
            } else if (dataTemp is JSONObject) {
                dataTemp.opt(keyTemp)
            } else if (dataTemp is List<*>) {
                dataTemp.getOrNull(keyTemp.toIntOrNull() ?: -1)
            } else if (dataTemp is JSONArray) {
                dataTemp.opt(keyTemp.toIntOrNull() ?: -1)
            } else null
        }

        return SafeMap(dataTemp)
    }

    fun originHashMapData(): HashMap<String, Any?> {
        return originData() as HashMap<String, Any?>
    }

    fun mergeData(newData: Map<String, Any?>) {
        originHashMapData().putAll(newData)
    }

    fun resetData(newData: Map<String, Any?>) {
        val originHashMapData = originHashMapData()
        originHashMapData.clear()
        originHashMapData.putAll(newData)
    }
}