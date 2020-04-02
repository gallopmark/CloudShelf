package com.holike.cloudshelf.util

//图片通过传入宽高限制大小
class ImageResizeUtils {

    companion object {
        fun resize(imageUrl: String?, width: Int, height: Int): String {
            if (imageUrl.isNullOrEmpty()) {
                return ""
            }
            //url拼接参数后能改变图片的大小
            return "${imageUrl}?imageView2/1/w/${width}/h/${height}/q/100"
        }
    }
}