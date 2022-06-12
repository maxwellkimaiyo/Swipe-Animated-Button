package com.maxwell.swipeanimatedbutton

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

class LoadingDotsBounce : LinearLayout {

    private lateinit var img: Array<ImageView?>
    private val circle = GradientDrawable()
    private lateinit var animator: Array<ObjectAnimator?>

    constructor(context: Context?) : super(context) {
        context?.let { initView(it) }
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setLayoutParams(layoutParams)

        context?.let { initView(it) }
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.let { initView(it) }
    }

    private fun initView(context: Context) {
        var color = Color.GRAY
        val background = background
        if (background is ColorDrawable) {
            color = background.color
        }
        setBackgroundColor(Color.TRANSPARENT)
        removeAllViews()
        img = arrayOfNulls(OBJECT_SIZE)
        circle.shape = GradientDrawable.OVAL
        circle.setColor(color)
        circle.setSize(200, 200)
        val layoutParams2 = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams2.weight = 1f
        val rel = arrayOfNulls<LinearLayout>(OBJECT_SIZE)
        for (i in 0 until OBJECT_SIZE) {
            rel[i] = LinearLayout(context)
            rel[i]?.gravity = Gravity.CENTER
            rel[i]?.layoutParams = layoutParams2
            img[i] = ImageView(context)
            img[i]?.background = circle
            rel[i]?.addView(img[i])
            addView(rel[i])
        }
    }

    private var onLayoutReach = false
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!onLayoutReach) {
            onLayoutReach = true
            val lp = LayoutParams(width / 5, width / 5)
            for (i in 0 until OBJECT_SIZE) {
                img[i]?.layoutParams = lp
            }
            animateView()
        }
    }

    @SuppressLint("Recycle")
    private fun animateView() {
        animator = arrayOfNulls(OBJECT_SIZE)
        for (i in 0 until OBJECT_SIZE) {
            img[i]?.translationY = (height / POST_DIV).toFloat()
            val y = PropertyValuesHolder.ofFloat(TRANSLATION_Y, (-height / POST_DIV).toFloat())
            val x = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0f)
            animator[i] = ObjectAnimator.ofPropertyValuesHolder(img[i], x, y)
            animator[i]?.repeatCount = -1
            animator[i]?.repeatMode = ValueAnimator.REVERSE
            animator[i]?.duration = DURATION.toLong()
            animator[i]?.startDelay = (DURATION / 3 * i).toLong()
            animator[i]?.start()
        }
    }

    companion object {
        private const val OBJECT_SIZE = 3
        private const val POST_DIV = 6
        private const val DURATION = 500
    }
}
