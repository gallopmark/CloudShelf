package com.holike.cloudshelf.mvp.model.fragment

import com.holike.cloudshelf.bean.ProductHomeProBean
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class ProductHomeProModel : ApiModel() {
    fun getProductHomeProList(classification: String?, type: String?, callback: HttpRequestCallback<MutableList<ProductHomeProBean>>) {
        remove("homePro-list")
        put("homePro-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getProductHomeProList(classification, type), callback))
    }
}