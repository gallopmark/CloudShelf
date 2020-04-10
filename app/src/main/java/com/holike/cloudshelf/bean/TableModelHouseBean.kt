package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//3D方案库数据
class TableModelHouseBean {
    var total: String? = null

    @SerializedName("dataList")
    private var dataList: MutableList<DataBean>? = null

    @SerializedName("ProductStyle")
    private var styleList: MutableList<ProductStyleBean>? = null

    @SerializedName("ModelHouseType")
    private var spaceList: MutableList<ModelHouseTypeBean>? = null

    fun getData(): MutableList<DataBean> {
        val data = dataList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    fun getStyleBeans(): MutableList<ProductStyleBean> {
        val data = styleList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    fun getSpaceBeans(): MutableList<ModelHouseTypeBean> {
        val data = spaceList
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class DataBean {
//        @SerializedName("ViewUrl")
//        var viewUrl: String? = null  // 全景图url
//
//        @SerializedName("click")
//        var click: String? = null  //点击次数
//
//        @SerializedName("pName")
//        var pName: String? = null //风格名
//
//        @SerializedName("UpTime")
//        var upTime: String? = null //创建时间

        @SerializedName("ID")
        var id: String? = null //案例id

//        @SerializedName("hNanme")
//        var spaceName: String? = null //空间名
//        var Memo: String? = null

        @SerializedName("Name")
        var name: String? = null
        var imageList: MutableList<String>? = null

        fun obtainImages(): MutableList<String> {
            val images = imageList
            if (images.isNullOrEmpty()) return ArrayList()
            return images
        }

        //获取第一张图片
        fun getFirstImage():String?{
            val images = obtainImages()
            if(images.isNotEmpty()){
                return images[0]
            }
            return null
        }
    }

    //风格列表数据
    class ProductStyleBean {
        @SerializedName("ID")
        var id: String? = null

        @SerializedName("Name")
        var name: String? = null
    }

    //空间列表数据
    class ModelHouseTypeBean {
        @SerializedName("ID")
        var id: String? = null

        @SerializedName("Name")
        var name: String? = null
    }


}