package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.BlueprintInfoBean
import pony.xcode.mvp.BaseView


interface BlueprintInfoView : BaseView {

    fun onPageSelected(position: Int, size: Int)

    fun onBottomImageSelected(position: Int)

    fun onShowLoading()

    fun onDismissLoading()

    fun onSuccess(bean: BlueprintInfoBean)

    fun onFailure(failReason: String?)
}