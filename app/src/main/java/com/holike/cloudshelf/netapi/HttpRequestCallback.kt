package com.holike.cloudshelf.netapi

import io.reactivex.disposables.Disposable

abstract class HttpRequestCallback<T> {

    open fun onStart(d: Disposable?) {}

    abstract fun onFailure(code: Int, failReason: String?)

    abstract fun onSuccess(result: T, message: String?)

    open fun onCompleted() {}
}