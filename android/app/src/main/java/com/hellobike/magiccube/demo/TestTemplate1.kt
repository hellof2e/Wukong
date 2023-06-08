package com.hellobike.magiccube.demo

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hellobike.magiccube.model.TypeNames
import com.hellobike.magiccube.v2.template.ICustomWidgetTemplate
import com.hellobike.magiccube.widget.BaseCustomWidget


@TypeNames(["c-img1", "c-img2"])
class TestTemplate1(context: Context) : BaseCustomWidget(context) {

    private val imageView: ImageView by lazy { ImageView(context) }

    override fun initView(context: Context, typeName: String): View = imageView

    override fun render(template: ICustomWidgetTemplate) {
        super.render(template)
        val data = template.getData() as? Map<String, Any?> ?: return
        val img = data["img"] as? String ?: return
        Glide.with(context).load(img).into(imageView)
    }
}