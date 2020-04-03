package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.mvp.view.IView


interface ProgramLibView : IView {
    fun onTableModelHouseResponse(bean: TableModelHouseBean, isLoadMoreEnabled: Boolean)
    fun onNoQueryResults()
    fun onTableModelHouseFailure(failReason: String?, isInit: Boolean)
    //打开方案详情
    fun onOpenProgramLib(id: String?)
}