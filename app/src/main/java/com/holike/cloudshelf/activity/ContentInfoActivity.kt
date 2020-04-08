package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.fragment.ContentInfoFragment


/*详情展示 多模块公用*/
class ContentInfoActivity : BaseActivity() {

    companion object {

        //打开方案库详情
        fun openProgramLib(act: BaseActivity, id: String?) {
            open(act, "type-program", id)
        }

        //打开产品大全模块详情
        fun openProductClassify(act: BaseActivity, id: String?) {
            open(act, "type-product", id)
        }

        fun open(act: BaseActivity, contentType: String?, id: String?) {
            val intent = Intent(act, ContentInfoActivity::class.java)
            val extras = Bundle().apply {
                putString("content-type", contentType)
                putString("id", id)
            }
            intent.putExtras(extras)
            act.openActivity(intent)
        }
    }

    //底部弹出动画
    override fun getLayoutResourceId(): Int = R.layout.activity_common

    override fun setup(savedInstanceState: Bundle?) {
        startFragment(ContentInfoFragment(), intent.extras)
    }
}