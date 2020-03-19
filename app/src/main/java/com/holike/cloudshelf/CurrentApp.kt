package com.holike.cloudshelf

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.holike.cloudshelf.util.JPushUtils
import com.holike.cloudshelf.util.LogCat
import com.scwang.smartrefresh.header.WaterDropHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import com.tencent.bugly.crashreport.CrashReport
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap


class CurrentApp : MultiDexApplication() {
    init {
        /*
         * 兼容5.0以下系统
         */
        /*获取当前系统的android版本号*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //适配android5.0以下
            /*解决低版本手机vectorDrawable不支持儿闪退问题*/
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> WaterDropHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> BallPulseFooter(context) }
    }

    private var mWeakRef: WeakReference<Activity>? = null
    private var mActivityCache: LinkedList<Activity>? = null

    private var mValueMap: HashMap<String, Any?>? = null //用于activity、fragment之间大数据传值

    //机器的分辨率为 3840*2160
    private var mScreenWidth: Int = 3840 //屏幕宽度
    private var mScreenHeight: Int = 2160 //屏幕高度
    private var mMaxPixels: Int = mScreenWidth.coerceAtLeast(mScreenHeight)  //取宽度、高度的最大值

    companion object {

        private lateinit var mInstance: CurrentApp

        fun getInstance() = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback())
        initDisplay()
        initJpush()
        initBugly()
    }

    //获取屏幕宽高，全局使用
    private fun initDisplay() {
        val wm = getSystemService(Context.WINDOW_SERVICE)
        if (wm != null && wm is WindowManager) {
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            mScreenWidth = outMetrics.widthPixels
            mScreenHeight = outMetrics.heightPixels
            mMaxPixels = mScreenWidth.coerceAtLeast(mScreenHeight)
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

    fun getScreenWidth() = mScreenWidth

    fun getScreenHeight() = mScreenHeight

    fun getMaxPixels() = mMaxPixels

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
            mActivityCache?.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (mActivityCache == null) {
                mActivityCache = LinkedList()
            }
            mActivityCache?.add(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            mWeakRef = WeakReference(activity)
        }
    }

    /**
     * 获取当前打开的界面
     */
    fun getCurrentActivity(): Activity? = mWeakRef?.get()

    fun finishAllActivities() {
        mActivityCache?.let {
            for (act in it) {
                act.finish()
            }
        }
    }

    //退出app
    fun exit() {
        finishAllActivities()
        Process.killProcess(Process.myPid())
    }
}