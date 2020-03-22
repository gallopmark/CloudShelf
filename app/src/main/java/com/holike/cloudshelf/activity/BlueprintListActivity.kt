package com.holike.cloudshelf.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.RefreshActivity
import com.holike.cloudshelf.bean.BlueprintBean
import com.holike.cloudshelf.mvp.presenter.BlueprintListPresenter
import com.holike.cloudshelf.mvp.view.BlueprintListView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//晒晒我家
class BlueprintListActivity : RefreshActivity<BlueprintListPresenter, BlueprintListView, BlueprintBean>(),
        BlueprintListView {

    override fun getLayoutResourceId(): Int = R.layout.activity_blueprint_list

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_sharehome, 0, 0, 0)
        typeTView.text = getString(R.string.text_bleached_house)
        startAnim()
        mPresenter.initRecyclerView(centerRView)
        mPresenter.initData()
        refreshLayout.setOnLoadMoreListener { mPresenter.onLoadMore() }
    }

    //view过度动画
    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
    }

    override fun whenLoadSuccess(bean: BlueprintBean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.pageTotal))
    }

    override fun whenLoadError(failReason: String?) {
        countTView.text = String.format(getString(R.string.text_program_count, "0"))
    }

    override fun onPictureItemClick(id: String?) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.translate_to_bottom2)
        containerLayout.startAnimation(animation)
        containerLayout.postDelayed({
            if (!isFinishing) {
                BlueprintInfoActivity.open(this@BlueprintListActivity, id)
            }
        }, 800)
    }

    override fun onReload() {
        mPresenter.initData()
    }
}