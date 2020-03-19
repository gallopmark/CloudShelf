package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//产品大全数据
class ProductCatalogBean {
    @SerializedName("miniQrUrl")
    var miniQrUrl: String? = null //跳转主页的菊花码链接地址

    @SerializedName("plansContents")
    private var plansContents: MutableList<PlansContentsBean>? = null

    fun getContentList(): MutableList<PlansContentsBean> {
        val data = plansContents
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class PlansContentsBean {
        @SerializedName("name")
        var name: String? = null //方案名称

        @SerializedName("id")
        var id: String? = null //方案id

        @SerializedName("templateId")
        var templateId: String? = null //模板id(注意，后面部分请求都需要携带)

        @SerializedName("pic")
        var pic: String? = null

        @SerializedName("icon")
        var icon: String? = null
    }
}