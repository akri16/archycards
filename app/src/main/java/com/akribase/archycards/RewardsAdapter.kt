package com.akribase.archycards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RewardsAdapter(private val rewards: List<Int>) : RecyclerView.Adapter<RewardsAdapter.RewardsHolder>() {
    class RewardsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return RewardsHolder(itemView)
    }

    override fun onBindViewHolder(holder: RewardsHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return thingsList.size
    }

}
