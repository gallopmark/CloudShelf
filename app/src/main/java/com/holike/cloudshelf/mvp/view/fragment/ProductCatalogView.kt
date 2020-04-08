package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.ProductCatalogBean
import com.holike.cloudshelf.mvp.view.IView

interface ProductCatalogView : IView {
    fun onResponse(bean: ProductCatalogBean)
    fun onFailure(failReason: String?)
    fun onItemClick(dictCode: String?, name: String?)
}