package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//首页广告位数据
class AdvertisingBean {
    @SerializedName("videoUrl")
    var videoUrl: String? = null //广告位视频链接

    @SerializedName("id")
    var id: String? = null  //广告id

    @SerializedName("pic")
    var pic: String? = null //广告图片链接

    @SerializedName("title")
    var title: String? = null //广告标题

    @SerializedName("videoPic")
    var videoPic: String? = null //视频缩略图
}