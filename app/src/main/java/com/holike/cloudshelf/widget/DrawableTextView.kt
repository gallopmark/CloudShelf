package com.holike.cloudshelf.widget

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.ceil

/*解决drawable不与文字居中的问题*/
class DrawableTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val drawables = compoundDrawables
        if (drawables[0] != null || drawables[2] != null) {
            // 左、右
            gravity = Gravity.CENTER_VERTICAL or if (drawables[0] != null) Gravity.START else Gravity.END
        } else if (drawables[1] != null || drawables[3] != null) {
            // 上、下
            gravity = Gravity.CENTER_HORIZONTAL or if (drawables[1] == null) Gravity.BOTTOM else Gravity.TOP
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        val drawablePadding = compoundDrawablePadding
        val drawables = compoundDrawables
        when {
            drawables[0] != null -> { // 左
                val drawableWidth = drawables[0].intrinsicWidth
                val bodyWidth: Float
                bodyWidth = if (TextUtils.isEmpty(text)) {
                    drawableWidth.toFloat()
                } else {
                    val textWidth = paint.measureText(text.toString())
                    textWidth + drawableWidth + drawablePadding + paddingLeft + paddingRight
                }
                canvas.translate((width - bodyWidth) / 2, 0f)
            }
            drawables[2] != null -> { // 右
                val drawableWidth = drawables[2].intrinsicWidth
                val bodyWidth: Float
                bodyWidth = if (TextUtils.isEmpty(text)) {
                    drawableWidth.toFloat()
                } else {
                    val textWidth = paint.measureText(text.toString())
                    textWidth + drawableWidth + drawablePadding + paddingLeft + paddingRight
                }
                canvas.translate((bodyWidth - width) / 2, 0f)
            }
            drawables[1] != null -> {   // 上
                val drawableHeight = drawables[1].intrinsicHeight
                val bodyHeight: Float
                bodyHeight = if (TextUtils.isEmpty(text)) {
                    drawableHeight.toFloat()
                } else {
                    val fm = paint.fontMetrics
                    val fontHeight = ceil((fm.descent - fm.ascent).toDouble()).toFloat()
                    fontHeight + drawableHeight + drawablePadding + paddingLeft + paddingRight
                }
                canvas.translate(0f, (height - bodyHeight) / 2)
            }
            drawables[3] != null -> { // 下
                val drawableHeight = drawables[3].intrinsicHeight
                val bodyHeight: Float
                bodyHeight = if (TextUtils.isEmpty(text)) {
                    drawableHeight.toFloat()
                } else {
                    val fm = paint.fontMetrics
                    val fontHeight = ceil((fm.descent - fm.ascent).toDouble()).toFloat()
                    fontHeight + drawableHeight + drawablePadding + paddingLeft + paddingRight
                }
                canvas.translate(0f, (bodyHeight - height) / 2)
            }
        }
        super.onDraw(canvas)
    }
}