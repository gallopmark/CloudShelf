package com.holike.cloudshelf.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.CityPickerActivity
import com.holike.cloudshelf.activity.PlotTypeListActivity
import com.holike.cloudshelf.base.RefreshFragment
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.mvp.presenter.fragment.MyHouseNearbyPresenter
import com.holike.cloudshelf.mvp.view.fragment.MyHouseNearbyHouseView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.fragment_myhouse_nearby.*
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//搜搜我家
class NearMyHouseFragment : RefreshFragment<MyHouseNearbyPresenter, MyHouseNearbyHouseView, SoughtHouseBean>(), MyHouseNearbyHouseView {

    override fun getLayoutResourceId(): Int = R.layout.fragment_myhouse_nearby

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_searchhome, 0, 0, 0)
        typeTView.text = getString(R.string.text_search_my_home)
        startLayoutAnimation()
        mPresenter.initRecyclerView(centerRView)
        refreshLayout.setOnLoadMoreListener { mPresenter.onLoadMore() }
        currentCityTView.setOnClickListener { CityPickerActivity.openForResult(this, 123) }
        searchTView.setOnClickListener { mPresenter.showSearchDialog(mContext) }
        mPresenter.initData()
    }

    //view过度动画
    private fun startLayoutAnimation() {
        topLayout.layoutAnimation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.la_layout_from_bottom)
        view_back.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom))
        bottomCityLayout.scheduleLayoutAnimation()
    }

    //定位开始  此方法里设置定位中...
    override fun onLocationStart() {
        currentCityTView.text = getString(R.string.text_locating)
    }

    //定位成功
    override fun onLocationSuccess(currentCity: String?) {
        currentCityTView.text = String.format(getString(R.string.text_current_position), if (currentCity.isNullOrEmpty()) "" else currentCity)
    }

    //定位失败
    override fun onLocationFailure(failReason: String?) {
        currentCityTView.text = getString(R.string.text_locate_failed)
    }

    //定位结束
    override fun onLocationEnd() {

    }

    override fun whenLoadSuccess(bean: SoughtHouseBean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.pageTotal))
    }

    override fun whenLoadError(failReason: String?) {
        countTView.text = String.format(getString(R.string.text_program_count, "0"))
    }

    //item点击
    override fun onSoughtHouseClick(id: String?, name: String?) {
        PlotTypeListActivity.open(this, id, name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            val city = data.getStringExtra("city")
            onLocationSuccess(city)
            mPresenter.setCurrentCity(city)
        }
    }

    override fun onReload() {
        mPresenter.initData()
    }

}