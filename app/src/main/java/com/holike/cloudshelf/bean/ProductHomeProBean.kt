package com.holike.cloudshelf.bean

import com.holike.cloudshelf.util.ImageResizeUtils

//家居家品分类3.0(成品家具、定制窗帘)
class ProductHomeProBean {
    var id: String? = null
    var classification: String? = null //家居家品分类
    var name: String? = null //空间/分类名称
    var type: String? = null //空间(成品中夹带所属分类)/分类
    var space: String? = null //所属分类对应的空间id
    var orders: String? = null  //排序
    var image: String? = null //图片链接
    var showNavigation: String? = null //是否隐藏列表系列
    var category: MutableList<ProductHomeProBean>? = null

    fun isShowNavigation(): Boolean = showNavigation == "1"
    fun getImageUrl(width: Int, height: Int): String = ImageResizeUtils.resize(image, width, height)
    fun obtainCategory(): MutableList<ProductHomeProBean> {
        val list = category
        if (list.isNullOrEmpty()) return ArrayList()
        return list
    }
}