package com.holike.cloudshelf.activity

import android.content.Intent
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity

/**
 * Android 启动模式及singleTask与点Home键后返回应用存在的问题
 * 当一个Activity启动模式设置为singleTask后（如MainActivity），并同时打开了其他Activity。
 * 此时按下HOME键。再返回应用时，没有显示打开的其他Activity，而显示MainActivity,
 *
 * 解决方式
 *通过一个标准Standard模式的Activity去启动SingleTask的Activity，可以避免这个问题。
 *如从闪屏页SplashActivity跳转到MainActivity页面。
 */
class SplashActivity : BaseActivity() {
    override fun getLayoutResourceId(): Int = R.layout.activity_splash

    override fun setScreenStyle() {
        super.setScreenStyle()
        openActivityForResult(MainActivity::class.java, 100)
        overridePendingTransition(R.anim.anim_silent, R.anim.anim_silent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            finish()
        }
    }
}