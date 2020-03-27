package com.holike.cloudshelf.base

import android.text.Editable
import android.text.TextWatcher

//EditText输入监听器，减少实现无用方法代码
open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}