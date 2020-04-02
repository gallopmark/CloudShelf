package com.holike.cloudshelf

import com.holike.cloudshelf.bean.SystemCodeBean

//获取业务字典回调
interface OnRequestDictListener {
    fun onDictSuccess(code: SystemCodeBean, message: String?)

    fun onDictFailure(code: Int, failReason: String?) {}
}