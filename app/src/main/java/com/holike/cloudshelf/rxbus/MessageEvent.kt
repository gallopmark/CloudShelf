package com.holike.cloudshelf.rxbus

import android.os.Bundle

class MessageEvent(var type: String?) {
    var arg1 = 0
    var arg2 = 0
    var obj: Any? = null
    var arguments: Bundle? = null

}