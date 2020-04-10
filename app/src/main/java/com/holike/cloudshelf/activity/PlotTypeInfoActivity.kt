package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.BaseFragment
import com.holike.cloudshelf.fragment.PlotTypeInfoFragment

//搜搜我家户型详情查询
class PlotTypeInfoActivity : BaseActivity() {

    companion object {
        fun open(fragment: BaseFragment, houseTypeId: String?) {
            fragment.openActivity(PlotTypeInfoActivity::class.java, Bundle().apply { putString("houseTypeId", houseTypeId) })
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(PlotTypeInfoFragment(),intent.extras)
    }

}