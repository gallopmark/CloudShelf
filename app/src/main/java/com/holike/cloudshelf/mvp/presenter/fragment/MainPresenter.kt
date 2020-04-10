package com.holike.cloudshelf.mvp.presenter.fragment

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import cn.jpush.android.api.JPushInterface
import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.AdvertisingBean
import com.holike.cloudshelf.bean.LoginBean
import com.holike.cloudshelf.bean.VersionInfoBean
import com.holike.cloudshelf.dialog.LoginDialog
import com.holike.cloudshelf.dialog.UniversalDialog
import com.holike.cloudshelf.dialog.VersionUpdateDialog
import com.holike.cloudshelf.fragment.MainFragment
import com.holike.cloudshelf.fragment.ExoPlayerFragment
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.BasePresenter
import com.holike.cloudshelf.mvp.model.fragment.MainModel
import com.holike.cloudshelf.mvp.view.fragment.MainView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.util.AppUtils

class MainPresenter : BasePresenter<MainModel, MainView>() {

    companion object {
        private const val RETRY_TIME = 3000L
    }

    //登录对话框
    private var mLoginDialog: LoginDialog? = null
    private var mVersionUpdateDialog: VersionUpdateDialog? = null
    private var mHandler: Handler? = null

    /*检测登录状态*/
    fun initLoginState(context: Context) {
        if (!PreferenceSource.isLogin()) {  //未登录过
            showLoginDialog(context)
        } else {
            view?.onLoginSuccess(null)
        }
    }

    /*弹出登录对话框*/
    fun showLoginDialog(context: Context) {
        gcLoginDialog()
        mLoginDialog = LoginDialog(context).apply {
            setListener(object : LoginDialog.OnViewClickListener {
                override fun onGetCode(phone: String) {
                    getVerificationCode(phone)
                }

                override fun doLogin(phone: String, code: String) {
                    login(phone, code, JPushInterface.getRegistrationID(context))
                }
            })
            show()
        }
    }

    /*获取手机验证码*/
    private fun getVerificationCode(phone: String) {
        mModel.getVerificationCode(phone, object : HttpRequestCallback<String>() {
            override fun onSuccess(result: String, message: String?) {
                view?.onGetVCodeSuccess(message)
                mLoginDialog?.onGetCodeSuccess()
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onGetVCodeFailure(failReason)
                mLoginDialog?.onGetCodeFailure()
            }
        })
    }

    /*登录请求*/
    private fun login(phone: String, code: String, deviceId: String?) {
        mModel.doLogin(phone, code, deviceId, object : HttpRequestCallback<LoginBean>() {
            override fun onSuccess(result: LoginBean, message: String?) {
                result.phone = phone
                PreferenceSource.setData(result)  //保存登录信息
                view?.onLoginSuccess(message)
                mLoginDialog?.dismiss()
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onLoginFailure(code, failReason)
            }
        })
    }

    fun initClickViews(context: Context, vararg views: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_zoom_in)
        for (view in views) {
            view.setOnClickListener {
                animation.setAnimationListener(MyAnimationListener(it.id))
                it.startAnimation(animation)
            }
        }
    }

    /*按钮点击的动画监听*/
    private inner class MyAnimationListener(val viewId: Int) : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            view?.onClickAnimationEnd(viewId)
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    }

    /*获取首页广告位*/
    fun getAdvertising() {
        mHandler?.removeCallbacks(mRun)
        mModel.getAdvertising(object : HttpRequestCallback<AdvertisingBean>() {
            override fun onSuccess(result: AdvertisingBean, message: String?) {
                view?.onAdvertisingSuccess(result)
            }

            override fun onFailure(code: Int, failReason: String?) {
//                view?.onAdvertisingFailure(failReason)
                //加载失败，3秒后自动重试
                if (mHandler == null) {
                    mHandler = Handler()
                }
                mHandler?.apply {
                    removeCallbacks(mRun)
                    postDelayed(mRun, RETRY_TIME)
                }
            }
        })
    }

    private val mRun = Runnable { getAdvertising() }

    /*获取版本信息*/
    fun getVersionInfo() {
        mHandler?.removeCallbacks(mVersionRun)
        mModel.getVersionInfo(object : HttpRequestCallback<VersionInfoBean>() {
            override fun onSuccess(result: VersionInfoBean, message: String?) {
                if (result.obtainVersion() > BuildConfig.VERSION_CODE) {
                    //服务器的版本号大于当前app版本号  则提示更新
                    view?.onVersionUpdate(result)
                }
            }

            override fun onFailure(code: Int, failReason: String?) {
                //失败后3秒自动重试
                if (mHandler == null) {
                    mHandler = Handler()
                }
                mHandler?.apply {
                    removeCallbacks(mVersionRun)
                    postDelayed(mVersionRun, RETRY_TIME)
                }
            }
        })
    }

    private val mVersionRun = Runnable { getVersionInfo() }

    fun showVersionUpdateDialog(context: Context, bean: VersionInfoBean) {
        mVersionUpdateDialog?.dismiss()
        mVersionUpdateDialog = VersionUpdateDialog(context, bean, object : VersionUpdateDialog.OnApkInstallListener {
            override fun onStartUnknownAppSourceSetting() {
                view?.onStartUnknownAppSourceSetting()
            }

            override fun onInstallApk() {
                view?.onInstallApk()
            }
        }).apply { show() }
    }

    fun onActivityResult(context: Context, requestCode: Int) {
        if (requestCode == VersionUpdateDialog.UNKNOWN_APP_REQUEST_CODE && AppUtils.canInstallApk(context)) {
            mVersionUpdateDialog?.installApk()
        }
    }

    //动态设置视频播放器的长宽，根据UI图的尺寸比例进行调整
    fun initVideoContainer(mainFragment: MainFragment, videoContainer: FrameLayout, bean: AdvertisingBean) {
        //获取屏幕宽度  214 / 640
        if (!bean.videoUrl.isNullOrEmpty()) {
            val sw = CurrentApp.getInstance().getMaxPixels()
            val videoWidth = (0.38f * sw).toInt()
            val videoHeight = (videoWidth * 0.64f).toInt()
            val lp = videoContainer.layoutParams as FrameLayout.LayoutParams
            lp.width = videoWidth
            lp.height = videoHeight
            videoContainer.layoutParams = lp
            //https://file.holike.com/miniprogram/test/video/4aa0637b-e062-4419-b9b8-f1d4daa08615.mp4
            mainFragment.childFragmentManager.beginTransaction().replace(R.id.videoContainer,
                    ExoPlayerFragment.newInstance(bean.videoUrl, bean.videoPic, bean.title)).commit()
        }
    }

    fun expired(context: Context, message: String?) {
        UniversalDialog(context).title(R.string.text_expiration_reminder).message(message).setRight(R.string.text_Iknow, null).show()
    }

    /*登出*/
    fun displayLogout(context: Context) {
        //弹出退出帐号提示窗
        UniversalDialog(context).title(R.string.sign_out).message(R.string.sign_out_message).setLeft(R.string.text_cancel, null)
                .setRight(R.string.text_confirm, object : UniversalDialog.OnViewClickListener {
                    override fun onClick(dialog: UniversalDialog, view: View) {
                        dialog.dismiss()
                        logout()
                    }
                }).show()
    }

    //执行退出登录操作
    private fun logout() {
        mModel.doLogout(PreferenceSource.getCliId(), PreferenceSource.getToken(),
                object : HttpRequestCallback<String>() {

                    override fun onSuccess(result: String, message: String?) {
                        //登出成功
                        PreferenceSource.clear()  //清空记录
//                        极光推送 删除别名
//                        JPushInterface.deleteAlias(CurrentApp.getInstance(), 0)
                        view?.onLogoutSuccess(message)
                    }

                    override fun onFailure(code: Int, failReason: String?) {
                        view?.onLogoutFailure(failReason)
                    }
                })
    }

    private fun gcLoginDialog() {
        mLoginDialog?.dismiss()
        mLoginDialog = null
    }

    private fun release() {
        gcLoginDialog()
        mVersionUpdateDialog?.dismiss()
        mVersionUpdateDialog = null
        mHandler?.apply {
            removeCallbacks(mVersionRun)
            removeCallbacks(mRun)
            mHandler = null
        }
    }

    override fun unregister() {
        release()
        super.unregister()
    }

}