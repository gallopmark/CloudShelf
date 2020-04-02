package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.ProductCatalogBean
import pony.xcode.mvp.BaseView


interface ProductCatalogView : BaseView {
    fun onResponse(bean: ProductCatalogBean)
    fun onFailure(failReason: String?)
    fun onItemClick(dictCode: String?, name: String?)
}