package com.holike.cloudshelf.mvp.model.fragment

import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient
import okhttp3.RequestBody


class ProductClassifyModel : ApiModel() {

    fun getProductCatalogList(body: RequestBody, callback: HttpRequestCallback<ProductOptionBean>) {
        remove("catalog-list")
        put("catalog-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getProductCatalogList(body), callback))
    }
}