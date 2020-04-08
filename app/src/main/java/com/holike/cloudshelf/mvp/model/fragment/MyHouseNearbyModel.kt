package com.holike.cloudshelf.mvp.model.fragment

import android.text.TextUtils
import com.google.gson.Gson
import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.bean.SoughtHouseBean
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.JsonParserHelper
import com.holike.cloudshelf.netapi.NetClient


class MyHouseNearbyModel : ApiModel() {

    fun onLocationEngine(callback: HttpRequestCallback<AMapLocationBean>) {
        remove("location")
        put("location", doRequest(NetClient.getInstance().getNetApi().getLocationInfo(BuildConfig.AMAP_URL), object : HttpRequestCallback<String>() {
            override fun onSuccess(result: String, message: String?) {
                try {
                    val bean = Gson().fromJson(result, AMapLocationBean::class.java)
                    if (TextUtils.equals(bean.status, "1")) { //返回结果状态值 值为0或1,0表示失败；1表示成功
                        //返回状态说明，status为0时，info返回错误原因，否则返回“OK”。
                        callback.onSuccess(bean, null)
                    } else {
                        callback.onFailure(JsonParserHelper.DEFAULT_CODE, bean.info)
                    }
                } catch (e: Exception) {
                    callback.onFailure(JsonParserHelper.DEFAULT_CODE, CurrentApp.getInstance().getString(R.string.json_parse_exception))
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