package com.holike.cloudshelf.base

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.holike.cloudshelf.R

//app通用缺省页处理
internal class DefaultPageHelper {

    companion object {
        /**
         * 展示无结果缺省页
         * @param iconRes 缺省图标
         * @param text 缺省提示语
         */
        fun noResult(act: BaseActivity, @DrawableRes iconRes: Int, text: CharSequence?) {
            val textInfo = if (text.isNullOrEmpty()) act.getString(R.string.text_no_result) else text
            noResult(act.findViewById(R.id.vg_default_page), act.getDrawableCompat(iconRes), textInfo)
        }

        /**
         * 展示无结果缺省页
         * @param iconRes 缺省图标
         * @param text 缺省提示语
         */
        fun noResult(fragment: BaseFragment, @DrawableRes iconRes: Int, text: CharSequence?) {
            val textInfo = if (text.isNullOrEmpty()) fragment.context?.getString(R.string.text_no_result) else text
            noResult(fragment.contentView.findViewById(R.id.vg_default_page), fragment.getDrawableCompat(iconRes), textInfo)
        }

        private fun noResult(defaultPage: View?, drawableTop: Drawable?, textInfo: CharSequence?) {
            defaultPage?.let {
                it.visibility = View.VISIBLE
                val centerTView = it.findViewById<TextView>(R.id.centerTView)
                centerTView.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
                centerTView.text = textInfo
                it.findViewById<TextView>(R.id.refreshTView).visibility = View.GONE
            }
        }

        /**
         * 无网络或者其他错误缺省页
         * @param iconRes 错误提示图标
         * @param failReason 错误原因
         */
        fun noNetwork(act: BaseActivity, @DrawableRes iconRes: Int, failReason: CharSequence?) {
            noNetwork(act.findViewById(R.id.vg_default_page), act.getDrawableCompat(iconRes)
                    , if (failReason.isNullOrEmpty()) act.getString(R.string.text_network_error) else failReason,
                    View.OnClickListener { act.onReload() })
        }

        /**
         * 无网络或者其他错误缺省页
         * @param iconRes 错误提示图标
         * @param failReason 错误原因
         */
        fun noNetwork(fragment: BaseFragment, @DrawableRes iconRes: Int, failReason: CharSequence?) {
            noNetwork(fragment.contentView.findViewById(R.id.vg_default_page), fragment.getDrawableCompat(iconRes)
                    , if (failReason.isNullOrEmpty()) fragment.context?.getString(R.string.text_network_error) else failReason,
                    View.OnClickListener { fragment.onReload() })
        }

        private fun noNetwork(defaultPage: View?, drawableTop: Drawable?, textInfo: CharSequence?, l: View.OnClickListener) {
            defaultPage?.let {
                it.visibility = View.VISIBLE
                val centerTView = it.findViewById<TextView>(R.id.centerTView)
                centerTView.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
                centerTView.text = textInfo
                val refreshTView = it.findViewById<TextView>(R.id.refreshTView)
                refreshTView.visibility = View.VISIBLE
                refreshTView.setOnClickListener { v ->
                    it.visibility = View.GONE
                    l.onClick(v)
                }
            }
        }

        fun hide(act: BaseActivity) {
            act.findViewById<View>(R.id.vg_default_page)?.visibility = View.GONE
        }

        fun hide(fragment: BaseFragment) {
            fragment.contentView.findViewById<View>(R.id.vg_default_page)?.visibility = View.GONE
        }
    }
}