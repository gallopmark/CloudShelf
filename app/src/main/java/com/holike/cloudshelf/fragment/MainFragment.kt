package com.holike.cloudshelf.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.BleachedHouseActivity
import com.holike.cloudshelf.activity.MultiTypeActivity
import com.holike.cloudshelf.activity.NearMyHouseActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.AdvertisingBean
import com.holike.cloudshelf.bean.VersionInfoBean
import com.holike.cloudshelf.dialog.VersionUpdateDialog
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.presenter.fragment.MainPresenter
import com.holike.cloudshelf.mvp.view.fragment.MainView
import com.holike.cloudshelf.rxbus.EventBus
import com.holike.cloudshelf.rxbus.EventType
import com.holike.cloudshelf.rxbus.MessageEvent
import com.holike.cloudshelf.util.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.*

//首页
class MainFragment : HollyFragment<MainPresenter, MainView>(), MainView {
    private var mDisposable: Disposable? = null

    override fun getLayoutResourceId(): Int = R.layout.fragment_main

    override fun setup(savedInstanceState: Bundle?) {
        mPresenter.initLoginState(mContext)
        mPresenter.initClickViews(mContext, programmeIView, productsIView, searchHomeIView, shareHomeIView)
        mPresenter.getAdvertising()
        mDisposable = EventBus.getInstance().toObservable(MessageEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (TextUtils.equals(it.type, EventType.TYPE_LOGIN_INVALID)) {  //收到被挤出登录的通知
                        logoutTextView.visibility = View.GONE  //隐藏退出登录按钮
                        mPresenter.initLoginState(mContext)
                    }
                }
    }

    //获取广告成功
    override fun onAdvertisingSuccess(bean: AdvertisingBean) {
        Glide.with(this).load(bean.pic).into(bgImageView)
        mPresenter.initVideoContainer(this, videoContainer, bean)
    }

    //onResume里面检测版本更新
    override fun onResume() {
        super.onResume()
        mPresenter.getVersionInfo()
    }

    //服务器有新版本的apk 此方法里提示更新
    override fun onVersionUpdate(bean: VersionInfoBean) {
        mPresenter.showVersionUpdateDialog(mContext, bean)
    }

    //8.0以上提示用户允许未知来源应用安装程序
    override fun onStartUnknownAppSourceSetting() {
        AppUtils.startUnknownAppSourceSetting(this,VersionUpdateDialog.UNKNOWN_APP_REQUEST_CODE)
    }

    //调起系统安装apk 结束首页
    override fun onInstallApk() {
        (mContext as BaseActivity).finish()
    }

    override fun onClickAnimationEnd(viewId: Int) {
        if (!PreferenceSource.isLogin()) {
            mPresenter.showLoginDialog(mContext)
        } else {  //已经登录过，则跳转相应的页面
            when (viewId) {
                R.id.programmeIView -> {
                    //方案库
                    MultiTypeActivity.open(mContext as BaseActivity, MultiTypeActivity.TYPE_PROGRAM)
                }
                R.id.productsIView -> {
                    //产品大全
                    MultiTypeActivity.open(mContext as BaseActivity, MultiTypeActivity.TYPE_PRODUCT_CATALOG)
                }
                R.id.searchHomeIView -> {
                    //搜搜我家
                    openActivity(Intent(mContext, NearMyHouseActivity::class.java))
                }
                R.id.shareHomeIView -> {
                    //晒晒我家
                    openActivity(Intent(mContext, BleachedHouseActivity::class.java))
                }
            }
        }
    }

    override fun onGetVCodeSuccess(message: String?) {
        showLongToast(message)
    }

    override fun onGetVCodeFailure(failReason: String?) {
        showLongToast(failReason)
    }

    override fun onLoginSuccess(message: String?) {
        showShortToast(message)
        logoutTextView.visibility = View.VISIBLE
        logoutTextView.setOnClickListener { mPresenter.displayLogout(mContext) }
    }

    override fun onLoginFailure(code: Int, failReason: String?) {
        if (code == 600001) { //该账号已在####到期，需要续费才能继续使用
            mPresenter.expired(mContext, failReason)
        }
    }

    //退出登录成功
    override fun onLogoutSuccess(message: String?) {
        showShortToast(message)
        logoutTextView.visibility = View.GONE  //隐藏退出登录按钮
    }

    override fun onLogoutFailure(failReason: String?) {
        showShortToast(failReason)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.onActivityResult(mContext, requestCode)
    }

    override fun onDestroy() {
        mDisposable?.dispose()
        super.onDestroy()
    }
}