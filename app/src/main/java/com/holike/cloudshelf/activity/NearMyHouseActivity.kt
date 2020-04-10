package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.fragment.NearMyHouseFragment


//搜搜我家
class NearMyHouseActivity : BaseActivity(){

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(NearMyHouseFragment())
    }
}