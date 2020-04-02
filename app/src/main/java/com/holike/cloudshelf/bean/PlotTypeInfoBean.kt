package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils


class PlotTypeInfoBean {
    @SerializedName("miniQrUrl")
    var miniQrUrl: String? = null //菊花码图片链接

    @SerializedName("vrUrl")
    var vrUrl: String? = null  //户型全景图vr链接

    @SerializedName("videoUrl")
    var videoUrl: String? = null  //户型视频链接

    @SerializedName("id")
    var id: String? = null

    @SerializedName("cloudImageList")
    private var imageList: MutableList<String>? = null

    fun obtainImageList(width: Int, height: Int): MutableList<String> {
        val images = imageList
        val imageList = ArrayList<String>()
        if (images.isNullOrEmpty()) return imageList
        for (url in images) {
            imageList.add(ImageResizeUtils.resize(url, width, height))
        }
        return imageList
    }
}