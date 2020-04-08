package com.holike.cloudshelf.helper

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.SimpleTextWatcher

//EditText输入清除按钮
class ClearEditTextHelper {
    interface TextChangeListener {
        fun textChanged(isEmpty: Boolean)
    }

    companion object {
        @SuppressLint("ClickableViewAccessibility")
        fun setTargetView(edt: EditText, listener: TextChangeListener?) {
            edt.addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        listener?.textChanged(true)
                        setIconChange(edt, 0)
                    } else {
                        setIconChange(edt, R.mipmap.ic_clear)
                        listener?.textChanged(false)
                    }
                }
            })
            edt.setOnTouchListener { _: View?, event: MotionEvent ->
                val action = event.action
                val drawableRight: Drawable? = edt.compoundDrawables[2]
                when (action) {
                    MotionEvent.ACTION_UP -> if (drawableRight != null && !TextUtils.isEmpty(edt.text)) {
                        val touchable = (event.x > edt.width - edt.totalPaddingRight && event.x < edt.width - edt.paddingRight
                                && event.y > 0 && event.y < edt.height)
                        if (touchable) {
                            edt.setText("")
                        }
                    }
                }
                false
            }
        }

        private fun setIconChange(editText: EditText, resId: Int) {
            val context = editText.context
            val clearDrawable = if (resId == 0) null else ContextCompat.getDrawable(context, resId)
            val size = context.resources.getDimensionPixelSize(R.dimen.dp_20)
            clearDrawable?.setBounds(0, 0, size, size)
            editText.setCompoundDrawables(editText.compoundDrawables[0],
                    editText.compoundDrawables[1], clearDrawable, editText.compoundDrawables[3])
        }
    }
}