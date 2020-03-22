package com.holike.cloudshelf.base

import pony.xcode.mvp.BasePresenter
import pony.xcode.mvp.BaseView
import pony.xcode.utils.GenericsUtils

//presenter activity
@Suppress("UNCHECKED_CAST")
abstract class HollyActivity<P : BasePresenter<*, V>, V : BaseView> : BaseActivity() {

    protected lateinit var mPresenter: P

    //通过泛型参数创建presenter对象
    override fun createPresenter() {
        try {
            mPresenter = (GenericsUtils.getGenericsSuperclassType(javaClass) as Class<P>).newInstance()
            if (this is BaseView) {
                //绑定view
                mPresenter.register(this as V)
            }
        } catch (e: Exception) {
            //You should extends BaseActivity only
        }
    }

    override fun onDestroy() {
        //解绑view
        mPresenter.unregister()
        super.onDestroy()
    }
}