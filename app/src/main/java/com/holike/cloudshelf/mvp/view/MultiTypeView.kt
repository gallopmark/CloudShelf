package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.TableModelHouseBean
import pony.xcode.mvp.BaseView


interface MultiTypeView : BaseView {

    fun onTableModelHouseResponse(bean: TableModelHouseBean, isLoadMoreEnabled: Boolean)
    fun onTableModelHouseFailure(failReason: String?, isInit: Boolean)
    fun onShowLoading()
    fun onDismissLoading()
    fun onNoQueryResults()
    fun onOpenProgramLib(id: String?)
}