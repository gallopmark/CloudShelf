package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName
import com.holike.cloudshelf.util.ImageResizeUtils

//产品大全各模块的方案详情数据
class ProductCatalogInfoBean {
    @SerializedName("miniQrUrl")
    var miniQrUrl: String? = null //菊花码链接

    @SerializedName("info")
    var info: InfoBean? = null

    class InfoBean {
        @SerializedName("id")
        var id: String? = null //详情id

        @SerializedName("title")
        var title: String? = null //详情标题

        @SerializedName("vrUrl")
        var vrUrl: String? = null //详情vr的链接

        @SerializedName("videoPic")
        var videoPic: String? = null //详情video封面图的链接

        @SerializedName("videoUrl")
        var videoUrl: String? = null //详情视频的链接

        @SerializedName("detail")
        var detail: String? = null  //详情描述

        @SerializedName("channelDev")
        var channelDev: String? = null //详情显示终端（页面不需要

        @SerializedName("brochureImgList")
        var brochureImgList: MutableList<BrochureImgBean>? = null

        fun getImageList(): MutableList<String> {
            val images = ArrayList<String>()
            val imgList = brochureImgList
            if (!imgList.isNullOrEmpty()) {
                for (bean in imgList) {
                    images.add(bean.getImage())
                }
            }
            return images
        }

        fun getImageList(width: Int, height: Int): MutableList<String> {
            val images = ArrayList<String>()
            val imgList = brochureImgList
            if (!imgList.isNullOrEmpty()) {
                for (bean in imgList) {
                    images.add(ImageResizeUtils.resize(bean.getImage(), width, height))
                }
            }
            return images
        }

        class BrochureImgBean {
            @SerializedName("id")
            var id: String? = null

            @SerializedName("imgUrl")
            private var imageUrl: String? = null

            fun getImage(): String {
                val url = imageUrl
                return if (url.isNullOrEmpty()) "" else url
            }
        }
    }
}