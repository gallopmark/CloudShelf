package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.base.RefreshView
import com.holike.cloudshelf.bean.BleachedHouseBean

interface BleachedHouseView : RefreshView<BleachedHouseBean> {
    fun onPictureItemClick(id: String?)
}