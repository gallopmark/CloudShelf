package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.fragment.BleachedHouseFragment

//晒晒我家
class BleachedHouseActivity : BaseActivity() {

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(BleachedHouseFragment())
    }
}