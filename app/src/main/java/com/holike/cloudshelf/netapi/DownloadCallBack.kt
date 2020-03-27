package com.holike.cloudshelf.netapi

import java.io.File

//下载回调
abstract class DownloadCallBack {
    open fun onStart() {}
    open fun onProgressChanged(progress: Int) {}

    abstract fun onSuccess(file: File?)
    abstract fun onFailure(failReason: String?)
}