package com.holike.cloudshelf.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.internal.PictureDisplayItem
import pony.xcode.recycler.lib.CommonAdapter

//图片列表适配器
class PictureDisplayAdapter(context: Context, dataList: MutableList<PictureDisplayItem>, private val itemWidth: Int)
    : CommonAdapter<PictureDisplayItem>(context, dataList) {
    private var mOnPictureItemClickListener: OnPictureItemClickListener? = null

    fun setOnPictureItemClickListener(l: OnPictureItemClickListener) {
        this.mOnPictureItemClickListener = l
    }

    override fun getItemResourceId(viewType: Int): Int = R.layout.item_reused

    override fun bindViewHolder(holder: RecyclerHolder, t: PictureDisplayItem, position: Int) {
        val lp = holder.itemView.layoutParams as RecyclerView.LayoutParams
        lp.width = itemWidth
        holder.itemView.layoutParams = lp
        Glide.with(mContext).load(t.topUrl).placeholder(R.color.col_03).centerCrop().into(holder.getView(R.id.iv_pic_top))
        holder.setText(R.id.tv_name_top, t.topName)
        Glide.with(mContext).load(t.bottomUrl).placeholder(R.color.col_03).centerCrop().into(holder.getView(R.id.iv_pic_bottom))
        holder.setText(R.id.tv_name_bottom, t.bottomName)
        holder.setOnClickListener(R.id.top_layout) {
            if (!t.topId.isNullOrEmpty()) {
                mOnPictureItemClickListener?.onPictureItemClick(t.topId, t.topImages)
            }
        }
        holder.setOnClickListener(R.id.bottom_layout) {
            if (!t.bottomId.isNullOrEmpty()) {
                mOnPictureItemClickListener?.onPictureItemClick(t.bottomId, t.bottomImages)
            }
        }
    }

    interface OnPictureItemClickListener {
        fun onPictureItemClick(id: String?, images: MutableList<String>?)
    }
}