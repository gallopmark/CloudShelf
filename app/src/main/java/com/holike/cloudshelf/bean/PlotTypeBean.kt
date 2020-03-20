package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//搜搜我家户型列表数据
class PlotTypeBean {
    private var pageTotal: String? = null

    @SerializedName("data")
    private var dataList: MutableList<DataBean>? = null
    fun obtainTotal(): String {
        val total = pageTotal
        if (total.isNullOrEmpty()) return "0"
        return total
    }

    fun obtainDataList(): MutableList<DataBean> {
        val data = dataList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class DataBean {
        var area: String? = null //面积
        var houseType: String? = null //户型
        var id: String? = null //户型id
        var cloudImage: String? = null //云货架的户型图

        fun getShowName(): String {
            var name = ""
            houseType?.let { name += "$it\u2000" }
            area?.let { name += "$it ㎡" }
            return name
        }
    }
}