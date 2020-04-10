package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.mvp.BaseView

interface IView : BaseView {
    fun onShowLoading()
    fun onDismissLoading()
}