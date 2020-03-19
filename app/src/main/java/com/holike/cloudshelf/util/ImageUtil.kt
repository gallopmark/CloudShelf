package com.holike.cloudshelf.util

import android.graphics.Bitmap
import android.graphics.Matrix


class ImageUtil {

    companion object {

        fun zoomBitmap(bm: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
            // 获得图片的宽高
            val width = bm.width
            val height = bm.height
            // 计算缩放比例
            val scaleWidth = reqWidth.toFloat() / width
            val scaleHeight = reqHeight.toFloat() / height
            val scale = Math.min(scaleWidth, scaleHeight)
            // 取得想要缩放的matrix参数
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            // 得到新的图片
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
        }
    }
}