package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils

//晒晒我家数据
class BlueprintBean {

    var pageTotal: String? = null //总记录数

    @SerializedName("data")
    private var dataList: MutableList<DataBean>? = null

    fun obtainDataList(): MutableList<DataBean> {
        val data = dataList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class DataBean {
        @SerializedName("id")
        var id: String? = null //晒图id
        var title: String? = null  //晒图标题
        private var image: String? = null //晒图展示图(url):上传位置在第一的图

        fun getImageUrl(width: Int, height: Int): String? = ImageResizeUtils.resize(image, width, height)
    }
}