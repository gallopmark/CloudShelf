package com.holike.cloudshelf.adapter

import android.content.Context
import android.text.TextUtils
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.internal.LabelSpec
import pony.xcode.recycler.CommonAdapter


class SpecListAdapter : CommonAdapter<LabelSpec> {
    constructor(context: Context, dataList: MutableList<LabelSpec>) : super(context, dataList)

    constructor(context: Context, dataList: MutableList<LabelSpec>, selected: ArrayMap<String, LabelSpec.Spec>)
            : this(context, dataList) {
        this.mSelected = selected
    }

    private val mDataSource = ArrayList<LabelSpec>()
    private var mSelected = ArrayMap<String, LabelSpec.Spec>()
    private var mOnSpecItemClickListener: OnSpecItemClickListener? = null

    fun addAll(dataList: MutableList<LabelSpec>) {
        this.mDataList.clear()
        this.mDataList.addAll(dataList)
        for (label in dataList) {
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
        itemRView.adapter = ChildSpecListAdapter(t.id, mContext, t.specList)
    }

    private inner class ChildSpecListAdapter(val id: String?, context: Context, dataList: MutableList<LabelSpec.Spec>)
        : CommonAdapter<LabelSpec.Spec>(context, dataList) {

        override fun getItemResourceId(viewType: Int): Int = R.layout.item_spec_flexbox

        override fun bindViewHolder(holder: RecyclerHolder, t: LabelSpec.Spec, position: Int) {
            holder.setText(R.id.tv_optional, t.name)
            if (TextUtils.equals(mSelected[id]?.id, t.id)) {
                holder.itemView.setBackgroundResource(R.mipmap.ic_chosen_sel)
            } else {
                holder.itemView.setBackgroundResource(R.mipmap.ic_unchosen_sel)
            }
            holder.itemView.setOnClickListener {
                if (t.isMore) {  //点击更多
                    mOnSpecItemClickListener?.onOpenMore(mDataSource)
                } else {
                    mSelected[id] = t
                    notifyDataSetChanged()
                    mOnSpecItemClickListener?.onSpecSelected(mSelected)
                }
            }
        }
    }

    fun setSelected(map: ArrayMap<String, LabelSpec.Spec>) {
        this.mSelected = map
        notifyDataSetChanged()
    }

    fun getSelected() = mSelected

    interface OnSpecItemClickListener {
        fun onOpenMore(dataSource: MutableList<LabelSpec>)
        fun onSpecSelected(map: ArrayMap<String, LabelSpec.Spec>)
    }
}