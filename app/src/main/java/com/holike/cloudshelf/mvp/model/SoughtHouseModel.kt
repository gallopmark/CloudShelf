package com.holike.cloudshelf.mvp.model

import com.google.gson.Gson
import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.MyJsonParser
import com.holike.cloudshelf.netapi.NetClient


class SoughtHouseModel : ApiModel() {

    fun onLocationEngine(callback: HttpRequestCallback<AMapLocationBean>) {
        remove("location")
        put("location", doRequest(NetClient.getInstance().getNetApi().getLocationInfo(BuildConfig.AMAP_URL), object : HttpRequestCallback<String>() {
            override fun onSuccess(result: String, message: String?) {
                try {
                    val bean = Gson().fromJson(result, AMapLocationBean::class.java)
                    callback.onSuccess(bean, null)
                } catch (e: Exception) {
                    callback.onFailure(MyJsonParser.DEFAULT_CODE, CurrentApp.getInstance().getString(R.string.json_parse_exception))
                }
            }

            override fun onFailure(code: Int, failReason: String?) {
                callback.onFailure(code, failReason)
            }
        })
        )
    }

    fun doSearchHouseList(currentCity: String?, content: String?, pageNo: String, pageSize: String, callback: HttpRequestCallback<SoughtHouseBean>) {
        remove("search-list")
        put("search-list", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getCommunityList(currentCity, content, pageNo, pageSize), callback))
    }
}