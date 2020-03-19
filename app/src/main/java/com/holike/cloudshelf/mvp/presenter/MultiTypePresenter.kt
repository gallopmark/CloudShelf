package com.holike.cloudshelf.mvp.presenter

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.MultiTypeActivity
import com.holike.cloudshelf.adapter.PictureDisplayAdapter
import com.holike.cloudshelf.adapter.SpecListAdapter
import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.bean.ProductSpecBean
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.bean.internal.LabelSpec
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import com.holike.cloudshelf.dialog.LabelSpecDialog
import com.holike.cloudshelf.mvp.model.MultiTypeModel
import com.holike.cloudshelf.mvp.view.MultiTypeView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.ListUtils
import pony.xcode.mvp.BasePresenter


class MultiTypePresenter : BasePresenter<MultiTypeModel, MultiTypeView>() {

    companion object {
        private const val PAGE_SIZE = 20 //页码大小
        private const val MAX_SHOW_SIZE = 7 //标签最大显示个数
        private const val SPEC_SPACE_ID = "space-id"  //空间标签id
        private const val SPEC_STYLE_ID = "style-id" //风格标签id
        private const val ALL_SPACE_ID = "all-space-id"  //全部空间id
        private const val ALL_STYLE_ID = "all-style-id" //全部风格id

        private const val ALL_SPEC_ID = "all-spec-id"
    }

    private var mAct: MultiTypeActivity? = null
    private var mType: String? = null
    private var mIconRes: Int = 0
    private var mTitle: String? = null

    private var mPageNo = 1  //页面
    private var mRoomId: String? = null //空间id
    private var mStyleId: String? = null //风格id
    private val mDataList: MutableList<PictureDisplayItem> = ArrayList()
    private var mAdapter: PictureDisplayAdapter? = null

    //底部标签数据
    private var mSpecAdapter: SpecListAdapter? = null
    private var mSpecUpdated = false

    private var mTemplateId: String? = null  //产品大全传过来的id
    private var mLoadProductSpec = false  //是否已经加载了产品大全标签
    private var mHandler: Handler? = null

    fun init(act: MultiTypeActivity) {
        this.mAct = act
        val intent = act.intent
        mType = intent.type
        mIconRes = intent.getIntExtra("icon", 0)
        mTitle = intent.getStringExtra("title")
        mTemplateId = intent.getStringExtra("templateId")
    }

    fun initTitle(typeTView: TextView) {
        typeTView.setCompoundDrawablesWithIntrinsicBounds(mIconRes, 0, 0, 0)
        typeTView.text = mTitle
    }

    fun initRView(act: MultiTypeActivity, centerRView: RecyclerView, bottomRView: RecyclerView) {
        val itemWidth = (CurrentApp.getInstance().getMaxPixels()
                - act.resources.getDimensionPixelSize(R.dimen.dp_45)
                - act.resources.getDimensionPixelSize(R.dimen.dp_16) * 4) / 4f
        mAdapter = PictureDisplayAdapter(act, mDataList, itemWidth.toInt()).apply {
            setOnPictureItemClickListener(object : PictureDisplayAdapter.OnPictureItemClickListener {
                override fun onPictureItemClick(id: String?, images: MutableList<String>?) {
                    view?.onOpenProgramLib(id)
                }
            })
        }
        centerRView.adapter = mAdapter
        mSpecAdapter = SpecListAdapter(act, ArrayList()).apply {
            setOnSpecItemClickListener(object : SpecListAdapter.OnSpecItemClickListener {
                override fun onOpenMore(dataSource: MutableList<LabelSpec>) {
                    showOptionDialog(act, dataSource, getSelected())
                }

                override fun onSpecSelected(map: ArrayMap<String, LabelSpec.Spec>) {
                    if (TextUtils.equals(mType, MultiTypeActivity.TYPE_PROGRAM)) {
                        onProgramSpecSelect(map, false)
                        initData()
                    }
                }
            })
        }
        bottomRView.adapter = mSpecAdapter
    }

    private fun showOptionDialog(context: Context, data: MutableList<LabelSpec>, selected: ArrayMap<String, LabelSpec.Spec>) {
        LabelSpecDialog(context).withData(data, selected).listen(object : LabelSpecDialog.OnConfirmListener {
            override fun onConfirm(map: ArrayMap<String, LabelSpec.Spec>) {
                if (TextUtils.equals(mType, MultiTypeActivity.TYPE_PROGRAM)) {
                    onProgramSpecSelect(map, true)
                }
            }
        }).show()
    }

    /*方案库标签选择*/
    private fun onProgramSpecSelect(map: ArrayMap<String, LabelSpec.Spec>, notifyChanged: Boolean) {
        val roomId = map[SPEC_SPACE_ID]?.id
        mRoomId = if (!TextUtils.equals(roomId, ALL_SPACE_ID)) roomId else null
        val styleId = map[SPEC_STYLE_ID]?.id
        mStyleId = if (!TextUtils.equals(styleId, ALL_STYLE_ID)) styleId else null
        if (notifyChanged) {
            mSpecAdapter?.setSelected(map)
        }
        initData()
    }

    fun initData() {
        mPageNo = 1
        getDataByType()
    }

    private fun getDataByType() {
        if (TextUtils.equals(mType, MultiTypeActivity.TYPE_PROGRAM)) {
            //方案库
            getTableModelHouse()
        } else if (TextUtils.equals(mType, MultiTypeActivity.TYPE_PRODUCT)) {
            //产品大全
            if (!mLoadProductSpec) {
                getProductSpecList()
            }
            getProductPlanList()
        }
    }

    //3D万套案例
    private fun getTableModelHouse() {
        if (mPageNo == 1) {
            view?.onShowLoading()
        }
        mModel?.getTableModelHouse(mRoomId, mStyleId, mPageNo, PAGE_SIZE, object : HttpRequestCallback<TableModelHouseBean>() {
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
            mAct?.let { act ->
                mSpecAdapter?.let {
                    //空间标签
                    val specList = ArrayList<LabelSpec>()
                    val dataSource = ArrayList<LabelSpec>()
                    val spaceBeans = bean.getSpaceBeans()
                    if (spaceBeans.isNotEmpty()) {
                        val totalList = ArrayList<LabelSpec.Spec>()
                        totalList.add(LabelSpec.Spec(ALL_SPACE_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                        for (spec in spaceBeans) {
                            totalList.add(LabelSpec.Spec(spec.id, spec.name))
                        }
                        dataSource.add(LabelSpec(SPEC_SPACE_ID, R.mipmap.ic_classification_space, act.getString(R.string.text_space), totalList))
                        val spaceSpecList = ArrayList<LabelSpec.Spec>()
                        spaceSpecList.add(LabelSpec.Spec(ALL_SPACE_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                        if (spaceBeans.size > MAX_SHOW_SIZE) {
                            val sList = spaceBeans.subList(0, MAX_SHOW_SIZE - 1)
                            for (spec in sList) {
                                spaceSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                            }
                            spaceSpecList.add(LabelSpec.Spec(null, act.getString(R.string.text_more), true))  //添加更多按钮
                        } else {
                            for (spec in spaceBeans) {
                                spaceSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                            }
                        }
                        specList.add(LabelSpec(SPEC_SPACE_ID, R.mipmap.ic_classification_space, act.getString(R.string.text_space), spaceSpecList))
                    }
                    val styleBeans = bean.getStyleBeans()
                    if (styleBeans.isNotEmpty()) {
                        val totalList = ArrayList<LabelSpec.Spec>()
                        totalList.add(LabelSpec.Spec(ALL_STYLE_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                        for (spec in styleBeans) {
                            totalList.add(LabelSpec.Spec(spec.id, spec.name))
                        }
                        dataSource.add(LabelSpec(SPEC_STYLE_ID, R.mipmap.ic_classification_style, act.getString(R.string.text_style), totalList))
                        val styleSpecList = ArrayList<LabelSpec.Spec>()
                        styleSpecList.add(LabelSpec.Spec(ALL_STYLE_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                        if (styleBeans.size > MAX_SHOW_SIZE) {
                            val sList = styleBeans.subList(0, MAX_SHOW_SIZE - 1)
                            for (spec in sList) {
                                styleSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                            }
                            styleSpecList.add(LabelSpec.Spec(null, act.getString(R.string.text_more), true))  //添加更多按钮
                        } else {
                            for (spec in styleBeans) {
                                styleSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                            }
                        }
                        specList.add(LabelSpec(SPEC_STYLE_ID, R.mipmap.ic_classification_style, act.getString(R.string.text_style), styleSpecList))
                    }
                    it.setDataSource(dataSource)
                    it.addAll(specList)
                }
            }
            mSpecUpdated = true
        }
    }

    //获取产品大全 - 底部标签列表和案例列表
    private fun getProductSpecList() {
        mModel.getProductSpecList(mTemplateId, object : HttpRequestCallback<ProductSpecBean>() {
            override fun onSuccess(result: ProductSpecBean, message: String?) {
                updateProductSpec(result)
            }

            override fun onFailure(code: Int, failReason: String?) {
                //加载失败 3秒后重试
                if (mHandler == null) {
                    mHandler = Handler()
                }
                mHandler?.postDelayed({ getProductSpecList() }, 3000)
            }
        })
    }

    //刷新产品大全相关字典标签
    private fun updateProductSpec(bean: ProductSpecBean) {
        if (!mSpecUpdated) {
            mAct?.let { act ->
                mSpecAdapter?.let {
                    //空间标签
                    val dataList = ArrayList<LabelSpec>()
                    val dataSource = ArrayList<LabelSpec>()
                    val beans = bean.obtainSpecList()
                    if (beans.isNotEmpty()) {
                        for (i in beans.indices) {
                            //所有数据
                            val optionList = ArrayList<LabelSpec.Spec>()
                            optionList.add(LabelSpec.Spec(ALL_SPEC_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                            val optionBeans = beans[i].obtainOptionsList()
                            for (option in optionBeans) {
                                optionList.add(LabelSpec.Spec(option.id, option.optionName))
                            }
                            val iconRes = if (i % 2 == 0) R.mipmap.ic_classification_space else R.mipmap.ic_classification_style
                            dataSource.add(LabelSpec(beans[i].id, iconRes, beans[i].specName, optionList))
                            /*************************************************************************/
                            //数据截取
                            val subOptionList = ArrayList<LabelSpec.Spec>()
                            subOptionList.add(LabelSpec.Spec(ALL_SPEC_ID, act.getString(R.string.text_all)))  //添加首位全部标签
                            if (optionBeans.size > MAX_SHOW_SIZE) {
                                val sList = optionBeans.subList(0, MAX_SHOW_SIZE - 1)
                                for (spec in sList) {
                                    subOptionList.add(LabelSpec.Spec(spec.id, spec.optionName))
                                }
                                subOptionList.add(LabelSpec.Spec(null, act.getString(R.string.text_more), true))  //添加更多按钮
                            } else {
                                for (spec in optionBeans) {
                                    subOptionList.add(LabelSpec.Spec(spec.id, spec.optionName))
                                }
                            }
                            dataList.add(LabelSpec(beans[i].id, iconRes, beans[i].specName, subOptionList))
                        }
                    }
                    it.setDataSource(dataSource)
                    it.addAll(dataList)
                }
                mSpecUpdated = true
            }
        }
    }

    /*产品大全各模块的方案列表(分页)*/
    private fun getProductPlanList() {
        if (mPageNo == 1) {
            view?.onShowLoading()
        }
        mModel?.getProductOptionsList(mTemplateId, null, mPageNo, PAGE_SIZE, object : HttpRequestCallback<ProductOptionBean>() {
            override fun onSuccess(result: ProductOptionBean, message: String?) {
                view?.onDismissLoading()
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
            }
        })
    }

    fun onLoadMore() {
        getDataByType()
    }

    override fun unregister() {
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        super.unregister()
    }
}