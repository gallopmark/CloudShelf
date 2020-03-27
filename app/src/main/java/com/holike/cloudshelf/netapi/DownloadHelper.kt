package com.holike.cloudshelf.netapi

import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.util.LogCat
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

//文件下载帮助类
class DownloadHelper {

    //下载信息
    internal class DownloadInfo {
        var progress: Int = 0
        var total: Long = 0
        var file: File? = null
        var failReason: String? = null
    }

    companion object {
        private const val TIMEOUT = 60L

        //rxjava 牛逼
        fun download(url: String, filepath: String, filename: String, callback: DownloadCallBack): Disposable {
            val downloadInfo = DownloadInfo()
            val okBuilder = OkHttpClient().newBuilder()
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okBuilder.build())
                    .build()
            val downloadService = retrofit.create(DownloadService::class.java)
            return downloadService.download(url)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .flatMap { create(it, filepath, filename, downloadInfo) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LogCat.i("download", "progress:${it.progress};total:${it.total}")
                        callback.onProgressChanged(it.progress)
                    }, {
                        LogCat.e("download", "下载失败...")
                        callback.onFailure(ApiException.handleException(it).getErrorMessage())
                    }, {
                        LogCat.d("download", "下载完成...")
                        callback.onSuccess(downloadInfo.file)
                    }, {
                        LogCat.d("download", "下载开始...")
                        callback.onStart()
                    })
        }

        private fun create(responseBody: ResponseBody, filepath: String, filename: String, downloadInfo: DownloadInfo)
                : ObservableSource<DownloadInfo> {
            return Observable.create { emitter ->
                var `is`: InputStream? = null
                val buf = ByteArray(2048)
                var len: Int
                var fos: FileOutputStream? = null
                try {
                    `is` = responseBody.byteStream()
                    val total = responseBody.contentLength()
                    val dir = File(filepath)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    val tempFilename = "${filename.substring(0, filename.lastIndexOf("."))}.temp"
                    val tempFile = File(dir, tempFilename)
                    fos = FileOutputStream(tempFile)
                    var progress = 0
                    var lastProgress: Int
                    var sum: Long = 0
                    while (`is`.read(buf).also { len = it } != -1) {
                        sum += len.toLong()
                        fos.write(buf, 0, len)
                        val finalSum = sum
                        lastProgress = progress
                        progress = (finalSum * 100 / total).toInt()
                        // 如果进度与之前进度相等，则不更新，如果更新太频繁，则会造成界面卡顿
                        if (progress > 0 && progress != lastProgress) {
                            downloadInfo.progress = progress
                            downloadInfo.total = total
                            emitter.onNext(downloadInfo)
                        }
                    }
                    fos.flush()
                    //走到这一步 下载完成
                    val newFile = File(filepath, filename)
                    tempFile.renameTo(newFile)  //文件重命名
                    downloadInfo.file = newFile
                    emitter.onComplete()
                } catch (e: Exception) {
                    LogCat.e(e)
                    //下载失败
                    emitter.onError(e)
                } finally {
                    //关闭流
                    try {
                        `is`?.close()
                    } catch (e: IOException) {
                        LogCat.e(e)
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                        LogCat.e(e)
                    }
                }
            }
        }
    }
}