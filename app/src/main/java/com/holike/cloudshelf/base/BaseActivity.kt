package com.holike.cloudshelf.base

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.dialog.LoadingDialog
import pony.xcode.base.CommonActivity

abstract class BaseActivity : CommonActivity() {

    companion object {
        const val EXTRA_DATA = "extra-data"
    }

    //强制横屏
    override fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    //全拼沉浸状态栏
    override fun isTransparentStatusBar(): Boolean = true

    override fun setup(savedInstanceState: Bundle?) {
        //查找返回键是否存在layout中，设置点击返回上一级
        findViewById<View>(R.id.view_back)?.setOnClickListener { onBackPressed() }
    }

    //无结果缺省页
    fun onNoResult() {
        onNoResult(getString(R.string.text_no_result))
    }

    /**
     * @param text 传入提示文字
     */
    fun onNoResult(text: CharSequence?) {
        onNoResult(R.mipmap.pic_emptypage_nonescreen, text)
    }

    fun onNoResult(@DrawableRes iconRes: Int, text: CharSequence?) {
        val defaultPage = findViewById<View>(R.id.vg_default_page)
        defaultPage?.let {
            it.visibility = View.VISIBLE
            val centerTView = it.findViewById<TextView>(R.id.centerTView)
            centerTView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawableCompat(iconRes), null, null)
            centerTView.text = if (text.isNullOrEmpty()) getString(R.string.text_no_result) else text
            it.findViewById<TextView>(R.id.refreshTView).visibility = View.GONE
        }
    }

    fun onNetworkError() {
        onNetworkError(getString(R.string.text_network_error))
    }

    //网络异常、请求失败等 缺省页
    fun onNetworkError(failReason: CharSequence?) {
        val defaultPage = findViewById<View>(R.id.vg_default_page)
        defaultPage?.let { dePage ->
            dePage.visibility = View.VISIBLE
            val centerTView = dePage.findViewById<TextView>(R.id.centerTView)
            centerTView.text = if (failReason.isNullOrEmpty()) getString(R.string.text_network_error) else failReason
            val refreshTView = dePage.findViewById<TextView>(R.id.refreshTView)
            refreshTView.visibility = View.VISIBLE
            refreshTView.setOnClickListener {
                dePage.visibility = View.GONE
                onReload()
            }
        }
    }

    fun hideDefaultPage() {
        findViewById<View>(R.id.vg_default_page)?.visibility = View.GONE
    }

    //重试回调
    open fun onReload() {

    }

    override fun getLoadingDialog(): Dialog {
        return LoadingDialog(this)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getCurrentApp(): CurrentApp {
        return application as CurrentApp
    }

    fun putExtra(name: String, obj: Any?) {
        getCurrentApp().putExtra(name, obj)
    }

    fun removeExtra(name: String) {
        getCurrentApp().removeExtra(name)
    }

}