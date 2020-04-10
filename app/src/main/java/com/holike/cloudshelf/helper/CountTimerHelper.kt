package com.holike.cloudshelf.helper

import android.os.CountDownTimer

//验证码倒计时帮助类
class CountTimerHelper {

    companion object {
        private const val COUNT_DOWN_INTERVAL = 1000L
        private var mTimer: CountTimer? = null
        var mLastRemainTime: Long = 0  //上次读秒剩余时间

        fun isLastRemain() = mLastRemainTime > 0

        fun execute(millisInFuture: Long, callback: CountDownCallback) {
            mTimer?.cancel()
            mTimer = if (mLastRemainTime > 0L) {
                CountTimer(mLastRemainTime, COUNT_DOWN_INTERVAL, callback)
            } else {
                CountTimer(millisInFuture, COUNT_DOWN_INTERVAL, callback)
            }
            mTimer?.start()
        }

        fun cancel() {
            mLastRemainTime = 0L
            mTimer?.cancel()
            mTimer = null
        }
    }

    private class CountTimer(millisInFuture: Long, countDownInterval: Long, private val callback: CountDownCallback) :
            CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            mLastRemainTime = millisUntilFinished  //剩余毫秒数
            callback.onTick(millisUntilFinished)
        }

        override fun onFinish() {
            mLastRemainTime = 0L
            callback.onFinish()
        }
    }

    interface CountDownCallback {
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }
}