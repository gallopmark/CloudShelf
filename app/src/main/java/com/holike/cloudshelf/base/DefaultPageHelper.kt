package com.holike.cloudshelf.base

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.holike.cloudshelf.R

//app通用缺省页处理
internal class DefaultPageHelper {

    companion object {

        fun noResult(defaultPage: View?, drawableTop: Drawable?, textInfo: CharSequence?) {
            renderPage(defaultPage, drawableTop, textInfo, false, null)
        }

        fun onPageError(defaultPage: View?, drawableTop: Drawable?, textInfo: CharSequence?, l: View.OnClickListener) {
            renderPage(defaultPage, drawableTop, textInfo, true, l)
        }

        /**
         * @param defaultPage 缺省布局
         * @param drawableTop 提示图标
         * @param textInfo 提示语
         * @param showRetryButton 是否显示“重试”按钮
         * @param l 点击重试回调监听
         */
        private fun renderPage(defaultPage: View?, drawableTop: Drawable?, textInfo: CharSequence?,
                               showRetryButton: Boolean, l: View.OnClickListener?) {
            defaultPage?.let {
                it.visibility = View.VISIBLE
                val centerTView = it.findViewById<TextView>(R.id.centerTView)
                centerTView.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
                centerTView.text = textInfo
                val refreshTView = it.findViewById<TextView>(R.id.refreshTView)
                if (showRetryButton) {
                    refreshTView.visibility = View.VISIBLE
                    refreshTView.setOnClickListener { v ->
                        it.visibility = View.GONE
                        l?.onClick(v)
                    }
                } else {
                    refreshTView.visibility = View.GONE
                }
            }
        }
    }
}