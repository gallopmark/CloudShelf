package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.RefreshActivity
import com.holike.cloudshelf.bean.PlotTypeBean
import com.holike.cloudshelf.mvp.presenter.PlotTypeListPresenter
import com.holike.cloudshelf.mvp.view.PlotTypeListView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//小区户型列表页面
class PlotTypeListActivity : RefreshActivity<PlotTypeListPresenter, PlotTypeListView, PlotTypeBean>(), PlotTypeListView {

    companion object {
        fun open(act: BaseActivity, communityId: String?, name: String?) {
            val intent = Intent(act, PlotTypeListActivity::class.java)
            intent.putExtra("communityId", communityId)
            intent.putExtra("name", name)
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_plottype_list

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_sharehome, 0, 0, 0)
        var text = getString(R.string.text_search_my_home)
        intent.getStringExtra("name")?.let { text += " > $it" }
        typeTView.text = text
        startAnim()
        mPresenter.attachRecyclerView(centerRView)
        mPresenter.init(this)
        refreshLayout.setOnLoadMoreListener { mPresenter.initData() }
    }

    //view过度动画
    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
    }

    override fun whenLoadSuccess(bean: PlotTypeBean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.obtainTotal()))
    }

    override fun whenLoadError(failReason: String?) {
        countTView.text = String.format(getString(R.string.text_program_count, "0"))
    }

    override fun onItemClick(id: String?) {

    }

    override fun onReload() {
        mPresenter.initData()
    }
}