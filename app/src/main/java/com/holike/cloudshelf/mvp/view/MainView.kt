package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.AdvertisingBean
import pony.xcode.mvp.BaseView


interface MainView : BaseView {

    fun onAdvertisingSuccess(bean: AdvertisingBean)

    fun onClickAnimationEnd(viewId: Int)

    fun onGetVCodeSuccess(message: String?)

    fun onGetVCodeFailure(failReason: String?)

    fun onLoginSuccess(message: String?)

    fun onLoginFailure(code: Int, failReason: String?)

    fun onLogoutSuccess(message: String?)

    fun onLogoutFailure(failReason: String?)
}