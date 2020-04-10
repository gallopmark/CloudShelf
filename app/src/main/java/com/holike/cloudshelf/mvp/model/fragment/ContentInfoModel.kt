package com.holike.cloudshelf.mvp.model.fragment

import com.google.gson.Gson
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.ProductCatalogInfoBean
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.*
import io.reactivex.disposables.Disposable


class ContentInfoModel : ApiModel() {

    /*方案库详情*/
    fun getTableModelDetail(id: String?, callback: HttpRequestCallback<TableModelDetailBean>) {
        remove("table-model-detail")
        put("table-model-detail", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi()
                .getTableModel(ApiService.TABLE_MODEL, id, PreferenceSource.getPhone()), object : HttpRequestCallback<String>() {
            override fun onStart(d: Disposable?) {
                callback.onStart(d)
            }

            override fun onSuccess(result: String, message: String?) {
                val bean: TableModelDetailBean
                try {
                    bean = Gson().fromJson(result, TableModelDetailBean::class.java)
                } catch (e: Exception) {
                    callback.onFailure(JsonParserHelper.DEFAULT_CODE, CurrentApp.getInstance().getString(R.string.json_parse_exception))
                    return
                }
                if (bean == null) {
                    callback.onFailure(JsonParserHelper.DEFAULT_CODE, CurrentApp.getInstance().getString(R.string.no_data_exception))
                } else {
                    callback.onSuccess(bean, null)
                }
            }

            override fun onFailure(code: Int, failReason: String?) {
                callback.onFailure(code, failReason)
            }

            override fun onCompleted() {
                callback.onCompleted()
            }
        }))
    }

    //产品大全模块详情
    fun getProductCatalogInfo(id: String?, callback: HttpRequestCallback<ProductCatalogInfoBean>) {
        remove("product-catalog-info")
        put("product-catalog-info", CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getProductCatalogInfo(id), callback))
    }
}