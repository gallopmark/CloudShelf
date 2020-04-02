package com.holike.cloudshelf.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.holike.cloudshelf.R
import com.holike.cloudshelf.widget.CustomToast


abstract class BaseFragment : Fragment() {
    protected lateinit var mContext: Context
    lateinit var contentView: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(getLayoutResourceId(), container, false)
        contentView.findViewById<View>(R.id.view_back)?.setOnClickListener { onBackPressed() }
        createPresenter()
        return contentView
    }

    abstract fun getLayoutResourceId(): Int

    internal open fun createPresenter() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(savedInstanceState)
    }

    open fun setup(savedInstanceState: Bundle?) {

    }

    open fun showLoading() {
        (mContext as BaseActivity).showLoading()
    }

    open fun dismissLoading() {
        (mContext as BaseActivity).dismissLoading()
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
        (mContext as BaseActivity).showToast(text, gravity, duration)
    }

    /*获取drawable*/
    open fun getDrawableCompat(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(mContext, id)
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

    fun onBackPressed() {
        (mContext as BaseActivity).onBackPressed()
    }
}