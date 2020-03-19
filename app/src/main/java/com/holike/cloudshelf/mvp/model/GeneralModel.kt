package com.holike.cloudshelf.mvp.model

import com.google.gson.Gson
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.MyJsonParser
import com.holike.cloudshelf.netapi.NetClient


class GeneralModel : ApiModel() {

    fun getTableModelDetail(id: String?, callback: HttpRequestCallback<TableModelDetailBean>) {
        remove("table-model-detail")
        put("table-model-detail", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getTableModel(id, PreferenceSource.getPhone()), object : HttpRequestCallback<String>() {
            override fun onSuccess(result: String, message: String?) {
                try {
                    val bean = Gson().fromJson(result, TableModelDetailBean::class.java)
                    callback.onSuccess(bean, null)
                } catch (e: Exception) {
                    callback.onFailure(MyJsonParser.DEFAULT_CODE,
                            CurrentApp.getInstance().getString(R.string.json_parse_exception))
                }
            }

            override fun onFailure(code: Int, failReason: String?) {
                callback.onFailure(code, failReason)
            }
        }))
    }
}