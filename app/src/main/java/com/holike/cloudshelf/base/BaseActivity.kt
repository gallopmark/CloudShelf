package com.holike.cloudshelf.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.dialog.LoadingDialog
import com.holike.cloudshelf.widget.CustomToast
import pony.xcode.system.SystemTintHelper

//app内所有activity的父类
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        @Suppress("unused")
        const val EXTRA_DATA = "extra-data"
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

    internal open fun createPresenter() {}

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
        showShortToast(resId, CustomToast.DEFAULT_GRAVITY)
    }

    open fun showShortToast(@StringRes resId: Int, gravity: Int) {
        showShortToast(getString(resId), gravity)
    }

    open fun showShortToast(text: CharSequence?) {
        showShortToast(text, CustomToast.DEFAULT_GRAVITY)
    }

    open fun showShortToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_SHORT)
    }

    open fun showLongToast(@StringRes resId: Int) {
        showLongToast(resId, CustomToast.DEFAULT_GRAVITY)
    }

    open fun showLongToast(@StringRes resId: Int, gravity: Int) {
        showLongToast(getString(resId), gravity)
    }

    open fun showLongToast(text: CharSequence?) {
        showLongToast(text, CustomToast.DEFAULT_GRAVITY)
    }

    open fun showLongToast(text: CharSequence?, gravity: Int) {
        showToast(text, gravity, Toast.LENGTH_LONG)
    }

    open fun showToast(text: CharSequence?, gravity: Int, duration: Int) {
        if (TextUtils.isEmpty(text)) return
        //先取消在实例化，避免频繁显示
        mToast?.let {
            it.cancel()
            mToast = null
        }
        mToast = CustomToast.obtain(this, text, duration, gravity).apply { show() }
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
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int) {
        openActivityForResult(clz, requestCode, null)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int, extras: Bundle?) {
        val intent = Intent(this, clz)
        extras?.let { intent.putExtras(it) }
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
        DefaultPageHelper.noResult(this, iconRes, text)
    }

    fun onNetworkError() {
        onNetworkError(getString(R.string.text_network_error))
    }

    //网络异常、请求失败等 缺省页
    fun onNetworkError(failReason: CharSequence?) {
        DefaultPageHelper.noNetwork(this, R.mipmap.pic_emptypage_nonenetwork, failReason)
    }

    //隐藏缺省页
    fun hideDefaultPage() {
        DefaultPageHelper.hide(this)
    }

    //重试回调
    open fun onReload() {

    }

    fun addFragment(fragment: Fragment) {
        addFragment(fragment, null)
    }

    fun addFragment(fragment: Fragment, extras: Bundle?) {
        addFragment(R.id.fl_fragment, fragment, extras)
    }

    fun addFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        addFragment(containerViewId, fragment, null)
    }

    fun addFragment(@IdRes containerViewId: Int, fragment: Fragment, extras: Bundle?) {
        fragment.arguments = extras
        supportFragmentManager.beginTransaction().add(containerViewId, fragment).commitAllowingStateLoss()
    }

    override fun onDestroy() {
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
        super.onDestroy()
    }
}