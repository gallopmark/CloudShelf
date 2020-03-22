package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.base.RefreshView
import com.holike.cloudshelf.bean.SoughtHouseBean


interface SoughtHouseView : RefreshView<SoughtHouseBean> {

    fun onLocationStart()
    fun onLocationSuccess(currentCity: String?)

    fun onLocationFailure(failReason: String?)
    fun onLocationEnd()

    fun onSoughtHouseClick(id: String?, name: String?)
}