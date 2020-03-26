package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.bean.PlotTypeInfoBean
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class PlotTypeInfoModel : ApiModel() {

    fun getPlotTypeInfoById(houseTypeId: String?, callback: HttpRequestCallback<PlotTypeInfoBean>) {
        remove("plot-type-info")
        put("plot-type-info", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getHouseTypeInfoById(houseTypeId), callback))
    }
}