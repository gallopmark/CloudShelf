package com.holike.cloudshelf.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.AdvertisingBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.mvp.presenter.MainPresenter
import com.holike.cloudshelf.mvp.view.MainView
import com.holike.cloudshelf.rxbus.EventBus
import com.holike.cloudshelf.rxbus.EventType
import com.holike.cloudshelf.rxbus.MessageEvent
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

//首页
class MainActivity : HollyActivity<MainPresenter, MainView>(), MainView {

    private var mDisposable: Disposable? = null
    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun setup(savedInstanceState: Bundle?) {
        mPresenter.initLoginState(this)
        mPresenter.initClickViews(this, programmeIView, productsIView, searchHomeIView, shareHomeIView)
        mPresenter.getAdvertising()
        mDisposable = EventBus.getInstance().toObservable(MessageEvent::class.java).subscribe {
            if (TextUtils.equals(it.type, EventType.TYPE_LOGIN_INVALID)) {  //收到被挤出登录的通知
                logoutTextView.visibility = View.GONE  //隐藏退出登录按钮
                mPresenter.initLoginState(this)
            }
        }
    }

    //获取广告成功
    override fun onAdvertisingSuccess(bean: AdvertisingBean) {
        Glide.with(this).load(bean.pic).into(bgImageView)
        mPresenter.initVideoContainer(this, videoContainer, bean)
    }

    override fun onClickAnimationEnd(viewId: Int) {
        if (!PreferenceSource.isLogin()) {
            mPresenter.showLoginDialog(this)
        } else {  //已经登录过，则跳转相应的页面
            mPresenter.jumpActivity(this, viewId)
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
        logoutTextView.setOnClickListener { mPresenter.displayLogout(this) }
    }

    override fun onLoginFailure(code: Int, failReason: String?) {
        if (code == 600001) { //该账号已在####到期，需要续费才能继续使用
            mPresenter.expired(this, failReason)
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

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
    }

    override fun onDestroy() {
        mDisposable?.dispose()
        super.onDestroy()
    }
}
