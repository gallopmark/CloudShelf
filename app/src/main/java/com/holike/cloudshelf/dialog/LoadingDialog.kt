package com.holike.cloudshelf.dialog

import android.content.Context
import com.holike.cloudshelf.R
import pony.xcode.base.CommonDialog

class LoadingDialog(context: Context?) : CommonDialog(context, R.style.LoadingDialogStyle) {

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }

    override fun getLayoutResourceId(): Int = R.layout.dialog_loading
}