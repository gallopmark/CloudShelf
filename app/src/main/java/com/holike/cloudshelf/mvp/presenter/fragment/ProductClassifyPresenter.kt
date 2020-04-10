package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.os.Bundle
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.OnRequestDictListener
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.PictureDisplayAdapter
import com.holike.cloudshelf.adapter.SpecListAdapter
import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.bean.SystemCodeBean
import com.holike.cloudshelf.bean.internal.LabelSpec
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.dialog.LabelSpecDialog
import com.holike.cloudshelf.enumc.ProductCatalog
import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.model.fragment.ProductClassifyModel
import com.holike.cloudshelf.mvp.view.fragment.ProductClassifyView
import com.holike.cloudshelf.netapi.ApiService
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils
import io.reactivex.disposables.Disposable
import okhttp3.RequestBody

class ProductClassifyPresenter : BasePresenter<ProductClassifyModel, ProductClassifyView>(),
        OnRequestDictListener {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var mDictCode: String? = null  //字典码:产品大全个模块类型:WHOLE_HOUSE/DOOR/AMBRY/HOME_PRO
    private var mSpaceFurnished: String? = null //空间的id(成品家具)与category_Furnished同时传值
    private var mCategoryFurnished: String? = null //品类的id(成品家具).与space_furnished同时传值

    private var mCategoryCurtain: String? = null //品类的id(定制窗帘)
    private var mPageNo = 1  //页面

    private var mImageWidth = 0
    private var mImageHeight = 0
    private val mDataList: MutableList<PictureDisplayItem> = ArrayList()
    private var mAdapter: PictureDisplayAdapter? = null

    //底部标签数据
    private var mSpecAdapter: SpecListAdapter? = null
    private var mSpecUpdated = false

    fun setDictCode(extras: Bundle, dictCode: String?) {
        this.mDictCode = dictCode
        if (dictCode == ProductCatalog.HOME_PRO_FURNISHED || dictCode == ProductCatalog.HOME_PRO_CURTAIN) {
            val showNavigation = extras.getBoolean("isShowNavigation")
            view?.onShowNavigation(showNavigation)
            if (dictCode == ProductCatalog.HOME_PRO_FURNISHED) {
                mSpaceFurnished = extras.getString("space_furnished")
                mCategoryFurnished = extras.getString("categoryId")
            } else {
                mCategoryCurtain = extras.getString("categoryId")
            }
        }
    }

    fun initRView(context: Context, centerRView: RecyclerView, bottomRView: RecyclerView) {
        mImageWidth = ((CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45)
                - context.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 4f).toInt()
        mImageHeight = (mImageWidth * 0.75f).toInt()
        mAdapter = PictureDisplayAdapter(context, mDataList, mImageWidth).apply {
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
                    adapter.onCupboardTypeSelected(ProductCatalog.AMBRY_CUSTOM_MADE, null, false)
                } else {
                    adapter.updateProductSpec(mDictCode)
                }
            }
            view?.onBottomSpecUpdate()
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
                    mSpecAdapter?.onCupboardTypeSelected(dictCode, selected, false)
                } else {
                    mSpecAdapter?.onCupboardTypeSelected(dictCode, null, false)
                }
            }
        }
    }

    fun initData() {
        mPageNo = 1
        getProductProgramData()
    }

    //加载更多
    fun onLoadMore() {
        getProductProgramData()
    }

    //获取各个模块产品方案列表
    private fun getProductProgramData() {
        mModel.getProductCatalogList(paramToJsonByType(), object : HttpRequestCallback<ProductOptionBean>() {
            override fun onStart(d: Disposable?) {
                if (mPageNo == 1) {
                    view?.onShowLoading()
                }
            }

            override fun onSuccess(result: ProductOptionBean, message: String?) {
                view?.onProductClassifyResponse(result, result.obtainDataList().size >= PAGE_SIZE)
                updatePage(result)
                mPageNo += 1  //数据加载成功 页面自增
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onProductClassifyFailure(failReason, mPageNo == 1)
            }

            override fun onCompleted() {
                view?.onDismissLoading()
            }
        })
    }

    private fun paramToJsonByType(): RequestBody {
        val param = HashMap<String, String?>().apply {
            mSpecAdapter?.getSelected()?.let { selected ->
                when (mDictCode) {
                    ProductCatalog.WHOLE_HOUSE -> {  //全屋定制
                        put("module_type", mDictCode)
                        put("space_house", selected[ProductCatalog.DICT_SPACE_HOUSE]?.id)  //空间(全屋)
                        put("series_house", selected[ProductCatalog.DICT_SERIES_HOUSE]?.id) //系列(全屋)
                    }
                    ProductCatalog.AMBRY -> { //橱柜定制
                        put("module_type", mDictCode)
                        val type = selected[ProductCatalog.DICT_CUPBOARD_TYPE]?.id
                        put("ambry_type", type) //AMBRY_CUSTOM_MADE/AMBRY_APPLIANCES
                        if (type == ProductCatalog.AMBRY_CUSTOM_MADE) { //橱柜类型
                            put("model_ambry", selected[ProductCatalog.DICT_CUPBOARD_MODEL]?.id) //造型
                            put("series_ambry", selected[ProductCatalog.DICT_CUPBOARD_SERIES]?.id) //系列
                        } else {
                            //电器类型
                            put("brand_appliance", selected[ProductCatalog.DICT_BRAND_APPLIANCE]?.id) //品牌
                            put("function_appliance", selected[ProductCatalog.DICT_FUNCTION_APPLIANCE]?.id) //功能
                        }
                    }
                    ProductCatalog.DOOR -> { //木门定制
                        put("module_type", mDictCode)
                        put("category_door", selected[ProductCatalog.DICT_DOOR_CATEGORY]?.id) //品类-木门
                        put("series_door", selected[ProductCatalog.DICT_DOOR_SERIES]?.id) //系列-木门
                    }
                    ProductCatalog.HOME_PRO_FURNISHED -> { //家居家品-成品
                        put("module_type", ProductCatalog.HOME_PRO)
                        put("home_pro_cla", mDictCode)
                        put("space_furnished", mSpaceFurnished)
                        put("category_furnished", mCategoryFurnished)
                        put("series_furnished", selected[ProductCatalog.HOME_PRO_FURNISHED]?.id)
                    }
                    ProductCatalog.HOME_PRO_CURTAIN -> { //家居家品-窗帘
                        put("module_type", ProductCatalog.HOME_PRO)
                        put("home_pro_cla", mDictCode)
                        put("category_curtain", mCategoryCurtain)
                        put("style_curtain", selected[ProductCatalog.HOME_PRO_CURTAIN]?.id)
                    }
                    else -> {
                    }
                }
            }
            put("pageNo", mPageNo.toString())
            put("pageSize", PAGE_SIZE.toString())
        }
        return ApiService.createRequestBody(param)
    }

    private fun updatePage(bean: ProductOptionBean) {
        if (mPageNo == 1) {
            mDataList.clear()
            if (bean.obtainDataList().isEmpty()) {  //加载无数据
                view?.onNoQueryResults()
            } else {
                updateImageAdapter(bean.obtainDataList())
            }
        } else {
            updateImageAdapter(bean.obtainDataList())
        }
    }

    private fun updateImageAdapter(dataList: MutableList<ProductOptionBean.DataBean>) {
        val sList = ListUtils.averageAssignFixLength(dataList, 2)
        for (i in sList.indices) {
            val item: PictureDisplayItem
            if (sList[i].size > 1) {
                item = PictureDisplayItem(sList[i][0].getImageUrl(mImageWidth, mImageHeight), sList[i][0].title,
                        sList[i][1].getImageUrl(mImageWidth, mImageHeight), sList[i][1].title)
                item.topId = sList[i][0].id
                item.bottomId = sList[i][1].id
            } else {
                item = PictureDisplayItem(sList[i][0].getImageUrl(mImageWidth, mImageHeight), sList[i][0].title, null, null)
                item.topId = sList[i][0].id
            }
            mDataList.add(item)
        }
        mAdapter?.notifyDataSetChanged()
    }
}