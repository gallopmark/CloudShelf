package com.holike.cloudshelf.netapi

import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException


class ApiException(throwable: Throwable?) : Exception(throwable) {
    private var mMessage: String? = throwable?.message

    companion object {

        fun handleException(e: Throwable?): ApiException {
            val ex = ApiException(e)
            if (e is HttpException) { //404或405、500等错误码
                ex.mMessage = CurrentApp.getInstance().getString(R.string.http_exception)
            } else if (e is SocketTimeoutException) {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.socket_timeout_exception)
            } else if (e is ConnectException) {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.connect_exception)
            } else if (e is InterruptedIOException) {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.connect_timeout_exception)
            } else if (e is UnknownHostException) {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.unknown_host_exception)
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is JsonSerializer<*>
                    || e is NotSerializableException
                    || e is ParseException
            ) {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.json_parse_exception)
            } else {
                ex.mMessage = CurrentApp.getInstance().getString(R.string.http_exception)
            }
            return ex
        }
    }

    fun getErrorMessage(): String? = mMessage
}