package com.holike.cloudshelf.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

class SystemTintHelper {
    companion object {

        /*获取状态栏高度*/
        @Suppress("unused")
        fun getStatusBarHeight(context: Context): Int {
            return try {
                val resources = context.resources
                val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                resources.getDimensionPixelSize(resourceId)
            } catch (e: Exception) {
                0
            }
        }

        //设置全屏  
        fun setFullscreen(activity: Activity) {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /*取消全屏*/
//        fun clearFullscreen(activity: Activity) {
//            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        }

        /*全屏透明沉浸状态栏*/
        fun setTransparentStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val window = activity.window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                    val decorView = window.decorView
                    //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                    val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    decorView.systemUiVisibility = option
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.TRANSPARENT
                } else {
                    //让contentView延伸到状态栏并且设置状态栏颜色透明
                    val attributes = window.attributes
                    attributes.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    window.attributes = attributes
                }
            }
        }
    }
}