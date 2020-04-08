package com.holike.cloudshelf.mvp.view.fragment

import com.holike.cloudshelf.mvp.view.IView


interface ProductHomeProView : IView {
    fun onDictSelect(name: String?)
    fun onSuccess()
    fun onNoQueryResults()
    fun onFailure(failReason: String?)
    fun onItemClick(parentId: String?, id: String?, classification: String?, title: String?, isShowNavigation: Boolean)
}