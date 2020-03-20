package com.holike.cloudshelf.rxbus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject


class EventBus private constructor() {
    private var mBus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    companion object {

        private val mInstance: EventBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            EventBus()
        }

        fun getInstance(): EventBus = mInstance
    }

    /**
     * 发送事件
     */
    fun post(event: Any) {
        mBus.onNext(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }
}