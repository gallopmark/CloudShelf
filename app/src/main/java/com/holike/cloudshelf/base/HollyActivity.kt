package com.holike.cloudshelf.base

import pony.xcode.mvp.BasePresenter
import pony.xcode.mvp.BaseView
import pony.xcode.utils.GenericsUtils

//presenter activity
@Suppress("UNCHECKED_CAST")
abstract class HollyActivity<P : BasePresenter<*, V>, V : BaseView> : BaseActivity() {

    protected lateinit var mPresenter: P

    override fun createPresenter() {
        try {
            mPresenter = (GenericsUtils.getGenericsSuperclassType(javaClass) as Class<P>).newInstance()
            if (this is BaseView) {
                mPresenter.register(this as V)
            }
        } catch (e: Exception) {
            //You should extends BaseActivity only
        }
    }

    override fun onDestroy() {
        mPresenter.unregister()
        super.onDestroy()
    }
}