package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.enumc.ProductCatalog
import com.holike.cloudshelf.fragment.ProductCatalogFragment
import com.holike.cloudshelf.fragment.ProductClassifyFragment
import com.holike.cloudshelf.fragment.ProductHomeProFragment
import com.holike.cloudshelf.fragment.ProgramLibFragment

class MultiTypeActivity : BaseActivity() {

    companion object {
        const val TYPE_PROGRAM = "type-program"  //方案库类型
        const val TYPE_PRODUCT_CATALOG = "type-product-catalog" //产品大全
        const val TYPE_PRODUCT_MODULE = "type-product-module"  //产品大全各模块

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

    override fun getLayoutResourceId(): Int = R.layout.activity_common

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        val type = intent.type
        if (type == TYPE_PROGRAM) {  //方案库
            startFragment(ProgramLibFragment())
        } else if (type == TYPE_PRODUCT_CATALOG) {  //产品大全
            startFragment(ProductCatalogFragment())
        } else if (type == TYPE_PRODUCT_MODULE) { //产品大全各个模块跳转
            val mode = intent.extras?.getString("dictCode") ?: return
            if (mode == ProductCatalog.HOME_PRO) {
                //家居家品
                startFragment(ProductHomeProFragment(), intent.extras)
            } else {
                startFragment(ProductClassifyFragment(), intent.extras)
            }
        }
    }
}