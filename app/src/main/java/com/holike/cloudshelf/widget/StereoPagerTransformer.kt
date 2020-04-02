package com.holike.cloudshelf.widget

import android.animation.TimeInterpolator
import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.pow


//viewPager翻页特效
class StereoPagerTransformer(private val pageWidth: Float) : ViewPager.PageTransformer {

    companion object{
        private const val MAX_ROTATE_Y = 90f
    }

    private val sInterpolator = TimeInterpolator { input ->
        if (input < 0.7) {
            input * 0.7.pow(3.0).toFloat() * MAX_ROTATE_Y
        } else {
            input.toDouble().pow(4.0).toFloat() * MAX_ROTATE_Y
        }
    }

    override fun transformPage(page: View, position: Float) {
        page.pivotY = page.height / 2f
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.pivotX = 0f
                page.rotationY = 90f
            }
            position <= 0 -> { // [-1,0]
                page.pivotX = pageWidth
                page.rotationY = -sInterpolator.getInterpolation(-position)
            }
            position <= 1 -> { // (0,1]
                page.pivotX = 0f
                page.rotationY = sInterpolator.getInterpolation(position)
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.pivotX = 0f
                page.rotationY = 90f
            }
        }
    }
}