package com.holike.cloudshelf.bean

import com.holike.cloudshelf.util.ImageResizeUtils

//晒晒我家详情数据
class BleachedHouseInfoBean {

    var address: String? = null //所在地区
    var miniQrUrl: String? = null //菊花码图片链接
    var houseType: String? = null //户型
    var deliver: String? = null //感想
    var areas: String? = null  // 面积
    var id: String? = null //晒图id
    var title: String? = null //晒图标题
    private var imageList: MutableList<String>? = null //晒图的方案图
    var budget: String? = null //响应码

    fun obtainImageList(): MutableList<String> {
        val images = imageList
        if (images.isNullOrEmpty()) return ArrayList()
        return images
    }

    fun obtainImageList(width: Int, height: Int): MutableList<String> {
        val images = imageList
        val imageList = ArrayList<String>()
        if (images.isNullOrEmpty()) return imageList
        for (url in images) {
            imageList.add(ImageResizeUtils.resize(url, width, height))
        }
        return imageList
    }
}