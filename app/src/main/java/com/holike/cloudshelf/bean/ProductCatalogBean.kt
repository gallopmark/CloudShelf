package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.R
import com.holike.cloudshelf.enumc.ProductCatalog

//产品大全数据
class ProductCatalogBean {
    @SerializedName("miniQrUrl")
    var miniQrUrl: String? = null //跳转主页的菊花码链接地址

    @SerializedName("moduleContents")
    private var moduleContents: MutableList<PlansContentsBean>? = null

    fun getModuleContentList(): MutableList<PlansContentsBean> {
        val data = moduleContents
        if (data.isNullOrEmpty()) return ArrayList()
        return data
    }

    class PlansContentsBean {
        @SerializedName("name")
        var name: String? = null //方案名称

        @SerializedName("id")
        var id: String? = null //方案id

        @SerializedName("dictCode")
        var dictCode: String? = null  //各模块对应的字典码

        @SerializedName("pic")
        var pic: String? = null

        @SerializedName("icon")
        var icon: String? = null

        fun getBackgroundPic(): Int {
            return when (dictCode) {
                ProductCatalog.WHOLE_HOUSE -> {
                    R.mipmap.pic_products_wholehome
                }
                ProductCatalog.AMBRY -> {
                    R.mipmap.pic_products_cupboards
                }
                ProductCatalog.DOOR -> {
                    R.mipmap.pic_products_wodeendoors
                }
                else -> R.mipmap.pic_products_finishedprod
            }
        }

        fun getPlaceholderIcon(): Int {
            return when (dictCode) {
                ProductCatalog.WHOLE_HOUSE -> {
                    R.mipmap.ic_products_wholehome
                }
                ProductCatalog.AMBRY -> {
                    R.mipmap.ic_products_cupboards
                }
                ProductCatalog.DOOR -> {
                    R.mipmap.ic_products_wodeendoors
                }
                else -> R.mipmap.ic_products_finishedprod
            }
        }
    }
}