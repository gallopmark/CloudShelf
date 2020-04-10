package com.holike.cloudshelf.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.R
import com.holike.cloudshelf.bean.VersionInfoBean
import com.holike.cloudshelf.netapi.download.DownloadCallBack
import com.holike.cloudshelf.netapi.download.DownloadEngine
import com.holike.cloudshelf.util.AppUtils
import com.holike.cloudshelf.widget.CustomToast
import io.reactivex.disposables.Disposable
import java.io.File

//版本更新对话框
class VersionUpdateDialog(context: Context, private val bean: VersionInfoBean, private val listener: OnApkInstallListener) :
        CommonDialog(context, R.style.AppDialogStyle) {
    private var mDisposable: Disposable? = null
    private var mApkFile: File? = null //下载后的apk文件

    companion object {
        const val UNKNOWN_APP_REQUEST_CODE = 0x1321
    }

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun getLayoutResourceId(): Int = R.layout.dialog_version_update

    override fun getWidth(): Int = mContext.resources.getDimensionPixelSize(R.dimen.dp_270)

    override fun initView(contentView: View) {
        contentView.findViewById<TextView>(R.id.tv_title).text = mContext.getString(R.string.text_version_update)
        contentView.findViewById<TextView>(R.id.tv_update_message).text = bean.updateTips
        contentView.findViewById<TextView>(R.id.update_button).setOnClickListener { doDownload() }
    }

    //下载apk
    private fun doDownload() {
        val url = bean.updatePath
        if (url.isNullOrEmpty()) {
            CustomToast.showToast(mContext, mContext.getString(R.string.text_download_link_empty), Toast.LENGTH_LONG)
            dismiss()
        } else {
            val filepath = AppUtils.getDefaultApkPath(mContext)
            if (filepath == null) {
                CustomToast.showToast(mContext, mContext.getString(R.string.text_download_path_error), Toast.LENGTH_LONG)
                dismiss()
                return
            }
            val filename: String = if (!TextUtils.isEmpty(bean.actualVersion)) {
                //接口返回的版本名字 如3.0.0
                "v${bean.actualVersion}.apk"
            } else {
                if (!TextUtils.isEmpty(bean.version)) { //接口返回的版本 如300
                    "v${bean.version}.apk"
                } else {
                    "${mContext.getString(R.string.app_name)}.apk"
                }
            }
            val file = File(filepath, filename)
            if (filename == "${mContext.getString(R.string.app_name)}.apk" && file.exists()) {
                file.delete()
                download(url, filepath, filename)
            } else {
                if (file.exists()) {  //当前版本的apk已经存在
                    mApkFile = file
                    checkAndInstall()  //直接检测和安装
                } else {  //不存在则下载
                    download(url, filepath, filename)
                }
            }
        }
    }

    private fun download(url: String, filepath: String, filename: String) {
        mDisposable = DownloadEngine.download(url, filepath, filename, object : DownloadCallBack() {
            override fun onStart() {
                onDownloadStart()
            }

            override fun onProgressChanged(progress: Int) {
                onProgressUpdate(progress)
            }

            override fun onSuccess(file: File?) {
                mApkFile = file
                onDownloadSuccess()
            }

            override fun onFailure(failReason: String?) {
                onDownloadFailure()
            }
        })
    }

    private fun onDownloadStart() {
        mContentView.findViewById<TextView>(R.id.tv_progress).text = String.format(mContext.getString(R.string.text_update_progress), "0%")
        mContentView.findViewById<TextView>(R.id.tv_failure).visibility = View.GONE
        mContentView.findViewById<TextView>(R.id.update_button).visibility = View.GONE
        mContentView.findViewById<LinearLayout>(R.id.updating_layout).visibility = View.VISIBLE
    }

    private fun onProgressUpdate(progress: Int) {
        val progressBar = mContentView.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 100
        progressBar.progress = progress
        mContentView.findViewById<TextView>(R.id.tv_progress).text =
                String.format(mContext.getString(R.string.text_update_progress), "${progress}%")
    }

    //下载成功
    private fun onDownloadSuccess() {
        //下载成功
        mContentView.findViewById<LinearLayout>(R.id.updating_layout).visibility = View.GONE
        mContentView.findViewById<TextView>(R.id.update_button).apply {
            text = mContext.getString(R.string.text_try_now)
            visibility = View.VISIBLE
            setOnClickListener { checkAndInstall() }
        }
    }

    //下载失败
    private fun onDownloadFailure() {
        mContentView.findViewById<LinearLayout>(R.id.updating_layout).visibility = View.GONE
        //下载失败
        mContentView.findViewById<TextView>(R.id.tv_failure).apply {
            text = mContext.getString(R.string.text_download_failure)
            visibility = View.VISIBLE
        }
        //更新按钮显示“重新下载”
        mContentView.findViewById<TextView>(R.id.update_button).apply {
            text = mContext.getString(R.string.text_download_retry)
            visibility = View.VISIBLE
            setOnClickListener { doDownload() }
        }
    }

    //判断是否允许安装未知来源应用
    private fun checkAndInstall() {
        if (AppUtils.canInstallApk(mContext)) {
            installApk()
        } else {
            listener.onStartUnknownAppSourceSetting()
        }
    }

    fun installApk() {
        val file = mApkFile
        if (file == null || !file.exists()) {
            //文件被删除 重新下载
            CustomToast.showToast(mContext, R.string.text_apk_does_not_exist, Toast.LENGTH_LONG)
            doDownload()
        } else {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                val data: Uri
                val type = "application/vnd.android.package-archive"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val authority = "${BuildConfig.APPLICATION_ID}.apkFileProvider"
                    data = FileProvider.getUriForFile(context, authority, file)
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                } else {
                    data = Uri.fromFile(file)
                }
                mContext.grantUriPermission(mContext.applicationContext.packageName, data, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(data, type)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext.startActivity(intent)
                dismiss()
                listener.onInstallApk()
            } catch (ignored: Exception) {
            }
        }
    }

    override fun dismiss() {
        mDisposable?.dispose()
        super.dismiss()
    }

    interface OnApkInstallListener {
        fun onStartUnknownAppSourceSetting()
        fun onInstallApk()
    }
}