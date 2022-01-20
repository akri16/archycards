package com.akribase.archycards

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.akribase.archycards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var rvState = MutableLiveData(RvState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initRv(binding.rv)
    }

    private fun initRv(rv: RecyclerView) {
        val rewards = listOf(R.drawable.reward1, R.drawable.reward2, R.drawable.reward3)
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val viewWidth = screenWidth / 2
        val viewHeight = (1.3f * viewWidth).toInt()

        rv.adapter = RewardsAdapter(rewards, rvState)
        rv.layoutManager = ArcLayoutManager(resources, screenWidth, viewWidth, viewHeight)

        binding.imageView2.layoutParams =
            (binding.imageView2.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = viewWidth
                height = viewHeight
            }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rv)

        val snapOnScrollListener = SnapOnScrollListener(snapHelper)
        rv.addOnScrollListener(snapOnScrollListener)
        snapOnScrollListener.snapPosition.observe(this) {
            rvState.apply { value = value?.copy(snapPosition = it) }
        }

        val swipeHandler = object : SwipeCallback(this, rvState) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rv)
    }
}