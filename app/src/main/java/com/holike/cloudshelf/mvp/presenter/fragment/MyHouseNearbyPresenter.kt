package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.dialog.SearchDialog
import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.model.fragment.MyHouseNearbyModel
import com.holike.cloudshelf.mvp.view.fragment.MyHouseNearbyHouseView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils
import io.reactivex.disposables.Disposable
import pony.xcode.recycler.lib.CommonAdapter

//搜搜我家presenter
class MyHouseNearbyPresenter : BasePresenter<MyHouseNearbyModel, MyHouseNearbyHouseView>() {
    private class SoughtHouseItem(
            var topId: String?,
            var topUrl: String?,
            var topName: String?,
            var topAddress: String?,
            var bottomId: String?,
            var bottomUrl: String?,
            var bottomName: String?,
            var bottomAddress: String?)

    private inner class SoughtHouseAdapter(context: Context, dataList: MutableList<SoughtHouseItem>)
        : CommonAdapter<SoughtHouseItem>(context, dataList) {

        override fun getItemResourceId(viewType: Int): Int = R.layout.item_soughthouse
        override fun bindViewHolder(holder: RecyclerHolder, t: SoughtHouseItem, position: Int) {
            val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams
            lp.width = mImageWidth
            holder.itemView.layoutParams = lp
            Glide.with(mContext).load(t.topUrl).placeholder(R.color.col_03).centerCrop().into(holder.getView(R.id.iv_pic_top))
            holder.setText(R.id.tv_name_top, t.topName)
            holder.setText(R.id.tv_address_top, t.topAddress)
            Glide.with(mContext).load(t.bottomUrl).centerCrop().into(holder.getView(R.id.iv_pic_bottom))
            holder.setText(R.id.tv_name_bottom, t.bottomName)
            holder.setText(R.id.tv_address_bottom, t.bottomAddress)
            holder.setOnClickListener(R.id.top_layout) {
                if (!t.topId.isNullOrEmpty()) {
                    view?.onSoughtHouseClick(t.topId, t.topName)
                }
            }
            holder.setOnClickListener(R.id.bottom_layout) {
                if (!t.bottomId.isNullOrEmpty()) {
                    view?.onSoughtHouseClick(t.bottomId, t.bottomName)
                }
            }
        }
    }

    private var mImageWidth = 0  //列表图片的宽度
    private var mImageHeight = 0 //列表图片的高度
    private var mPageNo = 1
    private val mPageSize = 20
    private var mCurrentCity: String? = null  //当前定位的城市
    private var mSearchContent: String? = null //搜索内容

    private val mDataList = ArrayList<SoughtHouseItem>()
    private var mAdapter: SoughtHouseAdapter? = null

    fun initRecyclerView(recyclerView: RecyclerView) {
        val context = recyclerView.context
        mImageWidth = ((CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 5.8f).toInt()
        mImageHeight = (mImageWidth * 0.75f).toInt()
        mAdapter = SoughtHouseAdapter(context, mDataList)
        recyclerView.adapter = mAdapter
    }

    //显示搜索对话框
    fun showSearchDialog(context: Context) {
        SearchDialog(context).setHint(context.getString(R.string.hint_community_name))
                .setOnSearchListener(object : SearchDialog.OnSearchListener {
                    override fun onSearch(content: String?) {
                        setSearchContent(content)
                    }
                }).show()
    }

    private fun setSearchContent(content: String?) {
        mSearchContent = content
        initData()
    }

    fun setCurrentCity(city: String?) {
        mCurrentCity = city
        initData()
    }

    fun initData() {
        mPageNo = 1
        if (TextUtils.isEmpty(mCurrentCity)) {
            startLocation()
        } else {
            doSearchCommunityList()
        }
    }

    //开始定位
    private fun startLocation() {
        view?.onLocationStart()
        mModel.onLocationEngine(object : HttpRequestCallback<AMapLocationBean>() {
            override fun onSuccess(result: AMapLocationBean, message: String?) {
                view?.onLocationSuccess(result.city)
                view?.onLocationEnd()
                //定位成功-获取数据
                mCurrentCity = result.city
                doSearchCommunityList()
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onLocationFailure(failReason)
                view?.onLocationEnd()
            }
        })
    }

    //搜搜我家小区列表
    private fun doSearchCommunityList() {
        mModel.doSearchHouseList(mCurrentCity, mSearchContent, mPageNo.toString(), mPageSize.toString(),
                object : HttpRequestCallback<SoughtHouseBean>() {
                    override fun onStart(d: Disposable?) {
                        if (mPageNo == 1) {
                            view?.onShowLoading()
                        }
                    }

                    override fun onSuccess(result: SoughtHouseBean, message: String?) {
                        view?.onSuccess(result, result.obtainDataList().size >= mPageSize)
                        updateSoughtHouse(result.obtainDataList())
                        mPageNo += 1
                    }

                    override fun onFailure(code: Int, failReason: String?) {
                        view?.onFailure(failReason, mPageNo == 1)
                    }

                    override fun onCompleted() {
                        view?.onDismissLoading()
                    }
                })
    }

    private fun updateSoughtHouse(dataList: MutableList<SoughtHouseBean.DataBean>) {
        if (mPageNo == 1) {
            mDataList.clear()
            if (dataList.isEmpty()) {  //加载无数据
                mAdapter?.notifyDataSetChanged()
                view?.onNoResults()
            } else {
                updateSoughtHouseList(dataList)
            }
        } else {
            updateSoughtHouseList(dataList)
        }
    }

    //刷新列表数据
    private fun updateSoughtHouseList(dataList: MutableList<SoughtHouseBean.DataBean>) {
        //list集合差分
        val sList = ListUtils.averageAssignFixLength(dataList, 2)
        for (i in sList.indices) {
            val item: SoughtHouseItem = if (sList[i].size > 1) {
                SoughtHouseItem(sList[i][0].id, sList[i][0].getResizeImage(mImageWidth, mImageHeight), sList[i][0].name, sList[i][0].address,
                        sList[i][1].id, sList[i][1].getResizeImage(mImageWidth, mImageHeight), sList[i][1].name, sList[i][1].address)
            } else {
                SoughtHouseItem(sList[i][0].id, sList[i][0].getResizeImage(mImageWidth, mImageHeight), sList[i][0].name, sList[i][0].address,
                        null, null, null, null)
            }
            mDataList.add(item)
        }
        mAdapter?.notifyDataSetChanged()
    }

    fun onLoadMore() {
        doSearchCommunityList()
    }
}