package com.akribase.archycards

import android.annotation.SuppressLint
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.akribase.archycards.databinding.ItemViewBinding

class RewardsAdapter(
    private val rewards: List<Int>,
    val rvState: MutableLiveData<RvState>,
    val itemWidth: Int,
    val itemHeight: Int
): RecyclerView.Adapter<RewardsAdapter.RewardsHolder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class RewardsHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.imageView.layoutParams = binding.imageView.layoutParams.apply {
                width = itemWidth
                height = itemHeight
            }

            binding.root.layoutParams = binding.root.layoutParams.apply {
                width = itemWidth
                height = 1000
            }
        }

        fun bind(id: Int) {
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(itemView.context, id))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsHolder {
        return RewardsHolder(
            ItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RewardsHolder, position: Int) {
        holder.bind(rewards[position])
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}
