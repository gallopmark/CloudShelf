package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.PlotTypeInfoBean
import pony.xcode.mvp.BaseView


interface PlotTypeInfoView : BaseView {
    fun onShowLoading()
    fun onDismissLoading()
    fun onSuccess(bean: PlotTypeInfoBean)
    fun onFailure(failReason: String?)
    fun onPageSelected(position:Int,size:Int)
    fun onBottomImageSelected(position: Int)
}