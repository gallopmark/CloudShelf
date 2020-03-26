package com.holike.cloudshelf.mvp.presenter

import android.content.Intent
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import cn.jpush.android.api.JPushInterface
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.*
import com.holike.cloudshelf.bean.AdvertisingBean
import com.holike.cloudshelf.bean.LoginBean
import com.holike.cloudshelf.dialog.LoginDialog
import com.holike.cloudshelf.dialog.UniversalDialog
import com.holike.cloudshelf.fragment.video.ExoPlayerFragment
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.model.MainModel
import com.holike.cloudshelf.mvp.view.MainView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import pony.xcode.mvp.BasePresenter

class MainPresenter : BasePresenter<MainModel, MainView>() {

    //登录对话框
    private var mLoginDialog: LoginDialog? = null
    private var mHandler: Handler? = null

    /*检测登录状态*/
    fun initLoginState(activity: MainActivity) {
        if (!PreferenceSource.isLogin()) {  //未登录过
            showLoginDialog(activity)
        } else {
            view?.onLoginSuccess(null)
        }
    }

    /*弹出登录对话框*/
    fun showLoginDialog(activity: MainActivity) {
        gcLoginDialog()
        mLoginDialog = LoginDialog(activity).apply {
            setListener(object : LoginDialog.OnViewClickListener {
                override fun onGetCode(phone: String) {
                    getVerificationCode(phone)
                }

                override fun doLogin(phone: String, code: String) {
                    login(phone, code, JPushInterface.getRegistrationID(activity))
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

    fun initClickViews(activity: MainActivity, vararg views: View) {
        val animation = AnimationUtils.loadAnimation(activity, R.anim.anim_zoom_in)
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
        mModel.getAdvertising(object : HttpRequestCallback<AdvertisingBean>() {
            override fun onSuccess(result: AdvertisingBean, message: String?) {
                view?.onAdvertisingSuccess(result)
                mHandler?.removeCallbacks(mRun)
            }

            override fun onFailure(code: Int, failReason: String?) {
//                view?.onAdvertisingFailure(failReason)
                //加载失败，3秒后自动重试
                if (mHandler == null) {
                    mHandler = Handler()
                }
                mHandler?.postDelayed(mRun, 3000L)
            }
        })
    }

    private val mRun = Runnable { getAdvertising() }

    //动态设置视频播放器的长宽，根据UI图的尺寸比例进行调整
    fun initVideoContainer(activity: MainActivity, videoContainer: FrameLayout, bean: AdvertisingBean) {
        //获取屏幕宽度  214 / 640
        if (!bean.videoUrl.isNullOrEmpty()) {
            val sw = activity.getCurrentApp().getMaxPixels()
            val videoWidth = (0.38f * sw).toInt()
            val videoHeight = (videoWidth * 0.64f).toInt()
            val lp = videoContainer.layoutParams as FrameLayout.LayoutParams
            lp.width = videoWidth
            lp.height = videoHeight
            videoContainer.layoutParams = lp
            //https://file.holike.com/miniprogram/test/video/4aa0637b-e062-4419-b9b8-f1d4daa08615.mp4
            activity.supportFragmentManager.beginTransaction().replace(R.id.videoContainer, ExoPlayerFragment.newInstance(bean.videoUrl, bean.videoPic, bean.title)).commit()
        }
    }

    fun expired(activity: MainActivity, message: String?) {
        UniversalDialog(activity).title(R.string.text_expiration_reminder)
                .message(message)
                .setRight(R.string.text_Iknow, null)
                .show()
    }

    /*登出*/
    fun displayLogout(act: MainActivity) {
        //弹出退出帐号提示窗
        UniversalDialog(act).title(R.string.sign_out)
                .message(R.string.sign_out_message)
                .setLeft(R.string.text_cancel, null)
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

    //跳转处理
    fun jumpActivity(activity: MainActivity, viewId: Int) {
        when (viewId) {
            R.id.programmeIView -> {
                //方案库
                MultiTypeActivity.open(activity, MultiTypeActivity.TYPE_PROGRAM, R.mipmap.ic_title_programee, activity.getString(R.string.text_program_library))
            }
            R.id.productsIView -> {
                //产品大全
                activity.openActivity(Intent(activity, ProductCatalogActivity::class.java))
            }
            R.id.searchHomeIView -> {
                //搜搜我家
                activity.openActivity(Intent(activity, SoughtHouseActivity::class.java))
            }
            R.id.shareHomeIView -> {
                //晒晒我家
                activity.openActivity(Intent(activity, BlueprintListActivity::class.java))
            }
        }
    }

    override fun unregister() {
        gcLoginDialog()
        mHandler?.removeCallbacks(mRun)
        super.unregister()
    }

}