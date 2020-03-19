package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//极光推送数据
class JPushBean {

    @SerializedName("content")
    var content: String? = null

    @SerializedName("type")
    var type: String? = null
}