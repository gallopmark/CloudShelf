package com.holike.cloudshelf.bean.internal

//图片展示item 上下条目展示数据
class PictureDisplayItem(var topUrl: String?, var topName: String?, var bottomUrl: String?, var bottomName: String?) {
    var topId: String? = null
    var bottomId: String? = null
    var topImages: MutableList<String>? = null
    var bottomImages: MutableList<String>? = null

    constructor(topUrl: String?, topName: String?, bottomUrl: String?, bottomName: String?,
                topImages: MutableList<String>,
                bottomImages: MutableList<String>?) : this(topUrl, topName, bottomUrl, bottomName) {
        this.topImages = topImages
        this.bottomImages = bottomImages
    }

}