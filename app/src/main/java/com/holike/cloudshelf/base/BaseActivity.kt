package com.holike.cloudshelf.base

import android.app.Activity
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
import com.holike.cloudshelf.R
import com.holike.cloudshelf.helper.SystemTintHelper
import com.holike.cloudshelf.util.CheckUtils
import com.holike.cloudshelf.widget.CustomToast
import kotlinx.android.synthetic.main.activity_common.*

//app内所有activity的父类
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        @Suppress("unused")
        const val EXTRA_DATA = "extra-data"
    }

    //自定义toast 解决频繁显示问题
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //进场动画
        overridePendingTransition(R.anim.activity_anim_enter, R.anim.activity_anim_silent)
        requestedOrientation = getScreenOrientation()
        setScreenStyle()
        super.onCreate(savedInstanceState)
        createPresenter()
        setContentView(R.layout.activity_common)
        setContentLayout()
        //查找返回键是否存在layout中，设置点击返回上一级
        findViewById<View>(R.id.backtrack)?.setOnClickListener { onBackPressed() }
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

    internal open fun createPresenter() {}

    protected open fun setContentLayout() {
        decorViewContainer.setContentView(getLayoutResourceId())
        decorViewContainer.setBacktrack(getBacktrackResource())
    }

    //子类重写此方法 设置主视图layout
    @LayoutRes
    protected open fun getLayoutResourceId(): Int = 0

    //子类重写此方法 设置返回键风格样式
    @LayoutRes
    protected open fun getBacktrackResource(): Int = 0

    //初始化方法
    protected open fun setup(savedInstanceState: Bundle?) {}

    open fun showLoading() {
        decorViewContainer.showLoadingView()
    }

    open fun showLoading(hide: Boolean) {
        decorViewContainer.showLoadingView(hide)
    }

    open fun hideContentView() {
        decorViewContainer.hideContentView()
    }

    open fun dismissLoading() {
        decorViewContainer.removeLoadingView()
    }

    open fun dismissLoading(show: Boolean) {
        decorViewContainer.removeLoadingView(show)
    }

    open fun showContentView() {
        decorViewContainer.showContentView()
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

    open fun openActivity(intent: Intent) {
        if (CheckUtils.isFastDoubleClick()) return
        startActivity(intent)
    }

    /*启动activity*/
    open fun openActivity(clz: Class<out Activity>) {
        openActivity(clz, null)
    }

    /*启动activity，带bundle参数*/
    open fun openActivity(clz: Class<out Activity>, extras: Bundle?) {
        if (CheckUtils.isFastDoubleClick()) return
        val intent = Intent(this, clz)
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    open fun openActivityForResult(intent: Intent, @IntRange(from = 0, to = 65535) requestCode: Int) {
        if (CheckUtils.isFastDoubleClick()) return
        startActivityForResult(intent, requestCode)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int) {
        openActivityForResult(clz, requestCode, null)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int, extras: Bundle?) {
        if (CheckUtils.isFastDoubleClick()) return
        val intent = Intent(this, clz)
        extras?.let { intent.putExtras(it) }
        startActivityForResult(intent, requestCode)
    }

    //无结果缺省页
    fun onNoResult() {
        onNoResult(getString(R.string.text_no_result))
    }

    /**
     * @param cs 传入提示文字
     */
    fun onNoResult(cs: CharSequence?) {
        onNoResult(R.mipmap.pic_emptypage_nonescreen, cs)
    }

    fun onNoResult(@DrawableRes iconRes: Int, cs: CharSequence?) {
        DefaultPageHelper.noResult(findViewById(R.id.defaultViewContainer),
                getDrawableCompat(iconRes), if (cs.isNullOrEmpty()) getString(R.string.text_no_result) else cs)
    }

    fun onPageError() {
        onPageError(getString(R.string.text_network_error))
    }

    //网络异常、请求失败等 缺省页
    fun onPageError(cs: CharSequence?) {
        DefaultPageHelper.onPageError(findViewById(R.id.defaultViewContainer), getDrawableCompat(R.mipmap.pic_emptypage_nonenetwork),
                if (cs.isNullOrEmpty()) getString(R.string.text_network_error) else cs, View.OnClickListener { onReload() })
    }

    //隐藏缺省页
    fun hideDefaultPage() {
        findViewById<View>(R.id.defaultViewContainer)?.visibility = View.GONE
    }

    //重试回调
    open fun onReload() {

    }

    fun startFragment(fragment: Fragment) {
        startFragment(fragment, null)
    }

    fun startFragment(fragment: Fragment, extras: Bundle?) {
        startFragment(R.id.decorViewContainer, fragment, extras)
    }

    fun startFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        startFragment(containerViewId, fragment, null)
    }

    fun startFragment(@IdRes containerViewId: Int, fragment: Fragment, extras: Bundle?) {
        startFragment(containerViewId, fragment, extras, null)
    }

    fun startFragment(@IdRes containerViewId: Int, fragment: Fragment, extras: Bundle?, tag: String?) {
        fragment.arguments = extras
        supportFragmentManager.beginTransaction().add(containerViewId, fragment, tag).commitAllowingStateLoss()
    }

    override fun finish() {
        super.finish()
        //退场动画
        overridePendingTransition(R.anim.activity_anim_silent, R.anim.activity_anim_exit)
    }
}