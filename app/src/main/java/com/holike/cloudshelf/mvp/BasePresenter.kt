package com.holike.cloudshelf.mvp

import com.holike.cloudshelf.util.GenericsUtils
import java.lang.ref.WeakReference

abstract class BasePresenter<M : BaseModel, V : BaseView> {

    private var mViewReference: WeakReference<V?>? = null
    protected lateinit var mModel: M

    //非静态代码块会在每次类被调用或者被实例化时就会被执行。
    init {
        try {
            @Suppress("UNCHECKED_CAST")
            mModel = (GenericsUtils.getGenericsSuperclassType(javaClass) as Class<M>).newInstance()
        } catch (e: Exception) {
            //子类缺少范型参数的类型.
        }
    }

    fun register(view: V) {
        mViewReference = WeakReference(view)
    }

    val view: V? get() = mViewReference?.get()

    open fun unregister() {
        mModel.destroy()
        unregisterView()
    }

    open fun unregisterView() {
        mViewReference?.clear()
        mViewReference = null
    }
}