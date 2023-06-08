package com.hellobike.magiccube.parser.widget

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.parser.engine.Engine
import com.hellobike.magiccube.v2.template.Template

open class ContainerWidget(context: Context) : BaseWidget<ViewGroup>(context) {

    protected val children: ArrayList<IWidget> = ArrayList()

    var engine: Engine? = null

    // 切换引擎
    fun useEngine(engine: Engine) {
        if (this.engine != engine) {
            this.engine = engine
            initEngine(engine)
        }
    }

    override fun attachToParent(parent: ContainerWidget?) {
        super.attachToParent(parent)
        this.engine = parent?.engine
    }

    override fun initView() = viewEngine.createContainer(context)

    fun addChild(widget: IWidget) {
        viewEngine.addChild(getView(), widget.getView(), widget.getNodeAdapter())
        children.add(widget)
    }

    fun addChild(widget: IWidget, index: Int) {
        viewEngine.addChild(getView(), widget.getView(), index, widget.getNodeAdapter())
        children.add(index, widget)
    }

    fun indexOfChild(view: View): Int {
        return viewEngine.indexOfChild(getView(), view)
    }

    fun indexOfChild(widget: IWidget): Int {
        return indexOfChild(widget.getView())
    }

    fun contains(widget: IWidget): Boolean {
        return contains(widget.getView())
    }

    fun containsV2(widget: IWidget): Boolean {
        return getView() == widget.getView().parent
    }

    fun removeChild(widget: IWidget) {
        removeChild(widget.getView())
        children.remove(widget)
    }

    private fun removeChild(view: View) {
        getView().removeView(view)
    }

    private fun contains(view: View): Boolean {
        return getView().contains(view)
    }

    override fun handlePressedRender(viewModel: BaseViewModel, template: Template) {
        super.handlePressedRender(viewModel, template)
        val layoutViewModel = viewModel as? LayoutViewModel ?: return

        val backgroundImage =  template.getValue(layoutViewModel.activeBackgroundImage).stringValue()

        if (!backgroundImage.isNullOrBlank()) {
            if (context is Activity) {
                if (!DSLParser.isDestroy(context)) {
                    renderBackgroundImage(backgroundImage)
                }
            } else {
                renderBackgroundImage(backgroundImage)
            }
        }
    }

    override fun handleDefaultRender(viewModel: BaseViewModel, template: Template) {
        super.handleDefaultRender(viewModel, template)
        val layoutViewModel = viewModel as? LayoutViewModel ?: return

        val backgroundImage =  template.getValue(layoutViewModel.backgroundImage).stringValue()

        if (!backgroundImage.isNullOrBlank()) {
            if (context is Activity) {
                if (!DSLParser.isDestroy(context)) {
                    renderBackgroundImage(backgroundImage)
                }
            } else {
                renderBackgroundImage(backgroundImage)
            }
        }
    }

    private fun renderBackgroundImage(backgroundImage: String) {

        var width = getView().layoutParams.width
        var height = getView().layoutParams.height

        if (width <= 0) {
            width = getView().measuredWidth
        }
        if (height <= 0) {
            height = getView().measuredHeight
        }

        if (width > 0 && height > 0) {
            Glide.with(context).load(backgroundImage)
                .dontTransform()
                .override(width, height)
                .into(object : SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(
                        resource: GlideDrawable?,
                        glideAnimation: GlideAnimation<in GlideDrawable>?
                    ) {
                        if (resource != null) {
                            getView().background = resource
                            if (resource is GifDrawable) {
                                resource.start()
                            }
                        }
                    }
                })
        } else {
            Glide.with(context).load(backgroundImage)
                .dontTransform()
                .into(object : SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(
                        resource: GlideDrawable?,
                        glideAnimation: GlideAnimation<in GlideDrawable>?
                    ) {
                        if (resource != null) {
                            getView().background = resource
                            if (resource is GifDrawable) {
                                resource.start()
                            }
                        }
                    }
                })
        }
    }
}