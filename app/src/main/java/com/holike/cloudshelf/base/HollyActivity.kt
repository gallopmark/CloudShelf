package com.holike.cloudshelf.base

import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.BaseView
import com.holike.cloudshelf.util.GenericsUtils


//presenter activity
abstract class HollyActivity<P : BasePresenter<*, V>, V : BaseView> : BaseActivity() {

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
            //You should extends BaseActivity only
        }
    }

    override fun onDestroy() {
        //解绑view
        mPresenter.unregister()
        super.onDestroy()
    }
}