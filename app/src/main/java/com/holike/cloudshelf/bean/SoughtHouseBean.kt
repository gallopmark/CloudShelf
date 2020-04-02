package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils

//搜搜我家数据
class SoughtHouseBean {
    @SerializedName("pageTotal")
    var pageTotal: String? = null //查询的总小区数

    @SerializedName("currentCity")
    var currentCity: String? = null //当前城市

    @SerializedName("data")
    private var dataList: MutableList<DataBean>? = null

    fun obtainDataList(): MutableList<DataBean> {
        val data = dataList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class DataBean {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null  //小区名称

        @SerializedName("city")
        var city: String? = null //城市

        @SerializedName("address")
        var address: String? = null //详细地址

        @SerializedName("cloudImage")
        var image: String? = null  //小区图片

        //http://img.zgsta.com/16-2-1-2-22866/44ca347e1a4a4e6519584e21119a905c_nofinger.jpg?imageView2/1/w/2550/h/1900/q/100
        fun getResizeImage(width: Int, height: Int): String? = ImageResizeUtils.resize(image, width, height)
    }
}