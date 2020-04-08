package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.base.RefreshView
import com.holike.cloudshelf.bean.PlotTypeBean


interface PlotTypeListView : RefreshView<PlotTypeBean> {

    fun onItemClick(id: String?)
}