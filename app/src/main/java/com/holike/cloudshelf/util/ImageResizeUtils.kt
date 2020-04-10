package com.holike.cloudshelf.util

//图片通过传入宽高限制大小
class ImageResizeUtils {

    companion object {
        /**
         * @param imageUrl 图片链接
         * @param width 所需图片的宽度
         * @param height 所需图片的高度
         */
        fun resize(imageUrl: String?, width: Int, height: Int): String {
            if (imageUrl.isNullOrEmpty()) {
                return ""
            }
            //url拼接参数后能改变图片的大小
            return "${imageUrl}?imageView2/1/w/${width}/h/${height}/q/100"
        }
    }
}