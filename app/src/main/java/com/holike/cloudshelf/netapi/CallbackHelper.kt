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
            val type = MyJsonParser.getSuperclassTypeParameter(callBack.javaClass)
            if (type === String::class.java) {
                @Suppress("UNCHECKED_CAST")
                callBack.onSuccess(s as T,  MyJsonParser.getMsg(s))
            } else {
                when (val code = MyJsonParser.getCode(s)) {
                    MyJsonParser.SUCCESS_CODE, MyJsonParser.DEFAULT_CODE -> {
                        val result: T? = MyJsonParser.parseHttpJson<T>(s, type)
                        if (result == null) {
                            callBack.onFailure(code, CurrentApp.getInstance().getString(R.string.no_data_exception))
                        } else {
                            callBack.onSuccess(result, MyJsonParser.getMsg(s))
                        }
                    }
                    MyJsonParser.INVALID_CODE -> {
                        CurrentApp.getInstance().backToHome()
                        callBack.onFailure(code, MyJsonParser.getMsg(s))
                    }
                    else -> {
                        callBack.onFailure(code, MyJsonParser.getMsg(s))
                    }
                }
            }
            callBack.onCompleted()
        }

        private fun <T> onDeliveryFailure(throwable: Throwable?, callBack: HttpRequestCallback<T>) {
            callBack.onFailure(MyJsonParser.DEFAULT_CODE, ApiException.handleException(throwable).getErrorMessage())
            callBack.onCompleted()
        }

    }
}