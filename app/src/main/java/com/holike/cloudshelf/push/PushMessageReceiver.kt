package com.holike.cloudshelf.push

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.google.gson.Gson
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.MainActivity
import com.holike.cloudshelf.bean.JPushBean
import com.holike.cloudshelf.dialog.UniversalDialog
import com.holike.cloudshelf.util.LogCat
import com.holike.cloudshelf.widget.CustomToast

//极光推送广播通知
class PushMessageReceiver : JPushMessageReceiver() {

    override fun onMessage(context: Context, customMessage: CustomMessage) {
        LogCat.e("JPush", "[onMessage] $customMessage")
        onArrived(customMessage.extra)
    }

    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        LogCat.e("JPush", "[onNotifyMessageArrived] $message")
        onArrived(message.notificationExtras)
    }

    private fun onArrived(body: String?) {
        try {
            val pushBean = Gson().fromJson(body, JPushBean::class.java)
            if (TextUtils.equals(pushBean.type, "1")) {
                //挤出账号 - 该账号已在另一台云货架设备登录！
                CurrentApp.getInstance().backToHome()
                CustomToast.showToast(CurrentApp.getInstance(), pushBean.content, Toast.LENGTH_LONG)
            } else if (TextUtils.equals(pushBean.type, "2") || TextUtils.equals(pushBean.type, "3")) {
                val act = CurrentApp.getInstance().getCurrentActivity()
                if (act != null) {
                    UniversalDialog(act).title(R.string.text_expiration_reminder)
                            .message(pushBean.content)
                            .setRight(R.string.text_Iknow, null)
                            .show()
                }
            }
        } catch (e: Exception) {
        }
    }
}