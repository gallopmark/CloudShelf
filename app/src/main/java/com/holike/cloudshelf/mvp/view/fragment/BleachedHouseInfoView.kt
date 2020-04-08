package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.BleachedHouseInfoBean
import com.holike.cloudshelf.mvp.view.IView


interface BleachedHouseInfoView : IView {

    fun onPageSelected(position: Int, size: Int)

    fun onBottomImageSelected(position: Int)

    fun onSuccess(bean: BleachedHouseInfoBean)

    fun onFailure(failReason: String?)
}