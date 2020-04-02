package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.bean.ProductOptionBean
import com.holike.cloudshelf.bean.ProductSpecBean
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.netapi.ApiService
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class MultiTypeModel : ApiModel() {

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

    /*获取产品大全标签列表*/
    fun getProductSpecList(templateId: String?, callback: HttpRequestCallback<ProductSpecBean>) {
        remove("product-spec")
        put("product-spec", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getProductSpecList(templateId), callback))
    }

    /*产品大全各模块的方案列表(分页)*/
    fun getProductOptionsList(templateId: String?, optionIds: String?, pageNo: Int, pageSize: Int, callback: HttpRequestCallback<ProductOptionBean>) {
        remove("product-options")
        put("product-options", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getProductOptionList(pageNo.toString(), pageSize.toString(), templateId, optionIds), callback))
    }
}