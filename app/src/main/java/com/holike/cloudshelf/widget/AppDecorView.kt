package com.holike.cloudshelf.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.holike.cloudshelf.R


class AppDecorView : FrameLayout {

    //layout content
    private var mContentView: View? = null

    //返回键视图
    private var mBacktrackView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleRes: Int) : super(context, attrs, defStyleRes)

    //设置contentView
    fun setContentView(@LayoutRes layoutResID: Int) {
        if (layoutResID != 0) {
            setContentView(LayoutInflater.from(context).inflate(layoutResID, this, false))
        }
    }

    fun setContentView(view: View?) {
        view?.let {
            it.isClickable = true
            this.mContentView = it
            addView(mContentView)
        }
    }

    //显示loadingView
    fun showLoadingView() {
        showLoadingView(false)
    }

    /**
     * 显示loadingView
     * @param hide 是否隐藏contentView
     */
    fun showLoadingView(hide: Boolean) {
        if (hide) {
            hideContentView()
        }
        val loadingView = findViewById<View>(R.id.loadingContainer)
        if (loadingView == null) {
            LayoutInflater.from(context).inflate(R.layout.include_loading, this, true)
        } else {
            if (loadingView.visibility != View.VISIBLE) {
                loadingView.visibility = View.VISIBLE
            }
        }
    }

    //隐藏contentView
    fun hideContentView() {
        mContentView?.apply {
            if (visibility != View.GONE) {
                visibility = View.GONE
            }
        }
    }

    //移除loadingView
    fun removeLoadingView() {
        removeLoadingView(false)
    }

    /**
     * 移除loadingView
     * @param show 是否显示contentView
     */
    fun removeLoadingView(show: Boolean) {
        if (show) {
            showContentView()
        }
        findViewById<View>(R.id.loadingContainer)?.let { removeView(it) }
    }

    //显示contentView
    fun showContentView() {
        mContentView?.apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }
        }
    }

    //设置返回键
    fun setBacktrack(@LayoutRes layoutResID: Int) {
        if (layoutResID != 0) {
            setBacktrack(LayoutInflater.from(context).inflate(layoutResID, this, false))
        }
    }

    fun setBacktrack(backtrackView: View?) {
        if (backtrackView != null) {
            this.mBacktrackView = backtrackView
            addView(mBacktrackView)
        }
    }

    fun removeBacktrack() {
        mBacktrackView?.let { removeView(it) }
    }
}