package com.hellobike.magiccube.demo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hellobike.magiccube.R
import com.hellobike.magiccube.v2.OnLoadStateListener
import com.hellobike.magiccube.v2.configs.MagicInfo
import com.hellobike.magiccube.widget.MagicBoxLayout

class MagicListActivity : AppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    private val templateUrls: Array<String> = arrayOf(
        "https://max-admaster-public.oss-cn-hangzhou.aliyuncs.com/temp/style/3bbc7b4e69804904aaa95cf9d65a61bd.json?supportVersion=6.30.0&md5=a807ec9b59be5d092e04680b3c348564&logic=1&scene=haha01&subScene=lele01",
        "https://max-admaster-public.oss-cn-hangzhou.aliyuncs.com/temp/style/558876e4cf8540a39cbdbb067aa6d42b.json?supportVersion=6.0.0&md5=57271077b03ad082b47d01e7c38bbaef&logic=1&scene=haha02&subScene=lele02",
        "https://admaster-public.51downapp.cn/temp/style/08aed6efba0b448194f97c7b179c9754.json?supportVersion=6.0.0&md5=ee55e819c4b02a51c248c55c5b6e5a42&logic=1&scene=haha03&subScene=lele03",
        "https://admaster-public.51downapp.cn/temp/style/9e9046763db64d33bdece00d14281cc2.json?supportVersion=6.0.0&md5=0e48374f21bdc822e6eccbb6cfdfd62a&logic=1&scene=haha04&subScene=lele04"
    )

    private val covers: Array<String> = arrayOf(
        "https://ss1.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/9e3df8dcd100baa1dc4fda014c10b912c8fc2e2c.jpg",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2Fba%2Fb0%2F1d%2Fbab01de371ce89ce0f44606d67fbc7a1.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642904114&t=0d103d88b47221ad878225bc5aea91e2",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic_source%2Fd0%2F34%2Ffc%2Fd034fc6183731d078d1c1808ac9d6c5f.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642904114&t=19dd8c73d9992e467cf30406354dece5",
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.kttpdq.com%2Fpic%2F1%2F149%2F2bcc77e6e04f6203.jpg&refer=http%3A%2F%2Fimg.kttpdq.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642904114&t=07171caf7f79114bfc067f9e6ed901cc"
    )

    private lateinit var recyclerView: RecyclerView
    private val layoutManager by lazy {
        LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    private val adapter by lazy { MagicAdapter(this, ArrayList()) }

    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        recyclerView = findViewById(R.id.recyclerView)

        startTime = System.currentTimeMillis()
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(this)


        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        initData()
    }

    override fun onGlobalLayout() {
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private fun initData() {

        val datas = ArrayList<MagicCubeItemData>()

        for (i in 0 until 100) {
            datas.add(MagicCubeItemData().apply {

                val type = i % templateUrls.size

                this.templateType = type

                this.templateUrl = templateUrls[type]

                val data = HashMap<String, Any?>()
                data["cover"] = covers[i % covers.size]
                data["title"] = "海贼王${i}"
                data["subTitle"] = "海贼王漫画连载中${i}"
                data["author"] = "${i}尾田荣一郎"
                data["content"] =
                    "${i}《航海王》是日本漫画家尾田荣一郎作画的少年漫画作品，于1997年7月22日在集英社《周刊少年Jump》开始连载。改编的电视动画《航海王》于1999年10月20日起在富士电视台首播。"
                data["date"] = "${i}1999年10月20日"
                data["advert"] = "广告"

                data["info"] = "${i}29.9w人正在围观"
                data["avatar01"] = covers[0]
                data["avatar02"] = covers[1]
                data["avatar03"] = covers[2]

                data["cover01"] = covers[0]
                data["cover02"] = covers[1]
                data["cover03"] = covers[2]
                data["cover04"] = covers[3]
                data["index"] = i.toString()

                this.templateData = data
            })
        }
        adapter.setNewData(datas)
    }

    private class MagicCubeItemData : MultiItemEntity {

        var templateUrl: String? = null
        var templateType: Int = 0
        var templateData: HashMap<String, Any?>? = null

        override fun getItemType(): Int = templateType
    }

    private inner class MagicAdapter(val context: Context, data: List<MagicCubeItemData>?) :
        BaseMultiItemQuickAdapter<MagicCubeItemData, BaseViewHolder>(data) {

        init {
            for (type in templateUrls.indices) {
                Log.d("fhpfhp", "type: ${type}")
                addItemType(type, R.layout.item_magic_cube)
            }
        }

        override fun convert(helper: BaseViewHolder, item: MagicCubeItemData) {

            val magicBoxLayout = helper.getView<MagicBoxLayout>(R.id.magicBoxLayout)

            val magicInfo = MagicInfo.Builder()
                .build()
            magicBoxLayout.loadByUrl(
                item.templateUrl,
                item.templateData,
                magicInfo,
                object : OnLoadStateListener {
                    override fun onLoadSuccess() {
                        Log.d("HBAndroidDSLSDK", "onLoadSuccess")

                    }

                    override fun onLoadFailed(code: Int, msg: String?) {
                        Log.d("HBAndroidDSLSDK", "onLoadFailed: $code $msg")
                    }

                }
            )

        }
    }


}