package com.holike.cloudshelf.base

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
            val defaultPage = act.findViewById<View>(R.id.vg_default_page)
            defaultPage?.let {
                it.visibility = View.VISIBLE
                val centerTView = it.findViewById<TextView>(R.id.centerTView)
                centerTView.setCompoundDrawablesWithIntrinsicBounds(null, act.getDrawableCompat(iconRes), null, null)
                centerTView.text = if (text.isNullOrEmpty()) act.getString(R.string.text_no_result) else text
                it.findViewById<TextView>(R.id.refreshTView).visibility = View.GONE
            }
        }

        /**
         * 无网络或者其他错误缺省页
         * @param iconRes 错误提示图标
         * @param failReason 错误原因
         */
        fun noNetwork(act: BaseActivity, @DrawableRes iconRes: Int, failReason: CharSequence?) {
            val defaultPage = act.findViewById<View>(R.id.vg_default_page)
            defaultPage?.let { dePage ->
                dePage.visibility = View.VISIBLE
                val centerTView = dePage.findViewById<TextView>(R.id.centerTView)
                centerTView.setCompoundDrawablesWithIntrinsicBounds(null, act.getDrawableCompat(iconRes),
                        null, null)
                centerTView.text = if (failReason.isNullOrEmpty())
                    act.getString(R.string.text_network_error) else failReason
                val refreshTView = dePage.findViewById<TextView>(R.id.refreshTView)
                refreshTView.visibility = View.VISIBLE
                refreshTView.setOnClickListener {
                    dePage.visibility = View.GONE
                    act.onReload()
                }
            }
        }

        fun hide(act: BaseActivity) {
            act.findViewById<View>(R.id.vg_default_page)?.visibility = View.GONE
        }
    }
}