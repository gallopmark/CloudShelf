package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.RefreshActivity
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.mvp.presenter.SoughtHousePresenter
import com.holike.cloudshelf.mvp.view.SoughtHouseView
import com.scwang.smartrefresh.horizontal.SmartRefreshHorizontal
import kotlinx.android.synthetic.main.activity_sought_house.*
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*


//搜搜我家
class SoughtHouseActivity : RefreshActivity<SoughtHousePresenter, SoughtHouseView, SoughtHouseBean>(), SoughtHouseView {

    override fun getLayoutResourceId(): Int = R.layout.activity_sought_house

    override fun getRefreshLayout(): SmartRefreshHorizontal = refreshLayout

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        typeTView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_title_searchhome, 0, 0, 0)
        typeTView.text = getString(R.string.text_search_my_home)
        startAnim()
        mPresenter.initRecyclerView(centerRView)
        refreshLayout.setOnLoadMoreListener { mPresenter.onLoadMore() }
        currentCityTView.setOnClickListener { CityPickerActivity.openForResult(this@SoughtHouseActivity, 123) }
        searchTView.setOnClickListener { mPresenter.showSearchDialog(this@SoughtHouseActivity) }
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
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            val city = data.getStringExtra("city")
            onLocationSuccess(city)
            mPresenter.setCurrentCity(city)
        }
    }

    override fun onReload() {
        mPresenter.initData()
    }
}