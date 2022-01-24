package com.akribase.archycards;

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context;
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.MotionEventCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

abstract class SwipeCallback(
    private val context: Context,
    private val rvstate: MutableLiveData<RvState>
) : ItemTouchHelper.SimpleCallback(0, 0) {

    private var maxTranslation: Int = 0
    private var rv: RecyclerView? = null
    private var selectedView: View? = null

    fun attachToRv(recyclerView: RecyclerView): ItemTouchHelper {
        rv = recyclerView
        return ItemTouchHelper(this).apply { attachToRecyclerView(recyclerView) }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val rvState = rvstate.value
        if (rvState?.snapPosition == viewHolder.layoutPosition) {
            return ItemTouchHelper.DOWN
        }
        return 0
    }

    override fun isLongPressDragEnabled() = true

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val rvHeight = recyclerView.height
        val viewPos = recyclerView.getChildRelativePos(viewHolder.itemView)
        val itemHeight = viewHolder.itemView.height
        maxTranslation = rvHeight - viewPos.top
        val progress = dY / maxTranslation

        rvstate.value = rvstate.value?.copy(progress = progress)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        val view = viewHolder?.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            selectedView = view
            view?.animate()?.scaleX(1.2f)?.scaleY(1.2f)?.withEndAction {
                rvstate.value = rvstate.value?.copy(isLongPressed = true)
            }
        } else {
            selectedView?.animate()?.scaleX(1f)?.scaleY(1f)?.withEndAction {
                rvstate.value = rvstate.value?.copy(isLongPressed = false)
            }

            selectedView = null
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

}