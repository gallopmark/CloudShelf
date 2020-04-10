package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.bean.ProductCatalogInfoBean
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.mvp.view.IView


interface ContentInfoView : IView {
    fun onTableModelResp(bean: TableModelDetailBean)

    fun onProductCatalogResp(bean: ProductCatalogInfoBean)

    fun onRequestSuccess()

    fun onFailure(failReason: String?)

    fun onPageSelected(position: Int, size: Int)

    fun onBottomImageSelected(position: Int)
}