package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils

//方案库详情数据
class TableModelDetailBean {
    @SerializedName("miniqrQr")
    var miniQrCode: String? = null //二维码

    @SerializedName("ViewUrl")
    var viewUrl: String? = null //全景url

    @SerializedName("ID")
    var id: String? = null //id

    @SerializedName("imageList")
    var imageList: MutableList<String>? = null

    @SerializedName("title")
    var title: String? = null

    fun obtainImages(): MutableList<String> {
        val images = imageList
        if (images.isNullOrEmpty()) return ArrayList()
        return images
    }

    fun obtainImages(width: Int, height: Int): MutableList<String> {
        val images = imageList
        val imageList = ArrayList<String>()
        if (images.isNullOrEmpty()) return imageList
        for (url in images) {
            imageList.add(ImageResizeUtils.resize(url, width, height))
        }
        return imageList
    }
}