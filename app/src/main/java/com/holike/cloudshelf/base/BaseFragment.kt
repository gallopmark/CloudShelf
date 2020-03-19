package com.holike.cloudshelf.base

import android.os.Bundle
import android.util.Log
import android.view.View
import pony.xcode.base.CommonFragment


abstract class BaseFragment : CommonFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("onViewCreated", ".........")
        init(view, savedInstanceState)
    }

    @Deprecated("kotlin replace with fun init")
    override fun setup(savedInstanceState: Bundle?) {

    }

    abstract fun init(view: View, savedInstanceState: Bundle?)
}