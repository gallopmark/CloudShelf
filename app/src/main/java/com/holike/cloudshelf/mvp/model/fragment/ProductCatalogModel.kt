package com.holike.cloudshelf.mvp.model.fragment

import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class ProductCatalogModel : ApiModel() {

    fun getPlanContentList(callback: HttpRequestCallback<ProductCatalogBean>) {
        request(NetClient.getInstance().getNetApi().getPlanContentList(), callback)
    }
}