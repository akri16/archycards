package com.akribase.archycards

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun ViewGroup.getChildRelativePos(child: View): Rect {
    val rect = Rect()
    child.getDrawingRect(rect)
    offsetDescendantRectToMyCoords(child, rect)
    return rect
}