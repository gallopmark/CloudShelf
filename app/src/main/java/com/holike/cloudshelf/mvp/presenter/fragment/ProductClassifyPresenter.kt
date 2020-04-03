package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.OnRequestDictListener
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.PictureDisplayAdapter
import com.holike.cloudshelf.adapter.SpecListAdapter
import com.holike.cloudshelf.bean.SystemCodeBean
import com.holike.cloudshelf.bean.internal.LabelSpec
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.dialog.LabelSpecDialog
import com.holike.cloudshelf.enumc.ProductCatalog
import com.holike.cloudshelf.mvp.model.fragment.ProductClassifyModel
import com.holike.cloudshelf.mvp.view.fragment.ProductClassifyView
import com.holike.cloudshelf.netapi.MyJsonParser
import com.holike.cloudshelf.util.LogCat
import pony.xcode.mvp.BasePresenter

class ProductClassifyPresenter : BasePresenter<ProductClassifyModel, ProductClassifyView>(), OnRequestDictListener {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var mDictCode: String? = null  //字典码
    private var mPageNo = 1  //页面
    private val mDataList: MutableList<PictureDisplayItem> = ArrayList()
    private var mAdapter: PictureDisplayAdapter? = null

    //底部标签数据
    private var mSpecAdapter: SpecListAdapter? = null
    private var mSpecUpdated = false

    fun setDictCode(dictCode: String?) {
        this.mDictCode = dictCode
    }

    fun initRView(context: Context, centerRView: RecyclerView, bottomRView: RecyclerView) {
        val itemWidth = (CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 4f
        mAdapter = PictureDisplayAdapter(context, mDataList, itemWidth.toInt()).apply {
            setOnPictureItemClickListener(object :
                    PictureDisplayAdapter.OnPictureItemClickListener {
                override fun onPictureItemClick(id: String?, images: MutableList<String>?) {
                    view?.onItemClick(id)
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
                    if (mDictCode == ProductCatalog.AMBRY && targetPos == 0) {
                        onProductSpecSelect(selected, false)
                    }
                    initData()
                }
            })
        }
        bottomRView.adapter = mSpecAdapter
        val systemCode = CurrentApp.getInstance().getSystemCode()
        if (systemCode == null) {
            CurrentApp.getInstance().getDictionary(this)
        } else {
            setBottomSpec()
        }
    }

    override fun onDictSuccess(code: SystemCodeBean, message: String?) {
        setBottomSpec()
    }

    private fun setBottomSpec() {
        if (!mSpecUpdated) {
            mSpecAdapter?.let { adapter ->
                if (mDictCode == ProductCatalog.AMBRY) {
                    adapter.onCupboardTypeSelected(ProductCatalog.AMBRY_CUSTOM_MADE, null)
                } else {
                    adapter.updateProductSpec(mDictCode)
                }
            }
            mSpecUpdated = true
        }
    }

    private fun showOptionDialog(context: Context, data: MutableList<LabelSpec>, selected: ArrayMap<String, LabelSpec.Spec>) {
        LabelSpecDialog(context).withData(data, selected)
                .withDictCode(mDictCode)
                .listen(object : LabelSpecDialog.OnConfirmListener {
                    override fun onConfirm(selected: ArrayMap<String, LabelSpec.Spec>, isDataChanged: Boolean) {
                        if (mDictCode == ProductCatalog.AMBRY && isDataChanged) {
                            onProductSpecSelect(selected, true)
                        } else {
                            mSpecAdapter?.setSelected(selected)
                        }
                        initData()
                    }
                }).show()
    }

    private fun onProductSpecSelect(selected: ArrayMap<String, LabelSpec.Spec>, reset: Boolean) {
        val spec = selected[ProductCatalog.DICT_CUPBOARD_TYPE]
        spec?.let {
            val dictCode = it.id
            if (dictCode == ProductCatalog.AMBRY_CUSTOM_MADE || dictCode == ProductCatalog.AMBRY_APPLIANCES) {
                if (reset) {
                    mSpecAdapter?.onCupboardTypeSelected(dictCode, selected)
                } else {
                    mSpecAdapter?.onCupboardTypeSelected(dictCode, null)
                }
            }
        }
    }

    fun initData() {
        mPageNo = 1
        getProductProgramData()
    }

    fun onLoadMore() {
        getProductProgramData()
    }

    //获取各个模块产品方案列表
    private fun getProductProgramData() {
        val specMap = HashMap<String, String>()
        mSpecAdapter?.getSelected()?.let {
            for ((k, v) in it) {
                specMap.put(k, v.obtainId())
            }
            LogCat.e("spec", MyJsonParser.toJson(specMap))
        }
    }
}