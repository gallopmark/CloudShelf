package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.OnRequestDictListener
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.ProductHomeProBean
import com.holike.cloudshelf.bean.SystemCodeBean
import com.holike.cloudshelf.enumc.ProductCatalog
import com.holike.cloudshelf.mvp.model.fragment.ProductHomeProModel
import com.holike.cloudshelf.mvp.view.fragment.ProductHomeProView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.widget.GridSpacingItemDecoration
import pony.xcode.mvp.BasePresenter
import pony.xcode.recycler.CommonAdapter


class ProductHomeProPresenter : BasePresenter<ProductHomeProModel, ProductHomeProView>(), OnRequestDictListener {
    private inner class ProductHomeProListAdapter(context: Context, dataList: MutableList<ProductHomeProBean>)
        : CommonAdapter<ProductHomeProBean>(context, dataList) {

        //是否是定制窗帘
        private var mCurtainPro: Boolean = false
        override fun getItemViewType(position: Int): Int = if (mCurtainPro) 1 else 0

        override fun getItemResourceId(viewType: Int): Int = if (viewType == 1) R.layout.item_product_homepro_grid
        else R.layout.item_product_homepro

        override fun bindViewHolder(holder: RecyclerHolder, t: ProductHomeProBean, position: Int) {
            val itemType = holder.itemViewType
            if (itemType == 1) {
                Glide.with(mContext).load(t.getImageUrl(mImageWidth, mImageHeight)).centerCrop().into(holder.getView(R.id.iv_pic))
                holder.setText(R.id.tv_name, t.name)
                holder.itemView.setOnClickListener {
                    val selectName = mBottomDictAdapter?.getSelectName()
                    val name = "${if (selectName.isNullOrEmpty()) "" else "$selectName > "}${if (t.name.isNullOrEmpty()) "" else "${t.name}"}"
                    var title = mContext.getString(R.string.text_product_catalog)
                    if (name.isNotEmpty()) {
                        title += " > $name"
                    }
                    view?.onItemClick(null, t.id, t.classification, title, t.isShowNavigation())
                }
            } else {
                holder.setText(R.id.tv_title, t.name)
                val gridRView = holder.getView<RecyclerView>(R.id.rv_pic)
                gridRView.isNestedScrollingEnabled = false
                if (gridRView.itemDecorationCount <= 0) {
                    gridRView.addItemDecoration(GridSpacingItemDecoration(5, mContext.resources.getDimensionPixelSize(R.dimen.dp_20), false))
                }
                gridRView.adapter = ImageGridAdapter(mContext, t.obtainCategory(), t.id, t.name)
            }
        }

        fun addAll(dataList: MutableList<ProductHomeProBean>, isCurtain: Boolean) {
            mCurtainPro = isCurtain
            this.mDataList.clear()
            if (!mCurtainPro) {
                //有些只有空间 没有品类 则过滤掉
                for (bean in dataList.filter { proBean -> proBean.obtainCategory().isNotEmpty() }) {
                    this.mDataList.add(bean)
                }
            } else {
                this.mDataList.addAll(dataList)
            }
            notifyDataSetChanged()
            if (mDataList.isNotEmpty()) {
                view?.onSuccess()
            } else {
                view?.onNoQueryResults()
            }
        }

        private inner class ImageGridAdapter(context: Context, dataList: MutableList<ProductHomeProBean>,
                                             val parentId: String?, val parentName: String?)
            : CommonAdapter<ProductHomeProBean>(context, dataList) {

            override fun getItemResourceId(viewType: Int): Int = R.layout.item_product_homepro_grid

            override fun bindViewHolder(holder: RecyclerHolder, t: ProductHomeProBean, position: Int) {
                Glide.with(mContext).load(t.getImageUrl(mImageWidth, mImageHeight)).centerCrop().into(holder.getView(R.id.iv_pic))
                holder.setText(R.id.tv_name, t.name)
                holder.itemView.setOnClickListener {
                    val selectName = mBottomDictAdapter?.getSelectName()
                    val name = "${if (selectName.isNullOrEmpty()) "" else "$selectName > "}${if (parentName.isNullOrEmpty()) "" else "$parentName > "}${if (t.name.isNullOrEmpty()) "" else "${t.name}"}"
                    var title = mContext.getString(R.string.text_product_catalog)
                    if (name.isNotEmpty()) {
                        title += " > $name"
                    }
                    view?.onItemClick(parentId, t.id, t.classification, title, t.isShowNavigation())
                }
            }
        }
    }

    private class DictBean(val id: String?, val name: String?)
    private inner class BottomDictAdapter(context: Context, dataList: MutableList<DictBean>) : CommonAdapter<DictBean>(context, dataList) {
        private var mSelectPosition = 0

        override fun getItemResourceId(viewType: Int): Int = R.layout.item_product_homepro_bottom

        override fun bindViewHolder(holder: RecyclerHolder, t: DictBean, position: Int) {
            holder.setText(R.id.tv_dict, t.name)
            if (mSelectPosition == position) {
                holder.itemView.setBackgroundResource(R.color.col_launch)
            } else {
                holder.itemView.setBackgroundResource(R.color.col_27)
            }
            holder.itemView.setOnClickListener {
                if (mSelectPosition != position) {
                    setSelectPosition(position)
                    view?.onDictSelect(t.name)
                    initData()
                }
            }
        }

        fun addAll(dataList: MutableList<DictBean>) {
            this.mDataList.clear()
            this.mDataList.addAll(dataList)
            notifyDataSetChanged()
            view?.onDictSelect(getSelectName())
        }

        fun getSelectName(): String? {
            if (mSelectPosition >= 0 && mSelectPosition < mDataList.size) {
                return mDataList[mSelectPosition].name
            }
            return ""
        }

        fun getSelectId(): String? {
            if (mSelectPosition >= 0 && mSelectPosition < mDataList.size) {
                return mDataList[mSelectPosition].id
            }
            return ""
        }

        private fun setSelectPosition(position: Int) {
            this.mSelectPosition = position
            notifyDataSetChanged()
        }
    }

    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mAdapter: ProductHomeProListAdapter? = null
    private var mBottomDictAdapter: BottomDictAdapter? = null

    fun initRView(context: Context, centerRView: RecyclerView, bottomRView: RecyclerView) {
        mImageWidth = ((CurrentApp.getInstance().getMaxPixels()
                - context.resources.getDimensionPixelSize(R.dimen.dp_45) * 2
                - context.resources.getDimensionPixelSize(R.dimen.dp_20) * 4) / 5f).toInt()
        mImageHeight = mImageWidth
        mAdapter = ProductHomeProListAdapter(context, ArrayList())
        centerRView.adapter = mAdapter
        mBottomDictAdapter = BottomDictAdapter(context, ArrayList())
        bottomRView.adapter = mBottomDictAdapter
        val systemCode = CurrentApp.getInstance().getSystemCode()
        if (systemCode != null) {
            setBottomDict(systemCode)
        } else {
            CurrentApp.getInstance().getDictionary(this)
        }
    }

    //滚动显示或隐藏“顶部”按钮
    fun initScrollController(rView: RecyclerView, backTopView: View) {
        val layoutManager = rView.layoutManager as LinearLayoutManager
        rView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //获取RecyclerView当前顶部显示的第一个条目对应的索引
                val position: Int = layoutManager.findFirstVisibleItemPosition()
                //根据索引来获取对应的itemView
                layoutManager.findViewByPosition(position)?.let { firstChildView ->
                    //获取当前显示条目的高度
                    val itemHeight = firstChildView.height
                    //获取当前RecyclerView 偏移量
                    val flag = position * itemHeight - firstChildView.top
                    //注意事项：recyclerView不要设置padding
                    if (flag == 0) backTopView.visibility = View.GONE else backTopView.visibility = View.VISIBLE
                }
            }
        })
        backTopView.setOnClickListener {
            backTopView.visibility = View.GONE
            rView.scrollToPosition(0)
        }
    }

    override fun onDictSuccess(code: SystemCodeBean, message: String?) {
        setBottomDict(code)
    }

    private fun setBottomDict(code: SystemCodeBean) {
        val dataList = ArrayList<DictBean>()
        for ((k, v) in code.getHomeProClaCode()) {
            dataList.add(DictBean(k, v))
        }
        mBottomDictAdapter?.addAll(dataList)
    }

    fun initData() {
        view?.onShowLoading()
        getProductHomeProList()
    }

    private fun getProductHomeProList() {
        //业务字典中的home_pro_cla：HOME_PRO_FURNISHED/HOME_PRO_CURTAIN
        val classification = mBottomDictAdapter?.getSelectId()
        //type为0或者1，0为成品家具的空间品类集合，1为定制窗帘的品类集合
        var type: String? = null
        if (classification == ProductCatalog.DICT_HOME_PRO_FURNISHED) {
            type = "0"
        } else if (classification == ProductCatalog.DICT_HOME_PRO_CURTAIN) {
            type = "1"
        }
        mModel.getProductHomeProList(classification, type, object : HttpRequestCallback<MutableList<ProductHomeProBean>>() {
            override fun onSuccess(result: MutableList<ProductHomeProBean>, message: String?) {
                view?.onDismissLoading()
                updateProductHomePro(result, classification == ProductCatalog.DICT_HOME_PRO_CURTAIN)
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onDismissLoading()
                view?.onFailure(failReason)
            }
        })
    }

    private fun updateProductHomePro(dataList: MutableList<ProductHomeProBean>, isCurtain: Boolean) {
        mAdapter?.addAll(dataList, isCurtain)
    }
}