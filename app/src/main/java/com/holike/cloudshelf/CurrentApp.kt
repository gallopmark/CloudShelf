package com.holike.cloudshelf

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.holike.cloudshelf.activity.MainActivity
import com.holike.cloudshelf.bean.SystemCodeBean
import com.holike.cloudshelf.local.PreferenceSource
import com.holike.cloudshelf.netapi.CallbackHelper
import com.holike.cloudshelf.netapi.HttpRequestCallback
import com.holike.cloudshelf.netapi.NetClient
import com.holike.cloudshelf.rxbus.EventBus
import com.holike.cloudshelf.rxbus.EventType
import com.holike.cloudshelf.rxbus.MessageEvent
import com.holike.cloudshelf.util.JPushUtils
import com.holike.cloudshelf.util.LogCat
import com.scwang.smartrefresh.header.WaterDropHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

//程序入口
class CurrentApp : MultiDexApplication() {
    init {
        /*
         * 兼容5.0以下系统
         */
        /*获取当前系统的android版本号*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //适配android5.0以下
            /*解决低版本手机vectorDrawable不支持儿闪退问题*/
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> WaterDropHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> BallPulseFooter(context) }
    }

    private var mWeakRef: WeakReference<Activity>? = null
    private lateinit var mActivityCache: LinkedList<Activity>

    private var mValueMap: HashMap<String, Any?>? = null //用于activity、fragment之间大数据传值

    //机器的分辨率为 3840*2160
    private var mScreenWidth: Int = 3840 //屏幕宽度
    private var mScreenHeight: Int = 2160 //屏幕高度
    private var mMaxPixels: Int = mScreenWidth.coerceAtLeast(mScreenHeight)  //取宽度、高度的最大值
    private var mMinPixels: Int = mScreenWidth.coerceAtMost(mScreenHeight)

    //业务字典 全局使用
    private var mSystemCode: SystemCodeBean? = null

    private var mDisposable: Disposable? = null
    private var mHandler: Handler? = null

    companion object {

        private lateinit var mInstance: CurrentApp

        fun getInstance() = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mActivityCache = LinkedList()
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback())
        initDisplay()
        initJpush()
        initBugly()
    }

    //获取屏幕宽高，全局使用
    private fun initDisplay() {
        val ws = getSystemService(Context.WINDOW_SERVICE)
        ws?.let {
            val wm = it as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            mScreenWidth = outMetrics.widthPixels
            mScreenHeight = outMetrics.heightPixels
            mMaxPixels = mScreenWidth.coerceAtLeast(mScreenHeight)
            mMinPixels = mScreenWidth.coerceAtMost(mScreenHeight)
        }
    }

    //极光推送初始化
    private fun initJpush() {
        JPushUtils.init()
        JPushUtils.setAlias()
//        JPushInterface.setTags(this, 0, HashSet<String?>().apply { add(mDeviceId) })
        LogCat.e("registrationId", JPushInterface.getRegistrationID(this))
    }

    //腾讯bugly初始化
    private fun initBugly() {
        if (BuildConfig.DEBUG) {
            CrashReport.initCrashReport(this, BuildConfig.BUGLY_APP_ID, false)
        }
    }

//    fun getScreenWidth() = mScreenWidth

    fun getScreenHeight() = mScreenHeight

    fun getMaxPixels() = mMaxPixels

//    fun getMinPixels() = mMinPixels

    fun putExtra(name: String, obj: Any?) {
        if (mValueMap == null) {
            mValueMap = HashMap()
        }
        mValueMap?.put(name, obj)
    }

    fun removeExtra(name: String) {
        mValueMap?.remove(name)
    }

    private inner class CustomActivityLifecycleCallback : ActivityLifecycleCallbacks {

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            mWeakRef?.clear()
            mActivityCache.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivityCache.add(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            mWeakRef = WeakReference(activity)
        }
    }

    /**
     * 获取当前打开的界面
     */
    fun getCurrentActivity(): Activity? = mWeakRef?.get()

//    fun finishAllActivities() {
//        for (act in mActivityCache) {
//            act.finish()
//        }
//    }

    //退出app
//    fun exit() {
//        finishAllActivities()
//        Process.killProcess(Process.myPid())
//    }

    //app收到登录认证失效时-即被挤出登录时 退回到首页
    fun backToHome() {
        PreferenceSource.clear()  //清除本地缓存
        //发送事件通知首页检测登录状态
        EventBus.getInstance().post(MessageEvent(EventType.TYPE_LOGIN_INVALID))
        //finish掉MainActivity之上的所有activity
        for (i in mActivityCache.size - 1 downTo 0) {
            val act = mActivityCache[i]
            if (act is MainActivity) {
                continue
            }
            act.overridePendingTransition(0, 0)
            act.finish()
        }
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        startActivity(intent)
    }

    //获取业务字典 app内全局使用
    fun getDictionary() {
        getDictionary(null)
    }

    fun getDictionary(listen: OnRequestDictListener?) {
        mDisposable?.dispose()
        mDisposable = CallbackHelper.deliveryResult(NetClient.getInstance().getNetApi().getDictionary(),
                object : HttpRequestCallback<SystemCodeBean>() {
                    override fun onSuccess(result: SystemCodeBean, message: String?) {
                        mSystemCode = result
                        listen?.onDictSuccess(result, message)
                    }

                    override fun onFailure(code: Int, failReason: String?) {
                        //失败后3秒重新请求
                        listen?.onDictFailure(code, failReason)
                        if (mHandler == null) {
                            mHandler = Handler()
                        }
                        mHandler?.removeCallbacks(mRetryRun)
                        mHandler?.postDelayed(mRetryRun, 3000L)
                    }
                })
    }

    private val mRetryRun = Runnable { getDictionary() }

    fun getSystemCode(): SystemCodeBean? = mSystemCode

    override fun onTerminate() {
        mDisposable?.dispose()
        mHandler?.removeCallbacks(mRetryRun)
        super.onTerminate()
    }
}