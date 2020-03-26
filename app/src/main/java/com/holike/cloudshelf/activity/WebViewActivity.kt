package com.holike.cloudshelf.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.jiguang.dy.Protocol.mContext
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import kotlinx.android.synthetic.main.activity_webview.*


class WebViewActivity : BaseActivity() {

    companion object {
        fun open(act: BaseActivity, url: String?) {
            val intent = Intent(act, WebViewActivity::class.java)
            intent.putExtra("url", url)
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_webview

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        initWebView()
        webView.loadUrl(intent.getStringExtra("url"))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.displayZoomControls = false
        settings.builtInZoomControls = true
        // 设置可以支持缩放
        // 设置可以支持缩放
        settings.setSupportZoom(false)
        //扩大比例的缩放
        //扩大比例的缩放
        settings.cacheMode = WebSettings.LOAD_NO_CACHE // 不加载缓存
        settings.defaultTextEncodingName = "utf-8"
        settings.domStorageEnabled = true //设置适应HTML5的一些方法
        settings.setAppCachePath(mContext.cacheDir.absolutePath)
        settings.allowFileAccess = true
        settings.setAppCacheEnabled(true)
        settings.supportMultipleWindows()
        settings.setSupportMultipleWindows(true)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return true
            }
        }
    }

    override fun onDestroy() {
        webView.clearHistory()
        webView.clearCache(true)
        webView.destroy()
        super.onDestroy()
    }
}