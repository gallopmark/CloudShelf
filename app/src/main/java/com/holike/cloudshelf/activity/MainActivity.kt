package com.holike.cloudshelf.activity

import android.os.Bundle
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.fragment.MainFragment

//首页
class MainActivity : BaseActivity() {

    override fun setup(savedInstanceState: Bundle?) {
        //进到首页 调用获取字典方法
        CurrentApp.getInstance().getDictionary()
        val fragment = supportFragmentManager.findFragmentByTag("tag-main")
        if (fragment == null) {
            startFragment(R.id.decorViewContainer, MainFragment(), null, "tag-main")
        }
    }
}
