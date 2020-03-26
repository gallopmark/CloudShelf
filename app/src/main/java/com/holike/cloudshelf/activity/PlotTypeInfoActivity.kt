package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.PlotTypeInfoBean
import com.holike.cloudshelf.mvp.presenter.PlotTypeInfoPresenter
import com.holike.cloudshelf.mvp.view.PlotTypeInfoView
import kotlinx.android.synthetic.main.activity_plottype_info.*
import kotlinx.android.synthetic.main.include_bottom_images_layout.*
import kotlinx.android.synthetic.main.include_miniqr_layout.*

//搜搜我家户型详情查询
class PlotTypeInfoActivity : HollyActivity<PlotTypeInfoPresenter, PlotTypeInfoView>(), PlotTypeInfoView {

    companion object {
        fun open(act: BaseActivity, houseTypeId: String?) {
            val intent = Intent(act, PlotTypeInfoActivity::class.java)
            intent.putExtra("houseTypeId", houseTypeId)
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_plottype_info

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        mPresenter.resizeContent(pictureContainer, infoLayout)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
        initData()
    }

    private fun initData() {
        mPresenter.getPlotTypeInfo(intent.getStringExtra("houseTypeId"))
    }

    override fun onPageSelected(position: Int, size: Int) {
        val s = String.format(getString(R.string.text_preview_index), position + 1, size)
        val sp = SpannableString(s)
        val span = AbsoluteSizeSpan(getDimensionPixelSize(R.dimen.sp_16))
        sp.setSpan(span, s.indexOf("第") + 1, s.indexOf("/"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        bottomTView.text = sp
    }

    override fun onBottomImageSelected(position: Int) {
        previewVP.currentItem = position
        bottomRView.smoothScrollToPosition(position)
    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onSuccess(bean: PlotTypeInfoBean) {
        Glide.with(this).load(bean.miniQrUrl).apply(RequestOptions().error(R.mipmap.ic_wxacode)).into(miniQrUrlIView)
        bottomLayout.visibility = View.VISIBLE
        if (TextUtils.isEmpty(bean.videoUrl)) {
            videoTView.visibility = View.VISIBLE
            videoTView.setOnClickListener { VideoPlayerActivity.open(this, bean.videoUrl) }
        } else {
            videoTView.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(bean.vrUrl)) {
            panoramicTView.visibility = View.VISIBLE
            panoramicTView.setOnClickListener { WebViewActivity.open(this, bean.vrUrl) }
        } else {
            panoramicTView.visibility = View.GONE
        }
    }

    override fun onFailure(failReason: String?) {
        showShortToast(failReason)
    }
}