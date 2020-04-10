package com.holike.cloudshelf.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseModel {

    private var mDisposables: CompositeDisposable? = null

    fun addDisposable(disposable: Disposable?) {
        disposable?.apply {
            if (mDisposables == null) {
                mDisposables = CompositeDisposable()
            }
            mDisposables?.add(this)
        }
    }

    fun removeDisposable(disposable: Disposable?) {
        disposable?.apply { mDisposables?.remove(this) }
    }

    fun clear() {
        mDisposables?.clear()
    }

    open fun destroy() {
        mDisposables?.dispose()
    }
}