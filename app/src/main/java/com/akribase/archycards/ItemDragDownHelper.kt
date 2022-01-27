package com.akribase.archycards

import android.content.Context
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class ItemDragDownHelper(
    val context: Context,
    private val rvstate: MutableLiveData<RvState>,
    var dragThreshold: Float = 0.5f,
    var viewScaleFactor: Float = 1.2f
) {
    private var rv: RecyclerView? = null
    private var gestureDetector = GestureDetector(context, DragGestureListener())
    private var itemTouchListener = DragItemTouchListener()
    private var selected: Selected? = null

    private fun ViewPropertyAnimator.setParams() {
        interpolator = AccelerateInterpolator(0.8f)
        duration = 300
    }

    fun attachToRv(recyclerView: RecyclerView) {
        rv = recyclerView
        rv?.addOnItemTouchListener(itemTouchListener)
    }

    fun calculateMaxTranslation(): Int {
        val itemView = selected?.selected?.itemView
        val rv = rv
        if (rv == null || itemView == null) return 0

        val rvHeight = rv.height
        val viewPos = rv.getChildRelativePos(itemView)
        return rvHeight - viewPos.top
    }

    fun calculateProgress(translation: Float, maxTranslation: Int) = translation.div(maxTranslation)

    fun canStartDragging(view: View): Boolean {
        val vh = rv?.findContainingViewHolder(view)
        return rvstate.value?.snapPosition == vh?.layoutPosition
    }

    inner class DragItemTouchListener : RecyclerView.OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(e)
            return selected != null
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            gestureDetector.onTouchEvent(e)

            val selectedConst = selected ?: return

            when (e.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    val progress = rvstate.value?.progress ?: 0f
                    val itemView = selectedConst.selected.itemView

                    if (progress <= dragThreshold) {
                        itemView.animate()
                            .translationY(0f)
                            .scaleX(1f)
                            .scaleY(1f).setParams()
                    }

                    selected = null
                    rvstate.value = rvstate.value?.copy(isLongPressed = false)
                }
                MotionEvent.ACTION_MOVE -> {
                    val maxTranslation = calculateMaxTranslation()
                    val progress = calculateProgress(selectedConst.translation, maxTranslation)
                    rvstate.value = rvstate.value?.copy(progress = progress)

                    val diff = e.rawY - selectedConst.initialY
                    if (diff > 0) {
                        val v = selectedConst.selected.itemView
                        v.translationY = diff
                        selected?.translation = diff
                    }
                }
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    inner class DragGestureListener : GestureDetector.SimpleOnGestureListener() {

        // Best practice to implement onDown as it is the starting point of all events
        override fun onDown(e: MotionEvent?) = true

        override fun onLongPress(e: MotionEvent?) {
            e?.apply {
                val v = rv?.findChildViewUnder(x, y)
                if (v != null && canStartDragging(v)) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    rv?.findContainingViewHolder(v)?.let {
                        selected = Selected(it, e.rawY)
                        rvstate.value = rvstate.value?.copy(isLongPressed = true)
                    }

                    // Scale on selected animation
                    v.animate()
                        .scaleX(viewScaleFactor)
                        .scaleY(viewScaleFactor)
                        .translationZBy(10.pxToDp(context))
                        .setParams()
                }
            }
        }
    }

    data class Selected(
        val selected: RecyclerView.ViewHolder,
        val initialY: Float,
        var translation: Float = 0f
    )
}