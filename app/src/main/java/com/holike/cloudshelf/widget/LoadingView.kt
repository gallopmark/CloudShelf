package com.holike.cloudshelf.widget

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R

//加载view
class LoadingView : View {

    private var mAnimationDrawable: AnimationDrawable

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleRes: Int) : super(context, attrs, defStyleRes) {
        background = ContextCompat.getDrawable(context, R.drawable.anim_list_loading)
        mAnimationDrawable = background as AnimationDrawable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (CurrentApp.getInstance().getMinPixels() / 2f).toInt()
        super.onMeasure(size, size)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    fun startAnimation() {
        mAnimationDrawable.start()
    }

    fun stopAnimation() {
        mAnimationDrawable.stop()
    }

    override fun setVisibility(visibility: Int) {
        val currentVisibility = getVisibility()
        super.setVisibility(visibility)
        if (visibility != currentVisibility) {
            if (visibility == VISIBLE) {
                startAnimation()
            } else if (visibility == GONE || visibility == INVISIBLE) {
                stopAnimation()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}