package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseFragment
import com.holike.cloudshelf.dialog.SearchDialog
import kotlinx.android.synthetic.main.fragment_citypicker.*
import kotlinx.android.synthetic.main.include_city_empty_view.*
import pony.xcode.citypicker.adapter.CityListAdapter
import pony.xcode.citypicker.adapter.InnerListener
import pony.xcode.citypicker.adapter.OnPickListener
import pony.xcode.citypicker.adapter.decoration.SectionItemDecoration
import pony.xcode.citypicker.db.DBManager
import pony.xcode.citypicker.model.City
import pony.xcode.citypicker.model.HotCity
import pony.xcode.citypicker.model.LocateState
import pony.xcode.citypicker.model.LocatedCity
import pony.xcode.citypicker.util.ScreenUtil
import pony.xcode.citypicker.view.SideIndexBar

//城市选择
class CityPickerFragment : BaseFragment(), InnerListener,
        SideIndexBar.OnIndexTouchedChangedListener {
    private var mAdapter: CityListAdapter? = null
    private var mAllCities: MutableList<City> = ArrayList()  //所有城市
    private var mHotCities: MutableList<HotCity> = ArrayList()  //热门城市
    private var mResults: MutableList<City> = ArrayList() //查询结果集

    private lateinit var mDbManager: DBManager

    private var mLocatedCity: LocatedCity? = null //定位城市
    private var locateState = 0
    private var mOnPickListener: OnPickListener? = null

    //设置当前定位城市
    fun setLocationCity(locatedCity: LocatedCity?) {
        this.mLocatedCity = locatedCity
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_citypicker

    override fun setup(savedInstanceState: Bundle?) {
        initData()
        val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        cityRView.layoutManager = layoutManager
        cityRView.setHasFixedSize(true)
        cityRView.addItemDecoration(SectionItemDecoration(mContext, mAllCities, layoutManager), 0)
//        cityRView.addItemDecoration(DividerItemDecoration(mContext), 1)
        mAdapter = CityListAdapter(mContext, mAllCities, mHotCities, locateState).apply {
            autoLocate(true)
            setInnerListener(this@CityPickerFragment)
            setLayoutManager(layoutManager)
            setGridSpanCount(5)
        }
        cityRView.adapter = mAdapter
        cityRView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter?.refreshLocationItem()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        })
        sideIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(mContext))
        sideIndexBar.setOverlayTextView(overlayTView).setOnIndexChangedListener(this)
        searchTView.setOnClickListener { showSearchDialog() }
    }

    private fun initData() {
        //初始化热门城市
        if (mHotCities.isEmpty()) {
            mHotCities = ArrayList()
            mHotCities.add(HotCity("北京", "北京", "101010100"))
            mHotCities.add(HotCity("上海", "上海", "101020100"))
            mHotCities.add(HotCity("广州", "广东", "101280101"))
            mHotCities.add(HotCity("深圳", "广东", "101280601"))
            mHotCities.add(HotCity("天津", "天津", "101030100"))
            mHotCities.add(HotCity("杭州", "浙江", "101210101"))
            mHotCities.add(HotCity("南京", "江苏", "101190101"))
            mHotCities.add(HotCity("成都", "四川", "101270101"))
            mHotCities.add(HotCity("武汉", "湖北", "101200101"))
        }
        //初始化定位城市，默认为空时会自动回调定位
        var locatedCity = mLocatedCity
        if (locatedCity == null) {
            locatedCity = LocatedCity(getString(R.string.cp_locating), "未知", "0")
            locateState = LocateState.LOCATING
        } else {
            locateState = LocateState.SUCCESS
        }
        mDbManager = DBManager(mContext)
        mAllCities = mDbManager.allCities
        mAllCities.add(0, locatedCity)
        mAllCities.add(1, HotCity("热门城市", "未知", "0"))
        mResults = mAllCities
    }

    override fun dismiss(position: Int, data: City?) {
        mOnPickListener?.onPick(position, data)
    }

    override fun locate() {
        mOnPickListener?.onLocate()
    }

    override fun onIndexChanged(index: String?, position: Int) {
        //滚动RecyclerView到索引位置
        mAdapter?.scrollToSection(index)
    }

    fun locationChanged(location: LocatedCity, state: Int) {
        mAdapter?.updateLocateState(location, state)
    }

    private fun showSearchDialog() {
        SearchDialog(mContext).setHint(mContext.getString(R.string.hint_input_city))
                .setOnSearchListener(object : SearchDialog.OnSearchListener {
                    override fun onSearch(content: String?) {
                        val keyword = content.toString()
                        if (TextUtils.isEmpty(keyword)) {
                            cityRView.visibility = View.VISIBLE
                            emptyTView.visibility = View.GONE
                            mResults = mAllCities
                            (cityRView.getItemDecorationAt(0) as SectionItemDecoration).setData(mResults)
                            mAdapter?.updateData(mResults)
                        } else {
                            //开始数据库查找
                            mResults = mDbManager.searchCity(keyword)
                            (cityRView.getItemDecorationAt(0) as SectionItemDecoration).setData(mResults)
                            if (mResults.isEmpty()) {
                                emptyTView.visibility = View.VISIBLE
                                cityRView.visibility = View.GONE
                            } else {
                                emptyTView.visibility = View.GONE
                                cityRView.visibility = View.VISIBLE
                                mAdapter?.updateData(mResults)
                            }
                        }
                        cityRView.scrollToPosition(0)
                    }
                }).show()
    }

    fun setOnPickListener(listener: OnPickListener?) {
        mOnPickListener = listener
    }
}