package com.holike.cloudshelf.util

import android.util.Log
import com.holike.cloudshelf.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

class LogCat {
    companion object {
        private val TAG: String = BuildConfig.VERSION_NAME
        private const val LOG_MAX_LENGTH = 2000

        fun i(msg: String?) {
            print("i", TAG, msg)
        }

        fun i(TAG: String, msg: String?) {
            print("i", TAG, msg)
        }

        fun e(msg: String?) {
            e(TAG, msg)
        }

        fun e(e: Throwable?) {
            e(TAG, e)
        }

        fun d(msg: String?) {
            print("d", TAG, msg + "")
        }

        fun d(TAG: String, msg: String?) {
            print("d", TAG, msg)
        }

        fun v(msg: String?) {
            print("v", TAG, msg)
        }

        fun v(TAG: String, msg: String?) {
            print("v", TAG, msg)
        }

        fun w(msg: String?) {
            print("w", TAG, msg)
        }

        fun w(TAG: String, msg: String?) {
            print("w", TAG, msg)
        }

        /*
        打印请求
         */
        fun request(msg: String?) {
            print("v", "request:", msg)
        }

        /*
        打印响应结果
         */
        fun response(msg: String?) {
            print("i", "response:", msg)
        }

        fun e(tag: String, e: Throwable?) {
            if(e == null) return
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw, true))
            print("e", tag, sw.toString())
        }

        fun e(tag: String, msg: String?) {
            print("e", tag, msg)
        }

        /*
        日志长度超过限制时，采取分段形式打印日志
         */
        fun print(logType: String?, TAG: String, msg: String?) {
            if (!BuildConfig.LOG_DEBUG || msg.isNullOrEmpty()) return
            val strLength = msg.length
            var start = 0
            var end = LOG_MAX_LENGTH
            for (i in 1..99) {
                if (strLength > end) {
                    when (logType) {
                        "v" -> Log.v(TAG + i, msg.substring(start, end))
                        "i" -> Log.i(TAG + i, msg.substring(start, end))
                        "d" -> Log.d(TAG + i, msg.substring(start, end))
                        "e" -> Log.e(TAG + i, msg.substring(start, end))
                        "w" -> Log.w(TAG + i, msg.substring(start, end))
                    }
                    start = end
                    end += LOG_MAX_LENGTH
                } else {
                    when (logType) {
                        "v" -> Log.v(TAG, msg.substring(start, strLength))
                        "i" -> Log.i(TAG, msg.substring(start, strLength))
                        "d" -> Log.d(TAG, msg.substring(start, strLength))
                        "e" -> Log.e(TAG, msg.substring(start, strLength))
                        "w" -> Log.w(TAG, msg.substring(start, strLength))
                    }
                    break
                }
            }
        }
    }
}