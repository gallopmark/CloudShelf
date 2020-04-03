package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.PlotTypeInfoBean


interface PlotTypeInfoView : IView {
    fun onSuccess(bean: PlotTypeInfoBean)
    fun onFailure(failReason: String?)
    fun onPageSelected(position:Int,size:Int)
    fun onBottomImageSelected(position: Int)
}