package com.holike.cloudshelf.mvp.view

import com.holike.cloudshelf.bean.TableModelDetailBean
import pony.xcode.mvp.BaseView


interface GeneralView : BaseView {
    fun onTableModelResponse(bean: TableModelDetailBean)
    fun onTableModelFailure(failReason: String?)

    fun onPageSelected(position: Int, size: Int)

    fun onBottomImageSelected(position: Int)
}