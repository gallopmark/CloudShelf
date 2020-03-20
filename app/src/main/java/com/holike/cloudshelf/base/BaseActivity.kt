package com.holike.cloudshelf.base

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.dialog.LoadingDialog
import pony.xcode.base.CommonActivity

abstract class BaseActivity : CommonActivity() {

    companion object {
        @Suppress("unused")
        const val EXTRA_DATA = "extra-data"
        const val TOAST_GRAVITY = Gravity.BOTTOM or Gravity.END
    }

    //自定义toast 解决频繁显示问题
    private var mToast: Toast? = null

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

    override fun showShortToast(@StringRes resId: Int) {
        showShortToast(resId, TOAST_GRAVITY)
    }

    override fun showShortToast(@StringRes resId: Int, gravity: Int) {
        showShortToast(getString(resId), gravity)
    }

    override fun showShortToast(text: CharSequence?) {
        showShortToast(text, TOAST_GRAVITY)
    }

    override fun showShortToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_SHORT)
    }

    override fun showLongToast(@StringRes resId: Int) {
        showLongToast(resId, TOAST_GRAVITY)
    }

    override fun showLongToast(@StringRes resId: Int, gravity: Int) {
        showLongToast(getString(resId), gravity)
    }

    override fun showLongToast(text: CharSequence?) {
        showLongToast(text, TOAST_GRAVITY)
    }

    override fun showLongToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_LONG)
    }

    override fun showToast(text: CharSequence?, gravity: Int, duration: Int) {
        if (TextUtils.isEmpty(text)) return
        mToast?.let {
            it.cancel()
            mToast = null
        }
        mToast = Toast(this).apply {
            val view = LayoutInflater.from(this@BaseActivity).inflate(R.layout.include_custom_toast, FrameLayout(this@BaseActivity), false)
            view.findViewById<TextView>(R.id.toast_view).text = text
            setView(view)
            setDuration(duration)
            setMargin(0f, 0f)
            setGravity(gravity, 0, 0)
            show()
        }
    }

    //重试回调
    open fun onReload() {

    }

    override fun getLoadingDialog(): Dialog {
        return LoadingDialog(this)
    }

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