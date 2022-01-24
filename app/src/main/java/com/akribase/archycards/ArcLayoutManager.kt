package com.akribase.archycards

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.floor
import kotlin.math.sin

class ArcLayoutManager(
    resources: Resources,
    private val screenWidth: Int,
    private val viewWidth: Int,
    private val viewHeight: Int,
):RecyclerView.LayoutManager() {

    var scrollEnabled = true
    private var horizontalScrollOffset = 0

    private val recyclerViewHeight =
        (resources.getDimensionPixelSize(R.dimen.recyclerview_height)).toDouble()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(viewWidth, viewHeight)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        fill(recycler, state)
    }

    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)

        // Looping
        val firstVisiblePosition =
            floor(horizontalScrollOffset.toDouble() / viewWidth.toDouble()).toInt()
        val lastVisiblePosition = (horizontalScrollOffset + screenWidth) / viewWidth

        for (index in firstVisiblePosition..lastVisiblePosition) {
            var recyclerIndex = index % itemCount
            if (recyclerIndex < 0) {
                recyclerIndex += itemCount
            }
            val view = recycler.getViewForPosition(recyclerIndex)
            addView(view)

            layoutChildView(index, viewWidth, view)
        }

        // Remove scrap views
        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }

    private fun layoutChildView(i: Int, viewWidthWithSpacing: Int, view: View) {
        val left = i * viewWidthWithSpacing - horizontalScrollOffset
        val right = left + viewWidth

        val viewCentreX = left + viewWidth / 2
        val s: Double = screenWidth.toDouble() / 2
        val h: Double = recyclerViewHeight
        val radius: Double = (h * h + s * s) / (h * 2)
        val cosAlpha = (s - viewCentreX) / radius
        val alpha = acos(MathUtils.clamp(cosAlpha, -1.0, 1.0))
        val yComponent = radius - (radius * sin(alpha))

        val top = (h - yComponent).toInt()
        val bottom = top + viewHeight

        // View Rotation
        view.rotation = 90f - (alpha * (180 / PI)).toFloat()

        // Measure
        measureChildWithMargins(view, 0, 0)
        // Layout
        layoutDecoratedWithMargins(view, left, top, right, bottom)
    }

    override fun canScrollHorizontally() = scrollEnabled

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        horizontalScrollOffset += dx
        fill(recycler, state)
        return dx
    }
}