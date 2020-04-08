package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils

//产品大全案例数据
class ProductOptionBean {

    @SerializedName("totalCount")
    var totalCount: String? = null //总记录数

    @SerializedName("list")
    var dataList: MutableList<DataBean>? = null

    fun obtainDataList(): MutableList<DataBean> {
        val dataList = dataList
        if (!dataList.isNullOrEmpty()) return dataList
        return ArrayList()
    }

    class DataBean {
        @SerializedName("id")
        var id: String? = null //方案id

        @SerializedName("brochureType")
        var brochureType: String? = null //方案对应的方案类型

        @SerializedName("image")
        var image: String? = null //方案的云货架图片封面

        @SerializedName("title")
        var title: String? = null //方案标题

        fun getImageUrl(width: Int, height: Int): String {
            return ImageResizeUtils.resize(image, width, height)
        }
    }
}