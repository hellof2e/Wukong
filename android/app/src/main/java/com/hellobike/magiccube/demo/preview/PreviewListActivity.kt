package com.hellobike.magiccube.demo.preview

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hellobike.magiccube.R
import com.hellobike.magiccube.demo.configs.GlobalConfig
import com.hellobike.magiccube.demo.preview.controller.IPreviewCallback
import com.hellobike.magiccube.demo.preview.controller.PreviewCache
import com.hellobike.magiccube.demo.preview.controller.PreviewController
import com.hellobike.magiccube.v2.OnLoadStateListener
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.configs.MagicInfo
import com.hellobike.magiccube.v2.preload.IMetaData
import com.hellobike.magiccube.v2.preload.WKRequest
import com.hellobike.magiccube.widget.MagicBoxLayout
import kotlinx.android.synthetic.main.activity_preview_list.*
import java.lang.Exception

class PreviewListActivity : AppCompatActivity(), IPreviewCallback {


    private val previewController = PreviewController(this)
    private val previewCache = PreviewCache(this)

    private var dialog: ContentLoadingProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_list)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()

        if (previewCache.getIp().trim().isNotBlank()) {
            etIp.setText(previewCache.getIp(), TextView.BufferType.NORMAL)
        }

        if (previewCache.getPort().trim().isNotBlank()) {
            etPort.setText(previewCache.getPort(), TextView.BufferType.NORMAL)
        }

        btnStart.setOnClickListener {

            val ipStr: String = etIp.text?.trim()?.toString() ?: ""

            if (ipStr.isBlank()) {
                Toast.makeText(this, "请输入正确的ip地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            previewCache.saveIp(ipStr)

            val portStr = etPort.text?.trim()?.toString() ?: "7788"

            if (portStr.isNotBlank()) {
                previewCache.savePort(portStr)
            }

            val url = "http://$ipStr:$portStr"
            previewController.previewOnce(url)
            showLoading()
        }

        btnStop.setOnClickListener {
            previewController.stopPreview()
        }

    }

    private fun showLoading() {
        if (dialog == null) {
            dialog = ContentLoadingProgressBar(this)
        }
        dialog?.show()
    }
    private fun hideLoading() {
        dialog?.hide()
        dialog = null
    }


    override fun onFailure(e: Exception) {
        e.printStackTrace()
        appendConsole("${System.currentTimeMillis()} >> onFailure: $e")
    }


    private fun clearConsole() {
        llConsoleContainer.removeAllViews()
    }

    private fun appendConsole(msg: String?) {
        if (msg.isNullOrBlank()) return

        val item = TextView(this)
        item.text = msg
        item.textSize = 12.0f
        item.setTextColor(Color.GREEN)
        llConsoleContainer.addView(item)
    }


    private fun newEngine(styleJson: String, dataJson: String) {

//        MagicCube.preloadJson(styleJson)

//        val data: HashMap<String, Any?> = JSONObject.parseObject(dataJson, object : TypeReference<HashMap<String, Any?>>() {})

        val list = ArrayList<MagicCubeItemData>()
        for (i in 0 until 100) {
            val magicCubeItemData = MagicCubeItemData()
            val data: HashMap<String, Any?> = JSONObject.parseObject(dataJson, object : TypeReference<HashMap<String, Any?>>() {})
            val hashMap = data.clone() as HashMap<String, Any?>
            hashMap["list_index"] = i
            (hashMap["list"] as? JSONArray)?.getJSONObject(0)?.put("pindex", i)
            magicCubeItemData.templateData = hashMap
            magicCubeItemData.templateJson = styleJson
            magicCubeItemData.templateType = 0

            val url = previewController.getStyleUrl(styleJson)
            magicCubeItemData.templateUrl = url
            list.add(magicCubeItemData)
        }

        val requestList = ArrayList<WKRequest>()
        list.forEachIndexed { index, magicCubeItemData ->
            if (index < 5) {
                val request = WKRequest(
                    magicCubeItemData.templateUrl!!,
                    magicCubeItemData.templateData as HashMap<String, Any?>
                )
                request.tag = magicCubeItemData
                requestList.add(request)
            }
        }



//        MagicCube.preloadMetaDataList(requestList, 500) { response ->
//            hideLoading()
//            response.data?.forEach {
//                (it.request?.tag as? MagicCubeItemData)?.metaData = it.data
//            }
//
//            val adapter = MagicAdapter(this, list)
//            recyclerView.adapter = adapter
//        }

//        MagicCube.preloadMetaDataToMemory(requestList) { response ->
            hideLoading()
            val adapter = MagicAdapter(this, list)
            recyclerView.adapter = adapter
//        }
    }

    override fun onSuccess(styleJson: String, dataJson: String) {
        clearConsole()
        newEngine(styleJson, dataJson)
    }

    override fun finish() {
        previewController.stopPreview()
        super.finish()
    }

    override fun onDestroy() {
        previewController.stopPreview()
        super.onDestroy()
    }


    private class MagicCubeItemData : MultiItemEntity {

        var templateJson: String? = null
        var templateType: Int = 0
        var templateData: HashMap<String, Any?>? = null
        var templateUrl: String? = null
        var metaData: IMetaData? = null

        override fun getItemType(): Int = templateType
    }

    private inner class MagicAdapter(val context: Context, data: List<MagicCubeItemData>?) :
            BaseMultiItemQuickAdapter<MagicCubeItemData, BaseViewHolder>(data) {

        init {
            addItemType(0, R.layout.item_magic_cube)
        }

        override fun convert(helper: BaseViewHolder, item: MagicCubeItemData) {

            val magicBoxLayout = helper.getView<MagicBoxLayout>(R.id.magicBoxLayout)

            val meteData = MagicCube.findMetaDataFromMemory(item.templateUrl, item.templateData)
            val magicInfo = MagicInfo.Builder()
                    .bindMetaData(meteData)
                    .build()

            GlobalConfig.applyCustomerTemplate(magicBoxLayout)
            magicBoxLayout.loadByUrl(
                item.templateUrl,
                item.templateData,
                magicInfo,
                object : OnLoadStateListener {
                    override fun onLoadSuccess() {

                    }

                    override fun onLoadFailed(code: Int, msg: String?) {

                    }

                }
            )
        }
    }

}