package com.holike.cloudshelf.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BasePagerAdapter
import com.holike.cloudshelf.util.LogCat


/**
 * @param resourceWidth 指定窗口的宽度
 * @param resourceHeight 指定窗口的高度
 */
class PicturePagerAdapter(context: Context, images: MutableList<String>,
                          val resourceWidth: Int, val resourceHeight: Int)
    : BasePagerAdapter<String>(context, images) {
    override fun getLayoutResourceId(): Int = R.layout.item_picture_adjustviewbounds

    override fun convert(convertView: View, bean: String, position: Int) {
        val imageView = convertView.findViewById<ImageView>(R.id.iv_pic)
        Glide.with(context).asDrawable().load(dataList[position]).into(object : CustomTarget<Drawable>() {

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                imageView.setImageDrawable(resource)
                //获取图片资源的宽
                val photoWidth = resource.intrinsicWidth
                //获取图片资源的高
                val photoHeight = resource.intrinsicHeight
                LogCat.e("drawable", "width:${photoWidth};height:${photoHeight}")
                val imageWidth: Int
                val imageHeight: Int
                if (photoWidth < resourceWidth && photoHeight < resourceHeight) {
                    //如果图片实际宽高均小于窗口的宽高
                    imageWidth = photoWidth
                    imageHeight = photoHeight
                } else {
                    //图片宽高比
                    val pScale = photoWidth.toDouble() / photoHeight.toDouble()
                    //指定窗口的比例
                    val scale = resourceWidth.toDouble() / resourceHeight.toDouble()
                    LogCat.e("scale", "pScale:$pScale;scale:${scale}")
                    if (pScale > scale) {
                        imageWidth = resourceWidth
                        imageHeight = (imageWidth / pScale).toInt()
                    } else {
                        imageHeight = resourceHeight
                        imageWidth = (imageHeight * pScale).toInt()
                    }
                }
                val lp = imageView.layoutParams as FrameLayout.LayoutParams
                lp.width = imageWidth
                lp.height = imageHeight
                imageView.layoutParams = lp
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }
}