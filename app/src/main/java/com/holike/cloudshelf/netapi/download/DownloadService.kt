package com.holike.cloudshelf.netapi.download

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

//文件下载api
interface DownloadService {

    @Streaming
    @GET
    fun download(@Url url: String): Observable<ResponseBody>
}