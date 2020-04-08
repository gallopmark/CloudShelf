package com.holike.cloudshelf.netapi

import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class CallbackHelper {
    companion object {
        fun <T> deliveryResult(observable: Observable<String?>, callBack: HttpRequestCallback<T>): Disposable {
            return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onDeliverySuccess(it, callBack) }, { onDeliveryFailure(it, callBack) }, {}, { callBack.onStart(it) })
        }

        private fun <T> onDeliverySuccess(s: String?, callBack: HttpRequestCallback<T>) {
            val type = JsonParserHelper.getSuperclassTypeParameter(callBack.javaClass)
            if (type === String::class.java) {
                @Suppress("UNCHECKED_CAST")
                callBack.onSuccess(s as T,  JsonParserHelper.getMsg(s))
            } else {
                when (val code = JsonParserHelper.getCode(s)) {
                    JsonParserHelper.SUCCESS_CODE, JsonParserHelper.DEFAULT_CODE -> {
                        val result: T? = JsonParserHelper.parseHttpJson<T>(s, type)
                        if (result == null) {
                            callBack.onFailure(code, CurrentApp.getInstance().getString(R.string.no_data_exception))
                        } else {
                            callBack.onSuccess(result, JsonParserHelper.getMsg(s))
                        }
                    }
                    JsonParserHelper.INVALID_CODE -> {
                        CurrentApp.getInstance().backToHome()
                        callBack.onFailure(code, JsonParserHelper.getMsg(s))
                    }
                    else -> {
                        callBack.onFailure(code, JsonParserHelper.getMsg(s))
                    }
                }
            }
            callBack.onCompleted()
        }

        private fun <T> onDeliveryFailure(throwable: Throwable?, callBack: HttpRequestCallback<T>) {
            callBack.onFailure(JsonParserHelper.DEFAULT_CODE, ApiException.handleException(throwable).getErrorMessage())
            callBack.onCompleted()
        }

    }
}