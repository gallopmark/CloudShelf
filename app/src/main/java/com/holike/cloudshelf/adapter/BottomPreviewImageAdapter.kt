package com.holike.cloudshelf.adapter

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import pony.xcode.recycler.CommonAdapter

//页面底部图片预览与选择适配器
class BottomPreviewImageAdapter(context: Context, images: MutableList<String>)
    : CommonAdapter<String>(context, images) {

    private var mSelectPosition: Int = 0
    private val selectWidth = context.resources.getDimensionPixelSize(R.dimen.dp_130)
    private val selectHeight = (selectWidth * 0.68f).toInt()
    private val unSelectWidth = context.resources.getDimensionPixelSize(R.dimen.dp_100)
    private val unSelectHeight = (unSelectWidth * 0.68f).toInt()
    private var mOnImageSelectListener: OnImageSelectListener? = null

    fun setOnImageSelectListener(l: OnImageSelectListener?) {
        this.mOnImageSelectListener = l
    }

    fun setSelectPosition(position: Int) {
        mSelectPosition = position
        notifyDataSetChanged()
        mOnImageSelectListener?.onImageSelect(position)
    }

    override fun getItemResourceId(viewType: Int): Int = R.layout.item_picture_preview_bottom
    override fun bindViewHolder(holder: RecyclerHolder, t: String, position: Int) {
        val pictureIView = holder.getView<ImageView>(R.id.iv_pic)
        val lp = pictureIView.layoutParams as FrameLayout.LayoutParams
        if (mSelectPosition == position) {
            lp.width = selectWidth
            lp.height = selectHeight
        } else {
            lp.width = unSelectWidth
            lp.height = unSelectHeight
        }
        pictureIView.layoutParams = lp
        Glide.with(mContext).load(t).centerCrop().into(holder.getView(R.id.iv_pic))
        holder.itemView.setOnClickListener { setSelectPosition(position) }
    }

    interface OnImageSelectListener {
        fun onImageSelect(position: Int)
    }
}