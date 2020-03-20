package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.SoughtHouseBean
import pony.xcode.mvp.BaseView


interface SoughtHouseView : BaseView {

    fun onLocationStart()
    fun onLocationSuccess(currentCity: String?)

    fun onLocationFailure(failReason: String?)
    fun onLocationEnd()

    fun onSearchSuccess(bean: SoughtHouseBean, isLoadMoreEnabled: Boolean)
    fun onNoResults()
    fun onSearchFailure(failReason: String?, isShowError: Boolean)

    fun onSoughtHouseClick(id: String?, name: String?)
}