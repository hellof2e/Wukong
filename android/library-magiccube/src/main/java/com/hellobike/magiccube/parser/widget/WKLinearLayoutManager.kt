package com.hellobike.magiccube.parser.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WKLinearLayoutManager(
    context: Context, @RecyclerView.Orientation orientation: Int, reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {


    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        try {
            return super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        try {
            return super.scrollHorizontallyBy(dx, recycler, state)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return 0
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) =
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }
}