package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.BaseFragment
import com.holike.cloudshelf.fragment.PlotTypeListFragment

//小区户型列表页面
class PlotTypeListActivity : BaseActivity() {

    companion object {
        fun open(fragment: BaseFragment, communityId: String?, name: String?) {
            fragment.openActivity(PlotTypeListActivity::class.java, Bundle().apply {
                putString("communityId", communityId)
                putString("name", name)
            })
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(PlotTypeListFragment(), intent.extras)
    }
}