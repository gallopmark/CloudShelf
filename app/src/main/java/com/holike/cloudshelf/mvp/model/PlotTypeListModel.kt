package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.bean.PlotTypeBean
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class PlotTypeListModel : ApiModel() {

    fun getPlotTypeList(id: String?, pageNo: Int, pageSize: Int, callback: HttpRequestCallback<PlotTypeBean>) {
        remove("plot-type-list")
        put("plot-type-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getHouseTypeById(pageNo.toString(), pageSize.toString(), id), callback))
    }
}