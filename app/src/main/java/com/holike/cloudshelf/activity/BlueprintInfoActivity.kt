package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.BlueprintInfoBean
import com.holike.cloudshelf.mvp.presenter.BlueprintInfoPresenter
import com.holike.cloudshelf.mvp.view.BlueprintInfoView
import kotlinx.android.synthetic.main.activity_blueprint_info.*

//晒晒好家的晒图详情查询
class BlueprintInfoActivity : HollyActivity<BlueprintInfoPresenter, BlueprintInfoView>(), BlueprintInfoView {

    companion object {
        fun open(act: BaseActivity, blueprintId: String?) {
            val intent = Intent(act, BlueprintInfoActivity::class.java)
            intent.putExtra("blueprintId", blueprintId)
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_blueprint_info

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        mPresenter.resizeContent(centerLayout,pictureFL)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
        mPresenter.getBlueprintInfo(intent.getStringExtra("blueprintId"))
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

    override fun onSuccess(bean: BlueprintInfoBean) {
        bottomLayout.visibility = View.VISIBLE
        titleTView.text = bean.title
        var source = ""
        bean.areas?.let { source += "${it}㎡\u3000" }
        bean.houseType?.let { source += "$it\u3000" }
        bean.budget?.let { source += "$it\u3000" }
        bean.address?.let { source += it }
        if (!source.isEmpty()) {
            val text = getString(R.string.text_housing_status) + source
            houseInfoTView.visibility = View.VISIBLE
            houseInfoTView.text = text
        } else {
            houseInfoTView.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(bean.deliver)) {
            deliverTView.visibility = View.VISIBLE
            deliverTView.text = bean.deliver
        } else {
            deliverTView.visibility = View.GONE
        }
    }

    override fun onFailure(failReason: String?) {
        showLoading(failReason)
    }
}