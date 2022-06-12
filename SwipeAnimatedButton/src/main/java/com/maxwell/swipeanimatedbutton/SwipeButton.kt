package com.maxwell.swipeanimatedbutton

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Keep
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.maxwell.swipeanimatedbutton.Animations.animateFadeHide
import com.maxwell.swipeanimatedbutton.Animations.animateFadeShow
import com.maxwell.swipeanimatedbutton.Animations.dpToPx
import com.maxwell.swipeanimatedbutton.Constants.Companion.BTN_INIT_RADIUS
import com.maxwell.swipeanimatedbutton.Constants.Companion.DEFAULT_SWIPE_DISTANCE
import com.maxwell.swipeanimatedbutton.Constants.Companion.DEFAULT_TEXT_SIZE
import com.maxwell.swipeanimatedbutton.Constants.Companion.MORPH_ANIM_DURATION


class SwipeButton : RelativeLayout {

    private lateinit var view: View
    private lateinit var mainGradientDrawable: GradientDrawable
    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var mainContentContainer: RelativeLayout
    private lateinit var contentContainer: RelativeLayout
    private lateinit var contentTv: TextView
    private lateinit var contentInst: TextView
    private lateinit var arrow1: ImageView
    private lateinit var arrow2: ImageView
    private lateinit var arrow3: ImageView
    private lateinit var arrowHintContainer: LinearLayout
    private lateinit var loading: LoadingDotsBounce
    private var btnText: CharSequence = "BUTTON"
    private var btnInstText: CharSequence = "BUTTON INSTRUCTION"

    @ColorInt
    private var textColorInt = 0

    @ColorInt
    private var bgColorInt = 0

    @ColorInt
    private var arrowColorInt = 0
    private var btnRadius: Int = BTN_INIT_RADIUS

    @Dimension
    private var textSize: Int = DEFAULT_TEXT_SIZE

    @Nullable
    var swipeListener: OnSwipeListener? = null
    private var swipeDistance: Float = DEFAULT_SWIPE_DISTANCE

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        attrs?.let { setAttrs(context, it) }
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        attrs?.let { setAttrs(context, it) }
        init(context)
    }

    private fun setAttrs(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.swipeButton,
            0, 0
        )
        try {
            val btnString = a.getString(R.styleable.swipeButton_btn_text)
            if (btnString != null) btnText = btnString
            textColorInt = a.getColor(
                R.styleable.swipeButton_text_color,
                ContextCompat.getColor(context, android.R.color.white)
            )
            bgColorInt = a.getColor(
                R.styleable.swipeButton_bg_color,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            arrowColorInt = a.getColor(
                R.styleable.swipeButton_arrow_color,
                ContextCompat.getColor(context, R.color.swipebtn_translucent_white)
            )
            btnRadius =
                a.getInteger(R.styleable.swipeButton_btn_radius, BTN_INIT_RADIUS)
            textSize =
                a.getDimensionPixelSize(R.styleable.swipeButton_text_size, DEFAULT_TEXT_SIZE)
        } finally {
            a.recycle()
        }
    }


    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.view_swipe_button, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mainContentContainer = view.findViewById(R.id.main_relativeLayout_swipeBtn_contentContainer)
        contentContainer = view.findViewById(R.id.relativeLayout_swipeBtn_contentContainer)
        arrowHintContainer = view.findViewById(R.id.linearLayout_swipeBtn_hintContainer)
        contentTv = view.findViewById(R.id.tv_btnText)
        contentInst = view.findViewById(R.id.tv_btnInstruction)
        arrow1 = view.findViewById(R.id.iv_arrow1)
        arrow2 = view.findViewById(R.id.iv_arrow2)
        arrow3 = view.findViewById(R.id.iv_arrow3)
        loading = view.findViewById(R.id.swipe_loading)
        tintArrowHint()
        contentTv.text = btnText
        contentInst.text = btnInstText
        contentTv.setTextColor(textColorInt)
        contentInst.setTextColor(textColorInt)
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        contentInst.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        gradientDrawable = GradientDrawable()
        mainGradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        mainGradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadius = btnRadius.toFloat()
        mainGradientDrawable.cornerRadius = btnRadius.toFloat()
        setBackgroundColor(bgColorInt)
        updateBackground()
        setupTouchListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        setOnTouchListener(OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> return@OnTouchListener true
                MotionEvent.ACTION_MOVE -> {
                    // Movement logic here
                    if (event.x > arrowHintContainer.width / 2 && event.x + arrowHintContainer.width / 2 < width &&
                        (event.x < arrowHintContainer.x + arrowHintContainer.width || arrowHintContainer.x != 0f)
                    ) {
                        // snaps the hint to user touch, only if the touch is within hint width or if it has already been displaced
                        arrowHintContainer.x = event.x - arrowHintContainer.width / 2
                    }
                    if (arrowHintContainer.x + arrowHintContainer.width > width &&
                        arrowHintContainer.x + arrowHintContainer.width / 2 < width
                    ) {
                        // allows the hint to go up to a max of btn container width
                        arrowHintContainer.x = (width - arrowHintContainer.width).toFloat()
                    }
                    if (event.x < arrowHintContainer.width / 2 &&
                        arrowHintContainer.x > 0
                    ) {
                        // allows the hint to go up to a min of btn container starting
                        arrowHintContainer.x = 0f
                    }
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    //Release logic here
                    when {
                        arrowHintContainer.x + arrowHintContainer.width > width * swipeDistance -> {
                            // swipe completed, fly the hint away!
                            performSuccessfulSwipe()
                        }
                        arrowHintContainer.x <= 0 -> {
                            // upon click without swipe
                            startFwdAnim()
                        }
                        else -> {
                            // swipe not completed, pull back the hint
                            animateHintBack()
                        }
                    }
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun tintArrowHint() {
        arrow1.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY)
        arrow2.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY)
        arrow3.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY)
    }

    private fun startFwdAnim() {
        if (isEnabled) {
            val animation = TranslateAnimation(
                0F,
                measuredWidth.toFloat(), 0F, 0F
            )
            animation.interpolator = AccelerateDecelerateInterpolator()
            animation.duration = 2000
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    animateFadeHide(context, contentTv)
                    animateFadeShow(context, contentInst)
                }

                override fun onAnimationEnd(animation: Animation) {
                    animateFadeShow(context, contentTv)
                    animateFadeHide(context, contentInst)
                    startHintInitAnim()
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // On animation repeat
                }
            })
            arrowHintContainer.startAnimation(animation)
        }
    }

    /**
     * animate entry of hint from the left-most edge
     */
    private fun startHintInitAnim() {
        val anim = TranslateAnimation((-arrowHintContainer.width).toFloat(), 0F, 0F, 0F)
        anim.duration = 500
        arrowHintContainer.startAnimation(anim)
    }


    private fun animateHintBack() {
        val positionAnimator = ValueAnimator.ofFloat(arrowHintContainer.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            arrowHintContainer.x = x
        }
        positionAnimator.duration = 200
        positionAnimator.start()
    }

    private fun updateBackground() {
        contentContainer.background = gradientDrawable
        mainContentContainer.background = mainGradientDrawable
    }


    interface OnSwipeListener {
        fun onSwipeConfirm()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startFwdAnim()
    }

    fun performSuccessfulSwipe() {
        swipeListener?.onSwipeConfirm()
        morphToCircle()
        animateFadeHide(context, arrowHintContainer)
        animateFadeHide(context, contentTv)
    }

    @Keep
    private fun morphToCircle() {

        setOnTouchListener(null)
        val cornerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            gradientDrawable,
            "cornerRadius",
            200f,
            0f
        )
        val widthAnimation: ValueAnimator = ValueAnimator.ofInt(width, dpToPx(50))
        widthAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = contentContainer.layoutParams
            layoutParams.width = `val`
            contentContainer.layoutParams = layoutParams
        }
        val heightAnimation = ValueAnimator.ofInt(height, dpToPx(50))
        heightAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = contentContainer.layoutParams
            layoutParams.height = `val`
            contentContainer.layoutParams = layoutParams
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = MORPH_ANIM_DURATION.toLong()
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation)
        animatorSet.start()
        showProgressBar()
    }

    private fun morphToRect() {
        setupTouchListener()
        val cornerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            gradientDrawable,
            "cornerRadius",
            0f,
            200f
        )
        val widthAnimation: ValueAnimator = ValueAnimator.ofInt(
            dpToPx(50),
            width
        )
        widthAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = contentContainer.layoutParams
            layoutParams.width = `val`
            contentContainer.layoutParams = layoutParams
        }
        val heightAnimation = ValueAnimator.ofInt(
            dpToPx(50),
            width
        )
        heightAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = contentContainer.layoutParams
            layoutParams.height = `val`
            contentContainer.layoutParams = layoutParams
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = MORPH_ANIM_DURATION.toLong()
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation)
        animatorSet.start()

    }

    private fun showProgressBar() {
        loading.visibility = View.VISIBLE
        animateFadeHide(context, contentTv)
    }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.disableButton))

            updateBackground()
            this.alpha = 0.5f
        } else {
            setBackgroundColor(getBackgroundColor())
            this.alpha = 1f
        }
    }

    @ColorInt
    fun getBackgroundColor(): Int {
        return bgColorInt
    }

    fun showResultIcon(isSuccess: Boolean, shouldReset: Boolean) {
        loading.visibility = View.GONE
        val failureIcon = AppCompatImageView(context)
        val icLayoutParams = LayoutParams(dpToPx(50), dpToPx(50))
        failureIcon.layoutParams = icLayoutParams
        failureIcon.visibility = GONE
        val icon: Int = if (isSuccess) R.drawable.ic_tick_icon else R.drawable.ic_remove_icon
        failureIcon.setImageResource(icon)
        contentContainer.addView(failureIcon)
        animateFadeShow(context, failureIcon)
        if (shouldReset) {
            // expand the btn again
            animateFadeHide(context, failureIcon)
            morphToRect()
            arrowHintContainer.x = 0f
            animateFadeShow(context, arrowHintContainer)
            animateFadeShow(context, contentTv)
        }
    }


    fun setText(text: CharSequence?) {
        if (text != null) {
            btnText = text
        }
        contentTv.text = text
    }

    fun setInstText(text: CharSequence?) {
        if (text != null) {
            btnInstText = text
        }
        contentInst.text = text
    }

    override fun setBackgroundColor(@ColorInt bgColor: Int) {
        bgColorInt = bgColor
        gradientDrawable.setColor(bgColor)
        mainGradientDrawable.setColor(bgColor)
        updateBackground()
    }

    fun setTextSize(@Dimension textSize: Int) {
        this.textSize = textSize
        contentInst.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setOnSwipeListener(customSwipeListener: OnSwipeListener?) {
        swipeListener = customSwipeListener
    }

}