package com.hellobike.magiccube.demo

import android.content.Context
import android.view.View
import android.widget.TextView
import com.hellobike.magiccube.model.TypeName
import com.hellobike.magiccube.v2.template.ICustomWidgetTemplate
import com.hellobike.magiccube.widget.BaseCustomWidget

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2022/1/17 5:14 PM
 */
@TypeName("c-custom-tv")
class TestTemplate2(context: Context) : BaseCustomWidget(context) {

    private val tv by lazy { TextView(context) }

    override fun initView(context: Context, typeName: String): View {
        return tv
    }

    override fun render(template: ICustomWidgetTemplate) {
        super.render(template)
        tv.text = "hello"
        tv.postDelayed({
            tv.text = "hello world"
        }, 2000)
    }
}