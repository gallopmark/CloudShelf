package com.holike.cloudshelf.mvp.presenter

import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.mvp.model.SoughtHouseModel
import com.holike.cloudshelf.mvp.view.LocationView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import pony.xcode.mvp.BasePresenter


class LocationPresenter : BasePresenter<SoughtHouseModel, LocationView>() {

    fun onLocate() {
        mModel.onLocationEngine(object : HttpRequestCallback<AMapLocationBean>() {
            override fun onSuccess(result: AMapLocationBean, message: String?) {
                view?.onLocationSuccess(result)
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onLocationFailure(failReason)
            }
        })
    }
}