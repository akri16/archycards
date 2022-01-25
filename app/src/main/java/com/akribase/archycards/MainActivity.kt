package com.akribase.archycards

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.BounceInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.akribase.archycards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var layoutManager: ArcLayoutManager
    private lateinit var binding: ActivityMainBinding
    private var rvState = MutableLiveData(RvState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        rvState.observe(this) {
            binding.frame.imageTintList = ColorStateList.valueOf(getColor(
                if (!it.isLongPressed) R.color.primary else R.color.grey
            ))
            layoutManager.scrollEnabled = !it.isLongPressed
        }

        initRv(binding.rv)
        initAnim()
    }

    private fun initAnim() {
        val arrowView = binding.doubleArrow
        ObjectAnimator.ofFloat(arrowView, "translationY", 0f, 50f).apply {
            interpolator = BounceInterpolator()
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            duration = 2000
            start()
        }
    }

    private fun initRv(rv: RecyclerView) {
        val rewards = listOf(R.drawable.reward1, R.drawable.reward2, R.drawable.reward3)
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val viewWidth = screenWidth/2
        val viewHeight = (1.25f * viewWidth).toInt()

        rv.adapter = RewardsAdapter(rewards, rvState, viewWidth, viewHeight)
        rv.layoutManager = ArcLayoutManager(resources, screenWidth, viewWidth, viewHeight).apply {
            layoutManager = this
        }

        val rvHeight = resources.getDimension(R.dimen.recyclerview_height).toInt()
        val pad = resources.getDimension(R.dimen.item_spacing).toInt()
        val selectBoxPadFraction = 0.5f
        val extraPad = ((1 - selectBoxPadFraction) * pad).toInt()

        binding.frame.layoutParams =
            (binding.frame.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = viewWidth - 2 * extraPad
                height = viewHeight - 2 * extraPad
                topMargin = rvHeight + extraPad
            }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rv)

        val snapOnScrollListener = SnapOnScrollListener(snapHelper)
        rv.addOnScrollListener(snapOnScrollListener)
        snapOnScrollListener.snapPosition.observe(this) {
            rvState.apply { value = value?.copy(snapPosition = it) }
        }
        ItemDragDownHelper(this, rvState).attachToRv(rv)
    }
}