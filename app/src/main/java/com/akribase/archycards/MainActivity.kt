package com.akribase.archycards

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.akribase.archycards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var layoutManager: ArcLayoutManager
    private lateinit var binding: ActivityMainBinding
    private var rvState = MutableLiveData(RvState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        rvState.observe(this) {
            binding.frame.imageTintList = ColorStateList.valueOf(
                getColor(
                    if (it.isLongPressed || it.progress > 0.5) R.color.grey else R.color.primary
                )
            )

            binding.tvDragDown.apply {
                setTextColor(getColor(if (!it.isLongPressed) R.color.grey else R.color.primary))
                alpha = if (it.progress > 0.5) 0f else 1f
            }

            binding.tvMain.apply {
                if (it.progress > 0.5) {
                    setTextColor(getColor(R.color.primary))
                    text = getString(
                        if (it.isLongPressed) R.string.release_to_activate_offer else R.string.activating_offer
                    )
                } else {
                    setTextColor(getColor(R.color.white))
                    text = getString(R.string.choose_your_welcome_gift)
                }
            }

            if (it.progress > 0.5 && !it.isLongPressed) binding.ml.transitionToEnd()

            layoutManager.scrollEnabled = !it.isLongPressed
        }


        initRv(binding.rv)
        initArrowAnim()
    }


    private fun initArrowAnim() {
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
        val rvHeight = resources.getDimension(R.dimen.recyclerview_height).toInt()
        val pad = resources.getDimension(R.dimen.item_spacing).toInt()
        val selectBoxPadFraction = 0.5f
        val selectBoxPad = (selectBoxPadFraction * pad).toInt()
        val extraPad = pad - selectBoxPad
        val viewWidth = screenWidth / 2
        val viewHeight = (1.25f * viewWidth).toInt()

        val effViewWidth = viewWidth - 2 * extraPad
        val effViewHeight = viewHeight - 2 * extraPad
        val viewScaleFactor = effViewWidth.toFloat() / (effViewWidth - 2 * selectBoxPad)

        rv.adapter = RewardsAdapter(rewards, rvState, viewWidth, viewHeight)
        rv.layoutManager = ArcLayoutManager(resources, screenWidth, viewWidth, viewHeight).apply {
            layoutManager = this
        }

        (binding.frame.layoutParams as ConstraintLayout.LayoutParams).apply {
            width = effViewWidth
            height = effViewHeight
            topMargin = rvHeight + extraPad
        }

        binding.glowView.layoutParams.apply {
            width = effViewWidth
        }

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rv)

        val snapOnScrollListener = SnapOnScrollListener(snapHelper)
        rv.addOnScrollListener(snapOnScrollListener)
        snapOnScrollListener.snapPosition.observe(this) {
            rvState.apply { value = value?.copy(snapPosition = it) }
        }

        ItemDragDownHelper(
            this,
            rvState,
            0.5f,
            viewScaleFactor
        ).attachToRv(rv)
    }

    override fun onResume() {
        super.onResume()
        binding.rv.smoothScrollBy(1, 0)
    }

}