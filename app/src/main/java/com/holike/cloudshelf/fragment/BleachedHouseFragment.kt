package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.BleachedHouseInfoActivity
import com.holike.cloudshelf.base.RefreshFragment
import com.holike.cloudshelf.bean.BleachedHouseBean
import com.holike.cloudshelf.mvp.presenter.fragment.BleachedHousePresenter
import com.holike.cloudshelf.mvp.view.fragment.BleachedHouseView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.include_backtrack_light.*
import kotlinx.android.synthetic.main.include_main_layout.*

//晒晒我家
class BleachedHouseFragment : RefreshFragment<BleachedHousePresenter, BleachedHouseView, BleachedHouseBean>(),
        BleachedHouseView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_bleachedhouse

    override fun getBacktrackResource(): Int = R.layout.include_backtrack_light

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_sharehome, 0, 0, 0)
        typeTView.text = getString(R.string.text_bleached_house)
        startLayoutAnimation()
        mPresenter.initRecyclerView(centerRView)
        mPresenter.initData()
        refreshLayout.setOnLoadMoreListener { mPresenter.onLoadMore() }
    }

    //view过度动画
    private fun startLayoutAnimation() {
        topLayout.layoutAnimation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.la_layout_from_bottom)
        backtrack.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
    }

    override fun whenLoadSuccess(bean: BleachedHouseBean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.pageTotal))
    }

    override fun whenLoadError(failReason: String?) {
        countTView.text = String.format(getString(R.string.text_program_count, "0"))
    }

    override fun onPictureItemClick(id: String?) {
        BleachedHouseInfoActivity.open(this, id)
    }

    override fun onReload() {
        mPresenter.initData()
    }
}