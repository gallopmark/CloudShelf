package com.holike.cloudshelf.netapi

import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class CallbackHelper {
    companion object {
        fun <T> deliveryResult(observable: Observable<String?>, callback: HttpRequestCallback<T>): Disposable {
            return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onDeliverySuccess(it, callback) },
                            { onDeliveryFailure(it, callback) },
                            {},
                            { callback.onStart(it) })
        }

        private fun <T> onDeliverySuccess(s: String?, callback: HttpRequestCallback<T>) {
            if (s.isNullOrEmpty()) {
                //json为null或empty
                callback.onFailure(JsonParserHelper.DEFAULT_CODE, CurrentApp.getInstance().getString(R.string.no_data_exception))
            } else {
                val type = JsonParserHelper.getSuperclassTypeParameter(callback.javaClass)
                if (type === String::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    callback.onSuccess(s as T, JsonParserHelper.getMsg(s))
                } else {
                    when (val code = JsonParserHelper.getCode(s)) {
                        JsonParserHelper.SUCCESS_CODE, JsonParserHelper.DEFAULT_CODE -> {
                            val result: T? = JsonParserHelper.parseHttpJson<T>(s, type)
                            if (result == null) {
                                callback.onFailure(code, CurrentApp.getInstance().getString(R.string.no_data_exception))
                            } else {
                                callback.onSuccess(result, JsonParserHelper.getMsg(s))
                            }
                        }
                        JsonParserHelper.INVALID_CODE -> {
                            CurrentApp.getInstance().backToHome()
                            callback.onFailure(code, JsonParserHelper.getMsg(s))
                        }
                        else -> {
                            callback.onFailure(code, JsonParserHelper.getMsg(s))
                        }
                    }
                }
            }
            callback.onCompleted()
        }

        private fun <T> onDeliveryFailure(throwable: Throwable?, callBack: HttpRequestCallback<T>) {
            callBack.onFailure(JsonParserHelper.DEFAULT_CODE, ApiException.handleException(throwable).getErrorMessage())
            callBack.onCompleted()
        }
    }
}