package com.holike.cloudshelf.mvp.model

import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pony.xcode.mvp.BaseModel


abstract class ApiModel : BaseModel() {
    var mMap: HashMap<String, Disposable>? = null

    fun nonNullWrap(content: String?): String = if (content.isNullOrEmpty()) "" else content

    fun <T> doRequest(observable: Observable<String?>, callback: HttpRequestCallback<T>): Disposable {
        return CallbackHelper.deliveryResult(observable, callback)
    }

    fun <T> request(observable: Observable<String?>, callback: HttpRequestCallback<T>) {
        addDisposable(CallbackHelper.deliveryResult(observable, callback))
    }

    fun remove(key: String) {
        val disposable = mMap?.remove(key)
        removeDisposable(disposable)
    }

    fun put(key: String, disposable: Disposable?) {
        if (disposable == null) return
        if (mMap == null) {
            mMap = HashMap()
        }
        mMap?.put(key, disposable)
        addDisposable(disposable)
    }

    override fun destroy() {
        mMap?.clear()
        super.destroy()
    }
}