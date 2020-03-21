package com.holike.cloudshelf.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.dialog.LoadingDialog
import pony.xcode.system.SystemTintHelper

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        @Suppress("unused")
        const val EXTRA_DATA = "extra-data"
        const val TOAST_GRAVITY = Gravity.BOTTOM or Gravity.END
    }
    /*加载loading对话框*/
    private var mLoadingDialog: Dialog? = null
    //自定义toast 解决频繁显示问题
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = getScreenOrientation()
        setScreenStyle()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        createPresenter()
        //查找返回键是否存在layout中，设置点击返回上一级
        findViewById<View>(R.id.view_back)?.setOnClickListener { onBackPressed() }
        setup(savedInstanceState)
    }

    //强制横屏
    open fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    /*setContentView之前调用，可以设置theme等属性*/
    open fun setScreenStyle() {
        if (isFullScreen()) {
            requestFullscreen()
        } else {
            if (isTransparentStatusBar()) {
                setTransparentStatusBar()
            }
        }
    }

    open fun isFullScreen(): Boolean = false

    private fun requestFullscreen() {
        SystemTintHelper.setFullscreen(this)
    }

    //全屏沉浸模式
    open fun isTransparentStatusBar(): Boolean = true

    /*设置全屏沉浸透明状态栏-建议调用addMarginTopEqualStatusBarHeight方法使得布局腾出状态栏的高度*/
    protected fun setTransparentStatusBar() {
        SystemTintHelper.setTransparentStatusBar(this)
    }

    @LayoutRes
    protected abstract fun getLayoutResourceId(): Int

    protected open fun setup(savedInstanceState: Bundle?) {}

    protected open fun createPresenter(){}

    open fun showLoading() {
        dismissLoading()
        if (mLoadingDialog == null) {
            mLoadingDialog = getLoadingDialog()
        }
        mLoadingDialog?.show()
    }

    open fun getLoadingDialog(): Dialog {
        return LoadingDialog(this)
    }

    open fun dismissLoading() {
        mLoadingDialog?.dismiss()
    }

    open fun showShortToast(@StringRes resId: Int) {
        showShortToast(resId, TOAST_GRAVITY)
    }

    open fun showShortToast(@StringRes resId: Int, gravity: Int) {
        showShortToast(getString(resId), gravity)
    }

    open fun showShortToast(text: CharSequence?) {
        showShortToast(text, TOAST_GRAVITY)
    }

    open fun showShortToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_SHORT)
    }

    open fun showLongToast(@StringRes resId: Int) {
        showLongToast(resId, TOAST_GRAVITY)
    }

    open fun showLongToast(@StringRes resId: Int, gravity: Int) {
        showLongToast(getString(resId), gravity)
    }

    open fun showLongToast(text: CharSequence?) {
        showLongToast(text,TOAST_GRAVITY)
    }

    open fun showLongToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_LONG)
    }

    open fun showToast(text: CharSequence?, gravity: Int, duration: Int) {
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

    /*获取drawable*/
    open fun getDrawableCompat(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(this, id)
    }

    /*获取dimens value*/
    open fun getDimension(@DimenRes id: Int): Float {
        return resources.getDimension(id)
    }

    /*将getDimension结果转换为int，并且小数部分四舍五入*/
    open fun getDimensionPixelSize(@DimenRes id: Int): Int {
        return resources.getDimensionPixelSize(id)
    }

    /*直接截断小数位，即取整其实就是把float强制转化为int，注意不是四舍五入*/
    open fun getDimensionPixelOffset(@DimenRes id: Int): Int {
        return resources.getDimensionPixelOffset(id)
    }

    open fun openActivity(intent: Intent?) {
        startActivity(intent)
    }

    /*启动activity*/
    open fun openActivity(clz: Class<out Activity?>) {
        openActivity(clz, null)
    }

    /*启动activity，带bundle参数*/
    open fun openActivity(clz: Class<out Activity?>, extras: Bundle?) {
        val intent = Intent(this, clz)
        if (extras != null) {
            intent.putExtras(extras)
        }
        startActivity(intent)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int) {
        openActivityForResult(clz, requestCode)
    }


    open fun openActivityForResult(clz: Class<out Activity>, extras: Bundle?, @IntRange(from = 0, to = 65535) requestCode: Int) {
        val intent = Intent(this, clz)
        if (extras != null) {
            intent.putExtras(extras)
        }
        startActivityForResult(intent, requestCode)
    }

    open fun getCurrentApp(): CurrentApp {
        return application as CurrentApp
    }

    open fun putExtra(name: String, obj: Any?) {
        getCurrentApp().putExtra(name, obj)
    }

    open fun removeExtra(name: String) {
        getCurrentApp().removeExtra(name)
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


    override fun onDestroy() {
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
        super.onDestroy()
    }
}