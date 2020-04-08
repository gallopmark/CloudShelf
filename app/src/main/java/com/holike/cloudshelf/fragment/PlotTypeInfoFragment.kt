package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.VideoPlayerActivity
import com.holike.cloudshelf.activity.WebViewActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.PlotTypeInfoBean
import com.holike.cloudshelf.mvp.presenter.PlotTypeInfoPresenter
import com.holike.cloudshelf.mvp.view.PlotTypeInfoView
import kotlinx.android.synthetic.main.fragment_plottype_info.*
import kotlinx.android.synthetic.main.include_bottom_images_layout.*
import kotlinx.android.synthetic.main.include_qrcode_layout.*


//搜搜我家户型详情查询
class PlotTypeInfoFragment : HollyFragment<PlotTypeInfoPresenter, PlotTypeInfoView>(), PlotTypeInfoView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_plottype_info

    override fun setup(savedInstanceState: Bundle?) {
        mPresenter.resizeContent(pictureContainer, infoLayout)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
        initData()
    }

    private fun initData() {
        mPresenter.getPlotTypeInfo(arguments?.getString("houseTypeId"))
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
        if (!bean.videoUrl.isNullOrEmpty()) {
            videoTView.visibility = View.VISIBLE
            videoTView.setOnClickListener { VideoPlayerActivity.open(mContext as BaseActivity, bean.videoUrl) }
        } else {
            videoTView.visibility = View.GONE
        }
        if (!bean.vrUrl.isNullOrEmpty()) {
            panoramicTView.visibility = View.VISIBLE
            panoramicTView.setOnClickListener { WebViewActivity.open(mContext as BaseActivity, bean.vrUrl) }
        } else {
            panoramicTView.visibility = View.GONE
        }
    }

    override fun onFailure(failReason: String?) {
        showShortToast(failReason)
    }
}