package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName


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

    fun obtainImageList(): MutableList<String> {
        val images = imageList
        if (images.isNullOrEmpty()) return ArrayList()
        return images
    }
}