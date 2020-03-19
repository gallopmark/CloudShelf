package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//产品大全案例数据
class ProductOptionBean {

    @SerializedName("pageTotal")
    var pageTotal: String? = null //总记录数

    var data: MutableList<DataBean>? = null

    fun getDataList(): MutableList<DataBean> {
        val dataList = data
        if (!dataList.isNullOrEmpty()) return dataList
        return ArrayList()
    }

    class DataBean {
        var id: String? = null //方案id

        @SerializedName("cloudImage")
        var cloudImage: String? = null //方案的云货架图片封面

        @SerializedName("title")
        var title: String? = null //方案标题
    }
}