package com.holike.cloudshelf.adapter

import android.content.Context
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.bean.internal.LabelSpec
import com.holike.cloudshelf.enumc.ProductCatalog
import pony.xcode.recycler.CommonAdapter


class SpecListAdapter : CommonAdapter<LabelSpec> {
    companion object {
        private const val MAX_SHOW_SIZE = 7 //标签最大显示个数
        const val SPEC_SPACE_ID = "space-id"  //空间标签id
        const val SPEC_STYLE_ID = "style-id" //风格标签id
        const val ALL_SPACE_ID = "all-space-id"  //全部空间id
        const val ALL_STYLE_ID = "all-style-id" //全部风格id
    }

    constructor(context: Context, dataList: MutableList<LabelSpec>) : super(context, dataList)

    constructor(context: Context, dataList: MutableList<LabelSpec>, selected: ArrayMap<String, LabelSpec.Spec>)
            : this(context, dataList) {
        this.mSelected = selected
    }

    private val mDataSource = ArrayList<LabelSpec>()
    private var mSelected = ArrayMap<String, LabelSpec.Spec>()
    private var mOnSpecItemClickListener: OnSpecItemClickListener? = null

    //来自方案库的标签集
    fun updateTableModelSpec(bean: TableModelHouseBean) {
        //空间标签
        val specList = ArrayList<LabelSpec>()
        val dataSource = ArrayList<LabelSpec>()
        val spaceBeans = bean.getSpaceBeans()
        if (spaceBeans.isNotEmpty()) {
            val totalList = ArrayList<LabelSpec.Spec>()
            totalList.add(LabelSpec.Spec(ALL_SPACE_ID, mContext.getString(R.string.text_all)))  //添加首位全部标签
            for (spec in spaceBeans) {
                totalList.add(LabelSpec.Spec(spec.id, spec.name))
            }
            dataSource.add(LabelSpec(SPEC_SPACE_ID, R.mipmap.ic_classification_space, mContext.getString(R.string.text_space), totalList))
            val spaceSpecList = ArrayList<LabelSpec.Spec>()
            spaceSpecList.add(LabelSpec.Spec(ALL_SPACE_ID, mContext.getString(R.string.text_all)))  //添加首位全部标签
            if (spaceBeans.size > MAX_SHOW_SIZE) {
                val sList = spaceBeans.subList(0, MAX_SHOW_SIZE - 1)
                for (spec in sList) {
                    spaceSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                }
                spaceSpecList.add(LabelSpec.Spec(null, mContext.getString(R.string.text_more), true))  //添加更多按钮
            } else {
                for (spec in spaceBeans) {
                    spaceSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                }
            }
            specList.add(LabelSpec(SPEC_SPACE_ID, R.mipmap.ic_classification_space, mContext.getString(R.string.text_space), spaceSpecList))
        }
        val styleBeans = bean.getStyleBeans()
        if (styleBeans.isNotEmpty()) {
            val totalList = ArrayList<LabelSpec.Spec>()
            totalList.add(LabelSpec.Spec(ALL_STYLE_ID, mContext.getString(R.string.text_all)))  //添加首位全部标签
            for (spec in styleBeans) {
                totalList.add(LabelSpec.Spec(spec.id, spec.name))
            }
            dataSource.add(LabelSpec(SPEC_STYLE_ID, R.mipmap.ic_classification_style, mContext.getString(R.string.text_style), totalList))
            val styleSpecList = ArrayList<LabelSpec.Spec>()
            styleSpecList.add(LabelSpec.Spec(ALL_STYLE_ID, mContext.getString(R.string.text_all)))  //添加首位全部标签
            if (styleBeans.size > MAX_SHOW_SIZE) {
                val sList = styleBeans.subList(0, MAX_SHOW_SIZE - 1)
                for (spec in sList) {
                    styleSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                }
                styleSpecList.add(LabelSpec.Spec(null, mContext.getString(R.string.text_more), true))  //添加更多按钮
            } else {
                for (spec in styleBeans) {
                    styleSpecList.add(LabelSpec.Spec(spec.id, spec.name))
                }
            }
            specList.add(LabelSpec(SPEC_STYLE_ID, R.mipmap.ic_classification_style, mContext.getString(R.string.text_style), styleSpecList))
        }
        setDataSource(dataSource)
        addAll(specList)
    }

    //来自产品大全的字典集
    fun updateProductSpec(dictType: String?) {
        val code = CurrentApp.getInstance().getSystemCode() ?: return
        if (dictType != ProductCatalog.WHOLE_HOUSE &&
                dictType != ProductCatalog.DOOR &&
                dictType != ProductCatalog.HOME_PRO_FURNISHED &&
                dictType != ProductCatalog.HOME_PRO_CURTAIN) {
            return
        }
        val dataList = ArrayList<LabelSpec>()
        val dataSource = ArrayList<LabelSpec>()
        when (dictType) {
            ProductCatalog.WHOLE_HOUSE -> { //全屋定制
                val spaceSource = code.getSpaceHouseCode() //全屋定制-空间字典
                //添加空间字典
                dataSource.add(obtainLabelSpec(spaceSource, ProductCatalog.DICT_SPACE_HOUSE,
                        R.mipmap.ic_classification_space, mContext.getString(R.string.text_space), true))
                dataList.add(obtainLabelSpecSub(spaceSource, ProductCatalog.DICT_SPACE_HOUSE,
                        R.mipmap.ic_classification_space, mContext.getString(R.string.text_space), true))
                /**********************************************************************************/
                val seriesSource = code.getSeriesHouseCode() //全屋定制-系列字典
                //添加系列字典
                dataSource.add(obtainLabelSpec(seriesSource, ProductCatalog.DICT_SERIES_HOUSE,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
                dataList.add(obtainLabelSpecSub(seriesSource, ProductCatalog.DICT_SERIES_HOUSE,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
            }
            ProductCatalog.DOOR -> { //木门定制
                val categorySource = code.getCategoryDoorCode()  //品类字典
                dataSource.add(obtainLabelSpec(categorySource, ProductCatalog.DICT_DOOR_CATEGORY,
                        R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_category), true))
                dataList.add(obtainLabelSpecSub(categorySource, ProductCatalog.DICT_DOOR_CATEGORY,
                        R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_category), true))
                val seriesSource = code.getSeriesDoorCode() //系列字典
                dataSource.add(obtainLabelSpec(seriesSource, ProductCatalog.DICT_DOOR_SERIES,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
                dataList.add(obtainLabelSpecSub(seriesSource, ProductCatalog.DICT_DOOR_SERIES,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
            }
            ProductCatalog.HOME_PRO_FURNISHED -> { //家居家品-成品家具
                val seriesSource = code.getSeriesFurnishedCode()
                dataSource.add(obtainLabelSpec(seriesSource, ProductCatalog.HOME_PRO_FURNISHED, R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
                dataList.add(obtainLabelSpecSub(seriesSource, ProductCatalog.HOME_PRO_FURNISHED, R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
            }
            else -> {
                val styleSource = code.getStyleCurtainCode()
                dataSource.add(obtainLabelSpec(styleSource, ProductCatalog.HOME_PRO_CURTAIN, R.mipmap.ic_classification_style, mContext.getString(R.string.text_style), true))
                dataList.add(obtainLabelSpecSub(styleSource, ProductCatalog.HOME_PRO_CURTAIN, R.mipmap.ic_classification_style, mContext.getString(R.string.text_style), true))
            }
        }
        setDataSource(dataSource)
        addAll(dataList)
    }

    //添加所有的标签
    private fun obtainLabelSpec(source: HashMap<String, String>, id: String, iconRes: Int, name: String, addAllFirst: Boolean): LabelSpec {
        val list = ArrayList<LabelSpec.Spec>()
        if (addAllFirst) {
            //添加首位全部标签
            list.add(LabelSpec.Spec("", mContext.getString(R.string.text_all)))
        }
        for ((k, v) in source) {
            list.add(LabelSpec.Spec(k, v))
        }
        return LabelSpec(id, iconRes, name, list)
    }

    //添加截取后的标签数组
    private fun obtainLabelSpecSub(source: HashMap<String, String>, id: String, iconRes: Int, name: String, addAllFirst: Boolean): LabelSpec {
        val specList = ArrayList<LabelSpec.Spec>()
        //添加首位全部标签
        if (addAllFirst) {
            specList.add(LabelSpec.Spec("", mContext.getString(R.string.text_all)))
        }
        if (source.size > MAX_SHOW_SIZE) {
            var i = 0
            for ((k, v) in source) {
                if (i >= MAX_SHOW_SIZE - 1) {
                    break
                }
                specList.add(LabelSpec.Spec(k, v))
                i++
            }
            specList.add(LabelSpec.Spec(null, mContext.getString(R.string.text_more), true))  //添加更多按钮
        } else {
            for ((k, v) in source) {
                specList.add(LabelSpec.Spec(k, v))
            }
        }
        return LabelSpec(id, iconRes, name, specList)
    }

    //橱柜定制-类型切换
    fun onCupboardTypeSelected(dictCode: String?, selected: ArrayMap<String, LabelSpec.Spec>?, includeEdge: Boolean) {
        val code = CurrentApp.getInstance().getSystemCode() ?: return
        val dataList = ArrayList<LabelSpec>()
        val dataSource = ArrayList<LabelSpec>()
        val typeSource = code.getAmBryCode()  //橱柜定制-类型字典
        dataSource.add(obtainLabelSpec(typeSource, ProductCatalog.DICT_CUPBOARD_TYPE,
                R.mipmap.ic_classifition_type, mContext.getString(R.string.text_type), false))
        val labelSpec = obtainLabelSpecSub(typeSource, ProductCatalog.DICT_CUPBOARD_TYPE,
                R.mipmap.ic_classifition_type, mContext.getString(R.string.text_type), false)
        dataList.add(labelSpec)
        if (dictCode == ProductCatalog.AMBRY_CUSTOM_MADE) {
            //橱柜定制-橱柜类型
            val modelSource = code.getModelAmBryCode() //橱柜定制-造型字典
            val modelSpec = obtainLabelSpec(modelSource, ProductCatalog.DICT_CUPBOARD_MODEL,
                    R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_modeling), true)
            dataSource.add(modelSpec)
            val seriesSource = code.getSeriesAmBryCode()
            val seriesSpec = obtainLabelSpec(seriesSource, ProductCatalog.DICT_CUPBOARD_SERIES,
                    R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true)
            dataSource.add(seriesSpec)
            if (includeEdge) {
                dataList.add(modelSpec)
                dataList.add(seriesSpec)
            } else {
                dataList.add(obtainLabelSpecSub(modelSource, ProductCatalog.DICT_CUPBOARD_MODEL,
                        R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_modeling), true))
                dataList.add(obtainLabelSpecSub(seriesSource, ProductCatalog.DICT_CUPBOARD_SERIES,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_series), true))
            }
        } else {
            //橱柜定制-电器类型
            val brandSource = code.getBrandApplianceCode() //橱柜电器-品牌字典
            val brandSpec = obtainLabelSpec(brandSource, ProductCatalog.DICT_BRAND_APPLIANCE,
                    R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_brand), true)
            dataSource.add(brandSpec)
            val funcSource = code.getFunctionApplianceCode() //橱柜电器-功能字典
            val funcSpec = obtainLabelSpec(code.getFunctionApplianceCode(), ProductCatalog.DICT_FUNCTION_APPLIANCE,
                    R.mipmap.ic_classification_style, mContext.getString(R.string.text_function), true)
            dataSource.add(funcSpec)
            if (includeEdge) {
                dataList.add(brandSpec)
                dataList.add(funcSpec)
            } else {
                dataList.add(obtainLabelSpecSub(brandSource, ProductCatalog.DICT_BRAND_APPLIANCE,
                        R.mipmap.ic_classifiton_modelling, mContext.getString(R.string.text_brand), true))
                dataList.add(obtainLabelSpecSub(funcSource, ProductCatalog.DICT_FUNCTION_APPLIANCE,
                        R.mipmap.ic_classification_style, mContext.getString(R.string.text_function), true))
            }
        }
        setDataSource(dataSource)
        this.mDataList.clear()
        this.mDataList.addAll(dataList)
        if (selected == null || selected.isEmpty) {
            mSelected.clear()
            val selectIndex = labelSpec.obtainSpecList().indexOf(LabelSpec.Spec(dictCode, null))
            if (selectIndex != -1) {
                mSelected[ProductCatalog.DICT_CUPBOARD_TYPE] = labelSpec.obtainSpecList()[selectIndex]
            }
            for (i in 1 until mDataList.size) {
                val label = mDataList[i]
                val specList = label.specList
                if (specList.isNotEmpty()) {
                    mSelected[label.id] = specList[0]
                }
            }
            notifyDataSetChanged()
        } else {
            setSelected(selected)
        }
    }

    fun addAll(dataList: MutableList<LabelSpec>) {
        this.mDataList.clear()
        this.mDataList.addAll(dataList)
        for (label in mDataList) {
            val specList = label.specList
            if (specList.isNotEmpty()) {
                mSelected[label.id] = specList[0]
            }
        }
        notifyDataSetChanged()
    }

    fun setDataSource(data: MutableList<LabelSpec>) {
        if (mDataSource.isNotEmpty()) {
            mDataSource.clear()
        }
        mDataSource.addAll(data)
    }

    fun setOnSpecItemClickListener(l: OnSpecItemClickListener?) {
        this.mOnSpecItemClickListener = l
    }

    override fun getItemResourceId(viewType: Int): Int = R.layout.item_spec

    override fun bindViewHolder(holder: RecyclerHolder, t: LabelSpec, position: Int) {
        holder.setText(R.id.tv_name, t.name)
        holder.setDrawableLeft(R.id.tv_name, t.iconRes)
        val itemRView = holder.getView<RecyclerView>(R.id.item_rv)
        itemRView.adapter = ChildSpecListAdapter(t.id, position, mContext, t.specList)
    }

    private inner class ChildSpecListAdapter(val parentId: String?, val targetPos: Int,
                                             context: Context, dataList: MutableList<LabelSpec.Spec>)
        : CommonAdapter<LabelSpec.Spec>(context, dataList) {
        private var mOldSelectIndex = -1

        init {
            mSelected[parentId]?.let {
                mOldSelectIndex = dataList.indexOf(it)
            }
        }

        override fun getItemResourceId(viewType: Int): Int = R.layout.item_spec_flexbox

        override fun bindViewHolder(holder: RecyclerHolder, t: LabelSpec.Spec, position: Int) {
            holder.setText(R.id.tv_optional, t.name)
            if (mSelected[parentId]?.id == t.id) {
                holder.itemView.setBackgroundResource(R.mipmap.ic_chosen_sel)
            } else {
                holder.itemView.setBackgroundResource(R.mipmap.ic_unchosen_sel)
            }
            holder.itemView.setOnClickListener {
                if (t.isMore) {  //点击更多
                    mOnSpecItemClickListener?.onOpenMore(mDataSource)
                } else {
                    if (position != mOldSelectIndex) {
                        mSelected[parentId] = t
                        mOldSelectIndex = position
                        notifyDataSetChanged()
                        mOnSpecItemClickListener?.onSpecSelected(targetPos, mSelected)
                    }
                }
            }
        }
    }

    fun setSelected(map: ArrayMap<String, LabelSpec.Spec>) {
        this.mSelected = map
        notifyDataSetChanged()
    }

    fun getSelected() = mSelected

    abstract class OnSpecItemClickListener {
        open fun onOpenMore(dataSource: MutableList<LabelSpec>) {

        }

        abstract fun onSpecSelected(targetPos: Int, selected: ArrayMap<String, LabelSpec.Spec>)
    }
}