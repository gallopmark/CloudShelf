package com.holike.cloudshelf.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.holike.cloudshelf.R


class CustomToast {

    companion object {
        fun showToast(context: Context, text: CharSequence?, duration: Int) {
            val toast = Toast(context)
            val view = LayoutInflater.from(context)
                .inflate(R.layout.include_custom_toast, FrameLayout(context), false)
            toast.view = view
            if (!text.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.toast_view).text = text
            }
            toast.duration = duration
            toast.setMargin(0f, 0f)
            toast.setGravity(Gravity.BOTTOM or Gravity.END, 0, 0)
            toast.show()
        }
    }

}