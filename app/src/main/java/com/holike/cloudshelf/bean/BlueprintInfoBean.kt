package com.holike.cloudshelf.bean

//晒晒我家详情数据
class BlueprintInfoBean {

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
}