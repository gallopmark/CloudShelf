package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.AMapLocationBean
import pony.xcode.mvp.BaseView


interface LocationView : BaseView {
    fun onLocationSuccess(bean: AMapLocationBean)
    fun onLocationFailure(failReason: String?)
}