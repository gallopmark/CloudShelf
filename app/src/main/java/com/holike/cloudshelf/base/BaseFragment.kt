package com.holike.cloudshelf.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.holike.cloudshelf.R
import com.holike.cloudshelf.util.CheckUtils
import com.holike.cloudshelf.widget.CustomToast
import kotlinx.android.synthetic.main.fragment_common.*

//app内所有fragment的父类
abstract class BaseFragment : Fragment() {
    protected lateinit var mContext: Context
    lateinit var contentView: View

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        createPresenter()
    }

    internal open fun createPresenter() {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.fragment_common, container, false)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContentLayout()
        contentView.findViewById<View>(R.id.backtrack)?.setOnClickListener { onBackPressed() }
        setup(savedInstanceState)
    }

    protected open fun setContentLayout() {
        decorViewContainer.setContentView(getLayoutResourceId())
        decorViewContainer.setBacktrack(getBacktrackResource())
    }

    //主视图layout id
    @LayoutRes
    abstract fun getLayoutResourceId(): Int

    //返回键layout id
    @LayoutRes
    protected open fun getBacktrackResource(): Int = 0

    //kotlin-android-extensions id引用必须在onViewCreated之后使用，否则报空指针异常
    open fun setup(savedInstanceState: Bundle?) {

    }

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

    //移除返回键
    open fun removeBacktrack(){
        decorViewContainer.removeBacktrack()
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

    /*获取string*/
    open fun getStringRes(@StringRes resId: Int): String {
        return mContext.getString(resId)
    }

    /*获取drawable*/
    open fun getDrawableRes(@DrawableRes id: Int): Drawable? {
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
        DefaultPageHelper.noResult(contentView.findViewById(R.id.defaultViewContainer),
                getDrawableRes(iconRes),
                if (text.isNullOrEmpty()) getStringRes(R.string.text_no_result) else text)
    }

    fun onNetworkError() {
        onNetworkError(getString(R.string.text_network_error))
    }

    //网络异常、请求失败等 缺省页
    fun onNetworkError(cs: CharSequence?) {
        DefaultPageHelper.onPageError(contentView.findViewById(R.id.defaultViewContainer),
                getDrawableRes(R.mipmap.pic_emptypage_nonenetwork),
                if (cs.isNullOrEmpty()) getStringRes(R.string.text_network_error) else cs,
                View.OnClickListener { onReload() })
    }

    //隐藏缺省页
    fun hideDefaultPage() {
        contentView.findViewById<View>(R.id.defaultViewContainer)?.visibility = View.GONE
    }

    //重试回调
    open fun onReload() {

    }

    open fun openActivity(intent: Intent) {
        if (CheckUtils.isFastDoubleClick() || activity == null) return
        startActivity(intent)
    }

    /*启动activity*/
    open fun openActivity(clz: Class<out Activity>) {
        openActivity(clz, null)
    }

    /*启动activity，带bundle参数*/
    open fun openActivity(clz: Class<out Activity>, extras: Bundle?) {
        if (CheckUtils.isFastDoubleClick() || activity == null) return
        val intent = Intent(mContext, clz)
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    open fun openActivityForResult(intent: Intent, @IntRange(from = 0, to = 65535) requestCode: Int) {
        if (CheckUtils.isFastDoubleClick() || activity == null) return
        startActivityForResult(intent, requestCode)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int) {
        openActivityForResult(clz, requestCode, null)
    }

    open fun openActivityForResult(clz: Class<out Activity>, @IntRange(from = 0, to = 65535) requestCode: Int, extras: Bundle?) {
        if (CheckUtils.isFastDoubleClick() || activity == null) return
        val intent = Intent(mContext, clz)
        extras?.let { intent.putExtras(it) }
        startActivityForResult(intent, requestCode)
    }

    fun onBackPressed() {
        (mContext as BaseActivity).onBackPressed()
    }
}