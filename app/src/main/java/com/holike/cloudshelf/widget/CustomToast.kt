package com.holike.cloudshelf.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.holike.cloudshelf.R

//自定义toast 默认显示在右下角
class CustomToast {

    companion object {
        const val DEFAULT_GRAVITY = Gravity.BOTTOM or Gravity.END
        //获取toast实例
        fun obtain(context: Context, text: CharSequence?, duration: Int, gravity: Int): Toast {
            val toast = Toast(context)
            val view = LayoutInflater.from(context)
                .inflate(R.layout.include_custom_toast, FrameLayout(context), false)
            toast.view = view
            if (!text.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.toast_view).text = text
            }
            toast.duration = duration
            toast.setMargin(0f, 0f)
            toast.setGravity(gravity, 0, 0)
            return toast
        }

        //显示toast
        fun showToast(context: Context, text: CharSequence?, duration: Int) {
            obtain(context, text, duration, DEFAULT_GRAVITY).show()
        }
    }

}