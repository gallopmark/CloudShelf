package com.holike.cloudshelf.rxbus

import android.os.Bundle

class MessageEvent {
    var arg1 = 0
    var arg2 = 0
    var obj: Any? = null
    var arguments: Bundle? = null
    var type: String? = null

    constructor() {}
    constructor(type: String?) {
        this.type = type
    }

}