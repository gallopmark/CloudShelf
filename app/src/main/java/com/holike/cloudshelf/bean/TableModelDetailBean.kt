package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

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
}