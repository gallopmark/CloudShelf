package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.BleachedHouseInfoBean
import com.holike.cloudshelf.mvp.presenter.fragment.BleachedHouseInfoPresenter
import com.holike.cloudshelf.mvp.view.fragment.BleachedHouseInfoView
import kotlinx.android.synthetic.main.fragment_bleachedhouse_info.*
import kotlinx.android.synthetic.main.include_bottom_images_layout.*
import kotlinx.android.synthetic.main.include_qrcode_layout.*

//晒晒我家各个方案详情
class BleachedHouseInfoFragment : HollyFragment<BleachedHouseInfoPresenter, BleachedHouseInfoView>(), BleachedHouseInfoView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_bleachedhouse_info

    override fun setup(savedInstanceState: Bundle?) {
        mPresenter.resizeContent(pictureContainer, infoLayout)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
        mPresenter.getBleachedHouseInfo(arguments?.getString("blueprintId"))
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

    override fun onSuccess(bean: BleachedHouseInfoBean) {
        Glide.with(this).load(bean.miniQrUrl).apply(RequestOptions().error(R.mipmap.ic_wxacode)).into(miniQrUrlIView)
        bottomLayout.visibility = View.VISIBLE
        titleTView.text = bean.title
        if (bean.areas.isNullOrEmpty() && bean.houseType.isNullOrEmpty() && bean.budget.isNullOrEmpty() && bean.address.isNullOrEmpty()) {
            flexBoxLayout.visibility = View.GONE
        } else {
            val area = bean.areas
            if (!area.isNullOrEmpty()) {
                areaTView.text = String.format(getString(R.string.text_tips_area), area)
                areaTView.visibility = View.VISIBLE
            }
            val houseType = bean.houseType
            if (!houseType.isNullOrEmpty()) {
                houseTypeTView.text = String.format(getString(R.string.text_tips_houseType), houseType)
                houseTypeTView.visibility = View.VISIBLE
            }
            val budget = bean.budget
            if (!budget.isNullOrEmpty()) {
                budgetTView.text = String.format(getString(R.string.text_tips_budget), budget)
                budgetTView.visibility = View.VISIBLE
            }
            val address = bean.address
            if (!address.isNullOrEmpty()) {
                addressTView.text = address
                addressTView.visibility = View.VISIBLE
            }
        }
        if (!bean.deliver.isNullOrEmpty()) {
            deliverTView.visibility = View.VISIBLE
            deliverTView.text = bean.deliver
        } else {
            deliverTView.visibility = View.GONE
        }
    }

    override fun onFailure(failReason: String?) {
        showShortToast(failReason)
    }
}