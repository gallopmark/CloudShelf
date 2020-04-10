package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.BaseFragment
import com.holike.cloudshelf.fragment.BleachedHouseInfoFragment

//晒晒好家的晒图详情查询
class BleachedHouseInfoActivity : BaseActivity() {

    companion object {
        fun open(fragment: BaseFragment, blueprintId: String?) {
            fragment.openActivity(BleachedHouseInfoActivity::class.java, Bundle().apply { putString("blueprintId", blueprintId) })
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(BleachedHouseInfoFragment(), intent.extras)
    }
}