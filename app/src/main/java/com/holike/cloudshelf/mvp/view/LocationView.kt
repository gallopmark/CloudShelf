package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.mvp.BaseView


interface LocationView : BaseView {
    fun onLocationSuccess(bean: AMapLocationBean)
    fun onLocationFailure(failReason: String?)
}