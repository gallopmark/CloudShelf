package com.holike.cloudshelf.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.holike.cloudshelf.R


abstract class CommonDialog : Dialog {
    protected var mContext: Context
    protected lateinit var mContentView: View
    private var mWidth = FrameLayout.LayoutParams.WRAP_CONTENT
    private var mHeight = FrameLayout.LayoutParams.WRAP_CONTENT //默认height为自动填充

    private var mWindowAnimations = -1
    private var mBackground: Drawable? = null
    private var mGravity = Gravity.CENTER //默认弹窗位置为屏幕中央

    constructor(context: Context) : this(context, R.style.AppDialogStyle)

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId) {
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val params: ViewGroup.LayoutParams
        val lp = getLayoutParams()
        if (lp != null) {
            params = lp
            setWidth(lp.width)
            setHeight(lp.height)
        } else {
            params = FrameLayout.LayoutParams(getWidth(), getHeight())
        }
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutResourceId(), null, false)
        initView(mContentView)
        setContentView(mContentView, params)
        window?.let {
            //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            renderWindow(it)
        }
    }

    /*子类可以重写此方法自定义宽度*/
    open fun getWidth(): Int = mWidth

    /*窗口宽度默认左右边距30dp*/
    open fun setWidth(width: Int) {
        mWidth = width
    }

    /*子类可以重写此方法自定义高度*/
    open fun getHeight(): Int = mHeight

    open fun setHeight(height: Int) {
        mHeight = height
    }

    /*子类可以重写此方法自定义窗口动画*/
    open fun getWindowAnimations(): Int = mWindowAnimations

    /*对话框弹出动画*/
    open fun setWindowAnimations(@StyleRes windowAnimations: Int) {
        mWindowAnimations = windowAnimations
    }

    open fun setBackgroundDrawable(background: Drawable?) {
        mBackground = background
    }

    /*重写此方法可以设置窗口background*/
    open fun getBackgroundDrawable(): Drawable? = mBackground

    open fun setBackgroundDrawableResource(@DrawableRes resId: Int) {
        mBackground = ContextCompat.getDrawable(mContext, resId)
    }

    open fun getLayoutParams(): MarginLayoutParams? {
        return null
    }

    /*重写此方法可以设置窗口弹出位置*/
    open fun getGravity(): Int {
        return mGravity
    }

    open fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    protected open fun renderWindow(window: Window) {
        if (isFullScreen()) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        } else {
            if (fullWidth()) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, getHeight())
            }
        }
        if (getWindowAnimations() != -1) {
            window.setWindowAnimations(getWindowAnimations())
        }
        getBackgroundDrawable()?.let { window.setBackgroundDrawable(it) }
        if (getGravity() != -1) {
            window.setGravity(getGravity())
        }
    }

    protected abstract fun getLayoutResourceId(): Int

    open fun initView(contentView: View) {}

    /*是否全屏显示*/
    protected open fun isFullScreen(): Boolean = false

    protected open fun fullWidth(): Boolean {
        return false
    }
}