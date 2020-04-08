package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.base.RefreshView
import com.holike.cloudshelf.bean.SoughtHouseBean


interface MyHouseNearbyHouseView : RefreshView<SoughtHouseBean> {

    fun onLocationStart()
    fun onLocationSuccess(currentCity: String?)

    fun onLocationFailure(failReason: String?)
    fun onLocationEnd()

    fun onSoughtHouseClick(id: String?, name: String?)
}