package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.PlotTypeInfoActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.RefreshFragment
import com.holike.cloudshelf.bean.PlotTypeBean
import com.holike.cloudshelf.mvp.presenter.fragment.PlotTypeListPresenter
import com.holike.cloudshelf.mvp.view.fragment.PlotTypeListView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//小区户型列表
class PlotTypeListFragment : RefreshFragment<PlotTypeListPresenter, PlotTypeListView, PlotTypeBean>(), PlotTypeListView {
    override fun getLayoutResourceId(): Int = R.layout.fragment_plottype_list

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_sharehome, 0, 0, 0)
        var text = getString(R.string.text_search_my_home)
        val bundle = arguments
        bundle?.let { arg -> arg.getString("name")?.let { text += " > $it" } }
        typeTView.text = text
        startLayoutAnimation()
        mPresenter.attachRecyclerView(centerRView)
        mPresenter.setExtraId(bundle?.getString("communityId"))
        refreshLayout.setOnLoadMoreListener { mPresenter.initData() }
    }

    //view过度动画
    private fun startLayoutAnimation() {
        topLayout.layoutAnimation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.la_layout_from_bottom)
        view_back.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
    }

    override fun whenLoadSuccess(bean: PlotTypeBean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.obtainTotal()))
    }

    override fun whenLoadError(failReason: String?) {
        countTView.text = String.format(getString(R.string.text_program_count, "0"))
    }

    override fun onItemClick(id: String?) {
        PlotTypeInfoActivity.open(this, id)
    }

    override fun onReload() {
        mPresenter.initData()
    }
}