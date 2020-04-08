package com.holike.cloudshelf.mvp.model.fragment

import com.holike.cloudshelf.bean.AdvertisingBean
import com.holike.cloudshelf.bean.LoginBean
import com.holike.cloudshelf.bean.VersionInfoBean
import com.holike.cloudshelf.mvp.model.ApiModel
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient


class MainModel : ApiModel() {

    /*获取版本信息*/
    fun getVersionInfo(callback: HttpRequestCallback<VersionInfoBean>) {
        remove("get-version")
        put("get-version", doRequest(NetClient.getInstance().getNetApi().getVersionInfo(), callback))
    }

    /*查询广告位信息*/
    fun getAdvertising(callback: HttpRequestCallback<AdvertisingBean>) {
        remove("get-ad")
        put("get-ad", doRequest(NetClient.getInstance().getNetApi().getAdvertising(), callback))
    }

    /*登陆短信验证码*/
    fun getVerificationCode(phone: String, callback: HttpRequestCallback<String>) {
        remove("get-code")
        put("get-code", doRequest(NetClient.getInstance().getNetApi().getVerificationCode(phone), callback))
    }

    /*登录*/
    fun doLogin(phone: String, code: String, deviceId: String?, callback: HttpRequestCallback<LoginBean>) {
        remove("do-login")
        put("do-login", doRequest(NetClient.getInstance().getNetApi().doLogin(nonNullWrap(phone),nonNullWrap(code),nonNullWrap(deviceId)), callback))
    }

    /*登出*/
    fun doLogout(cliId: String?, token: String?, callback: HttpRequestCallback<String>) {
        remove("do-logout")
        put("do-logout", doRequest(NetClient.getInstance().getNetApi().doLogout(nonNullWrap(cliId), nonNullWrap(token)), callback))
    }
}