package com.holike.cloudshelf.mvp.model.fragment

import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.ApiService
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class ProgramLibModel : ApiModel() {

    fun getTableModelHouse(roomId: String?, styleId: String?, pageNo: Int, pageSize: Int, callback: HttpRequestCallback<TableModelHouseBean>) {
        remove("table-mode-house")
        val fieldMap = HashMap<String, String>().apply {
            put("openid", nonNullWrap(PreferenceSource.getPhone()))
            put("RoomId", nonNullWrap(roomId))
            put("PStyleId", nonNullWrap(styleId))
            put("pageNo", pageNo.toString())
            put("pageSize", pageSize.toString())
        }
        put("table-mode-house", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getTableModeHouse(ApiService.TABLE_MODEL_HOUSE, fieldMap), callback))
    }

}