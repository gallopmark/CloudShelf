package com.holike.cloudshelf.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.holike.cloudshelf.base.BaseFragment
import java.io.File
import java.util.*

class AppUtils {
    companion object {
        fun getDefaultApkPath(context: Context): String? {
            var fileDir = context.applicationContext.externalCacheDir
            var path: String? = null
            if (fileDir != null) {
                path = fileDir.absolutePath + File.separator + "apk"
            } else {
                fileDir = context.applicationContext.getExternalFilesDir(null)
                if (fileDir != null) {
                    path = fileDir.absolutePath + File.separator + "apk"
                }
            }
            if (path != null) {
                val file = File(path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                return file.absolutePath
            }
            return null
        }

        fun canInstallApk(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.applicationContext.packageManager.canRequestPackageInstalls()
            } else true
        }

        /* 后面跟上包名，可以直接跳转到对应APP的未知来源权限设置界面。使用startActivityForResult
        是为了在关闭设置界面之后，获取用户的操作结果，然后根据结果做其他处理*/
        fun startUnknownAppSourceSetting(fragment: BaseFragment, requestCode: Int) {
            val context = fragment.context
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context != null) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.applicationContext.packageName}"))
                    fragment.openActivityForResult(intent, requestCode)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    fragment.openActivityForResult(intent, requestCode)
                }
            }
        }

        private const val UUID_PREF_NAME = "uuid_pref"

        /*获取设备唯一标识的正确姿势*/
        @SuppressLint("HardwareIds")
        fun getDeviceUUid(context: Context): String? {
            val sp = context.getSharedPreferences(UUID_PREF_NAME, Context.MODE_PRIVATE)
            val uuid = sp.getString("uuid", "")
            if (uuid.isNullOrEmpty()) {
                val androidId = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
                val id = androidId ?: ""
                val deviceUuid = UUID(id.hashCode().toLong(), id.hashCode().toLong() shl 32)
                val deviceId = deviceUuid.toString()
                sp.edit().putString("uuid", deviceId).apply() //持久化保存-保证唯一性
                return deviceId
            }
            return uuid
        }
    }
}