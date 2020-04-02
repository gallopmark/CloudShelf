package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.fragment.ProductClassifyFragment
import com.holike.cloudshelf.fragment.ProgramLibFragment

//方案库
class MultiTypeActivity : BaseActivity() {

    companion object {
        const val TYPE_PROGRAM = "type-program"  //方案库类型
        const val TYPE_PRODUCT = "type-product"  //产品大全

        fun open(act: BaseActivity, type: String?) {
            open(act, type, null)
        }

        fun open(act: BaseActivity, type: String?, extras: Bundle?) {
            val intent = Intent(act, MultiTypeActivity::class.java)
            intent.type = type
            extras?.let { intent.putExtras(it) }
            act.startActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_multi_type

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        val type = intent.type
        if (type == TYPE_PROGRAM) {
            addFragment(ProgramLibFragment())
        } else if (type == TYPE_PRODUCT) {
            addFragment(ProductClassifyFragment(), intent.extras)
        }
    }
}