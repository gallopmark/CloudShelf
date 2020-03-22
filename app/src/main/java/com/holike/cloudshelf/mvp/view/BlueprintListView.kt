package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.base.RefreshView
import com.holike.cloudshelf.bean.BlueprintBean

interface BlueprintListView : RefreshView<BlueprintBean> {
    fun onPictureItemClick(id: String?)
}