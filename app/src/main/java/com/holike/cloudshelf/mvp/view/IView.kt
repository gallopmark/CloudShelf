package com.holike.cloudshelf.mvp.view

import pony.xcode.mvp.BaseView


interface IView : BaseView {
    fun onShowLoading()
    fun onDismissLoading()
}