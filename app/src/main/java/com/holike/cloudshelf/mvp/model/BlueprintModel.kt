package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.bean.BlueprintBean
import com.holike.cloudshelf.bean.BlueprintInfoBean
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class BlueprintModel : ApiModel() {

    //晒晒好晒图家列表查询(分页)
    fun getBlueprintList(pageNo: Int, pageSize: Int, callback: HttpRequestCallback<BlueprintBean>) {
        remove("blueprint-list")
        put("blueprint-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getBlueprintList(pageNo.toString(), pageSize.toString()), callback))
    }

    //晒晒好家的晒图详情查询
    fun getBlueprintInfo(blueprintId: String?, callback: HttpRequestCallback<BlueprintInfoBean>) {
        remove("blueprint-info")
        put("blueprint-info", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getBlueprintInfo(blueprintId), callback))
    }
}