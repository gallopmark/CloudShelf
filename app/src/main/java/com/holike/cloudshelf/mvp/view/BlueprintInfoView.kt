package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.BlueprintInfoBean
import pony.xcode.mvp.BaseView


interface BlueprintInfoView : IView {

    fun onPageSelected(position: Int, size: Int)

    fun onBottomImageSelected(position: Int)

    fun onSuccess(bean: BlueprintInfoBean)

    fun onFailure(failReason: String?)
}