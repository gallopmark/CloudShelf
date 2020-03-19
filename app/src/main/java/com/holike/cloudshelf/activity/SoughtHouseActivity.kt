package com.holike.cloudshelf.activity

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.mvp.presenter.SoughtHousePresenter
import com.holike.cloudshelf.mvp.view.SoughtHouseView
import kotlinx.android.synthetic.main.activity_sought_house.*
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//搜搜我家
class SoughtHouseActivity : HollyActivity<SoughtHousePresenter, SoughtHouseView>(), SoughtHouseView {

    override fun getLayoutResourceId(): Int = R.layout.activity_sought_house

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_searchhome, 0, 0, 0)
        typeTView.text = getString(R.string.text_search_my_home)
        startAnim()
        mPresenter.initRecyclerView(centerRView)
        refreshLayout.setOnLoadMoreListener { mPresenter.onLoadMore() }
        mPresenter.initData()
    }

    //view过度动画
    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
    }

    //定位开始  此方法里设置定位中...
    override fun onLocationStart() {

    }

    //定位成功
    override fun onLocationSuccess(currentCity: String?) {
        currentCityTView.text = String.format(getString(R.string.text_current_position),
                if (currentCity.isNullOrEmpty()) "" else currentCity)
    }

    //定位失败
    override fun onLocationFailure(failReason: String?) {

    }

    //定位结束
    override fun onLocationEnd() {

    }

    override fun onSearchSuccess(bean: SoughtHouseBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.pageTotal))
        hideDefaultPage()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    override fun onNoResults() {
        refreshLayout.visibility = View.GONE
        onNoResult()
    }

    override fun onSearchFailure(failReason: String?, isShowError: Boolean) {
        refreshLayout.finishLoadMore()
        if (isShowError) {
            countTView.text = String.format(getString(R.string.text_program_count, "0"))
            refreshLayout.visibility = View.GONE
            onNetworkError(failReason)
        } else {
            if (refreshLayout.visibility != View.VISIBLE) {
                refreshLayout.visibility = View.VISIBLE
            }
            showShortToast(failReason)
        }
    }

    override fun onSoughtHouseClick(id: String?) {

    }

    override fun onReload() {
        mPresenter.initData()
    }
}