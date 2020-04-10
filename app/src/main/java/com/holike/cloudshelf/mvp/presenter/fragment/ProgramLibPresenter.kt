package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.text.TextUtils
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.PictureDisplayAdapter
import com.holike.cloudshelf.adapter.SpecListAdapter
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.bean.internal.LabelSpec
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.dialog.LabelSpecDialog
import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.model.fragment.ProgramLibModel
import com.holike.cloudshelf.mvp.view.fragment.ProgramLibView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils


class ProgramLibPresenter : BasePresenter<ProgramLibModel, ProgramLibView>() {
    companion object {
        private const val PAGE_SIZE = 20
    }

    private var mPageNo = 1  //页面
    private var mRoomId: String? = null //空间id
    private var mStyleId: String? = null //风格id
    private val mDataList: MutableList<PictureDisplayItem> = ArrayList()
    private var mAdapter: PictureDisplayAdapter? = null

    //底部标签数据
    private var mSpecAdapter: SpecListAdapter? = null
    private var mSpecUpdated = false

    fun initRView(context: Context, centerRView: RecyclerView, bottomRView: RecyclerView) {
        val itemWidth = (CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 4f
        mAdapter = PictureDisplayAdapter(context, mDataList, itemWidth.toInt()).apply {
            setOnPictureItemClickListener(object :
                    PictureDisplayAdapter.OnPictureItemClickListener {
                override fun onPictureItemClick(id: String?, images: MutableList<String>?) {
                    view?.onOpenProgramLib(id)
                }
            })
        }
        centerRView.adapter = mAdapter
        mSpecAdapter = SpecListAdapter(context, ArrayList()).apply {
            setOnSpecItemClickListener(object : SpecListAdapter.OnSpecItemClickListener() {
                override fun onOpenMore(dataSource: MutableList<LabelSpec>) {
                    showOptionDialog(context, dataSource, getSelected())
                }

                override fun onSpecSelected(targetPos: Int, selected: ArrayMap<String, LabelSpec.Spec>) {
                    onProgramSpecSelect(selected, false)
                    initData()
                }
            })
        }
        bottomRView.adapter = mSpecAdapter
    }

    private fun showOptionDialog(context: Context, data: MutableList<LabelSpec>, selected: ArrayMap<String, LabelSpec.Spec>) {
        LabelSpecDialog(context).withData(data, selected)
                .listen(object : LabelSpecDialog.OnConfirmListener {
                    override fun onConfirm(selected: ArrayMap<String, LabelSpec.Spec>, isDataChanged: Boolean) {
                        onProgramSpecSelect(selected, true)
                    }
                }).show()
    }

    /*方案库标签选择*/
    private fun onProgramSpecSelect(map: ArrayMap<String, LabelSpec.Spec>, notifyChanged: Boolean) {
        val roomId = map[SpecListAdapter.SPEC_SPACE_ID]?.id
        mRoomId = if (!TextUtils.equals(roomId, SpecListAdapter.ALL_SPACE_ID)) roomId else null
        val styleId = map[SpecListAdapter.SPEC_STYLE_ID]?.id
        mStyleId = if (!TextUtils.equals(styleId, SpecListAdapter.ALL_STYLE_ID)) styleId else null
        if (notifyChanged) {
            mSpecAdapter?.setSelected(map)
        }
        initData()
    }

    fun initData() {
        mPageNo = 1
        getTableModelHouse()
    }

    fun onLoadMore() {
        getTableModelHouse()
    }

    //3D万套案例
    private fun getTableModelHouse() {
        if (mPageNo == 1) {
            view?.onShowLoading()
        }
        mModel.getTableModelHouse(mRoomId, mStyleId, mPageNo, PAGE_SIZE, object : HttpRequestCallback<TableModelHouseBean>() {
            override fun onSuccess(result: TableModelHouseBean, message: String?) {
                view?.onDismissLoading()
                view?.onTableModelHouseResponse(result, result.getData().size >= PAGE_SIZE)
                onTableModelHouseSuccess(result)
                mPageNo += 1  //页码自动加1
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
                view?.onTableModelHouseFailure(failReason, mPageNo == 1)
            }
        })
    }

    /*获取方案库成功*/
    fun onTableModelHouseSuccess(bean: TableModelHouseBean) {
        if (mPageNo == 1) {
            mDataList.clear()
            if (bean.getData().isEmpty()) {  //加载无数据
                view?.onNoQueryResults()
            } else {
                updateTableModelHouseData(bean)
            }
        } else {
            updateTableModelHouseData(bean)
        }
        updateTableModelSpec(bean)
    }

    //方案库列表更新
    private fun updateTableModelHouseData(bean: TableModelHouseBean) {
        val dataList = ListUtils.averageAssignFixLength(bean.getData(), 2)
        for (i in dataList.indices) {
            val item: PictureDisplayItem
            if (dataList[i].size > 1) {
                item = PictureDisplayItem(dataList[i][0].getFirstImage(), dataList[i][0].name, dataList[i][1].getFirstImage(),
                        dataList[i][1].name, dataList[i][0].obtainImages(), dataList[i][1].obtainImages())
                item.topId = dataList[i][0].id
                item.bottomId = dataList[i][1].id
            } else {
                item = PictureDisplayItem(dataList[i][0].getFirstImage(), dataList[i][0].name, null,
                        null, dataList[i][0].obtainImages(), null)
                item.topId = dataList[i][0].id
            }
            mDataList.add(item)
        }
        mAdapter?.notifyDataSetChanged()
    }

    /*刷新方案库字典标签*/
    private fun updateTableModelSpec(bean: TableModelHouseBean) {
        if (!mSpecUpdated) {
            mSpecAdapter?.updateTableModelSpec(bean)
            view?.onBottomSpecUpdate()
            mSpecUpdated = true
        }
    }
}