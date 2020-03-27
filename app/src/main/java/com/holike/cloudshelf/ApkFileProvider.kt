package com.holike.cloudshelf

import androidx.core.content.FileProvider

//自定义FileProvider 避免与其他第三方库冲突
class ApkFileProvider : FileProvider()