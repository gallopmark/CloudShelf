package com.holike.cloudshelf.base

import pony.xcode.mvp.BasePresenter
import pony.xcode.mvp.BaseView
import pony.xcode.utils.GenericsUtils


abstract class HollyFragment<P : BasePresenter<*, V>, V : BaseView> : BaseFragment() {
    protected lateinit var mPresenter: P

    //通过泛型参数创建presenter对象
    @Suppress("UNCHECKED_CAST")
    override fun createPresenter() {
        try {
            mPresenter = (GenericsUtils.getGenericsSuperclassType(javaClass) as Class<P>).newInstance()
            if (this is BaseView) {
                //绑定view
                mPresenter.register(this as V)
            }
        } catch (e: Exception) {
            //You should extends BaseFragment only
        }
    }

    override fun onDestroyView() {
        //解绑view
        mPresenter.unregister()
        super.onDestroyView()
    }
}