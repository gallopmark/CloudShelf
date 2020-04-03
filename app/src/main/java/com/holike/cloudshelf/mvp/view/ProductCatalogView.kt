package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.ProductCatalogBean

interface ProductCatalogView : IView {
    fun onResponse(bean: ProductCatalogBean)
    fun onFailure(failReason: String?)
    fun onItemClick(dictCode: String?, name: String?)
}