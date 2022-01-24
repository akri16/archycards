package com.akribase.archycards

import androidx.recyclerview.widget.RecyclerView

data class RvState(
    var snapPosition: Int = RecyclerView.NO_POSITION,
    var isLongPressed: Boolean = false,
    var progress: Float = 0f
)