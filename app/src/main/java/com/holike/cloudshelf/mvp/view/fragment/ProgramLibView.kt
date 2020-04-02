package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.TableModelHouseBean
import pony.xcode.mvp.BaseView


interface ProgramLibView : BaseView {
    fun onShowLoading()
    fun onDismissLoading()
    fun onTableModelHouseResponse(bean: TableModelHouseBean, isLoadMoreEnabled: Boolean)
    fun onNoQueryResults()
    fun onTableModelHouseFailure(failReason: String?, isInit: Boolean)
    //打开方案详情
    fun onOpenProgramLib(id: String?)
}