package com.akribase.archycards;

import android.content.Context;
import android.graphics.Canvas
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import java.text.FieldPosition

abstract class SwipeCallback(
    private val context: Context,
    private val snapPosition: LiveData<RvState>
): ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, 0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val rvState = snapPosition.value
        if (rvState?.snapPosition == viewHolder.layoutPosition && rvState.isLongPressed) {
            return ItemTouchHelper.DOWN
        }
        return 0
    }
}