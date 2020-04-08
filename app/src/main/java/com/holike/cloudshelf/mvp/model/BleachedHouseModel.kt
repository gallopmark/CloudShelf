package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.bean.BleachedHouseBean
import com.holike.cloudshelf.bean.BleachedHouseInfoBean
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class BleachedHouseModel : ApiModel() {

    //晒晒好晒图家列表查询(分页)
    fun getBleachedHouse(pageNo: Int, pageSize: Int, callback: HttpRequestCallback<BleachedHouseBean>) {
        remove("blueprint-list")
        put("blueprint-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getBlueprintList(pageNo.toString(), pageSize.toString()), callback))
    }

    //晒晒好家的晒图详情查询
    fun getBleachedHouseInfo(blueprintId: String?, callback: HttpRequestCallback<BleachedHouseInfoBean>) {
        remove("blueprint-info")
        put("blueprint-info", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getBlueprintInfo(blueprintId), callback))
    }
}