package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.PlotTypeBean
import com.holike.cloudshelf.mvp.presenter.PlotTypeListPresenter
import com.holike.cloudshelf.mvp.view.PlotTypeListView
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//小区户型列表页面
class PlotTypeListActivity : HollyActivity<PlotTypeListPresenter, PlotTypeListView>(), PlotTypeListView {

    companion object {
        fun open(act: BaseActivity, communityId: String?, name: String?) {
            val intent = Intent(act, PlotTypeListActivity::class.java)
            intent.putExtra("communityId", communityId)
            intent.putExtra("name", name)
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_plottype_list

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_sharehome, 0, 0, 0)
        var text = getString(R.string.text_search_my_home)
        intent.getStringExtra("name")?.let { text += " > $it" }
        typeTView.text = text
        startAnim()
        mPresenter.attachRecyclerView(centerRView)
        mPresenter.init(this)
    }

    //view过度动画
    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onSuccess(bean: PlotTypeBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.obtainTotal()))
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

    override fun onFailure(failReason: String?, isShowError: Boolean) {
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

    override fun onItemClick(id: String?) {

    }

    override fun onReload() {
        mPresenter.initData()
    }
}