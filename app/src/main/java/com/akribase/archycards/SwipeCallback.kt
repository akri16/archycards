package com.akribase.archycards;

import android.content.Context;
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import java.text.FieldPosition

abstract class SwipeCallback(
    private val context: Context,
    private val snapPosition: MutableLiveData<RvState>
): ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, 0) {

    var selectedView: View? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val rvState = snapPosition.value
        if (rvState?.snapPosition == viewHolder.layoutPosition) {
            return ItemTouchHelper.DOWN
        }
        return 0
    }

    override fun isLongPressDragEnabled() = true

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        val view = viewHolder?.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            selectedView = view
            view?.animate()?.scaleX(1.2f)?.scaleY(1.2f)?.withEndAction {
                snapPosition.value = snapPosition.value?.copy(isLongPressed = true)
            }
        } else {
            selectedView?.animate()?.scaleX(1f)?.scaleY(1f)?.withEndAction {
                snapPosition.value = snapPosition.value?.copy(isLongPressed = false)
            }
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

}