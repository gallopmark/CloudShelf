package com.holike.cloudshelf.dialog

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.holike.cloudshelf.R
import com.holike.cloudshelf.util.CheckUtils
import com.holike.cloudshelf.helper.ClearEditTextHelper
import com.holike.cloudshelf.helper.CountTimerHelper

//登录对话框
class LoginDialog(context: Context) : CommonDialog(context, R.style.AppDialogStyle) {

    companion object {
        const val TIME_OUT = 120 * 1000L //倒计时2分钟
    }

    private var mGetCodeTextView: TextView? = null
    private var mListener: OnViewClickListener? = null
    private var mCountDowning = false
    private var mPhoneNumberEnabled = false

    override fun getLayoutResourceId(): Int = R.layout.dialog_login

    override fun getWidth(): Int = mContext.resources.getDimensionPixelSize(R.dimen.dp_270)

    override fun initView(contentView: View) {
        setCanceledOnTouchOutside(false)  //点击外部不销毁
        val phoneEdt = contentView.findViewById<EditText>(R.id.et_phone)
        val codeEdt = contentView.findViewById<EditText>(R.id.et_code)
        mGetCodeTextView = contentView.findViewById(R.id.tv_getCode)
        val loginTxt = contentView.findViewById<TextView>(R.id.tv_login)
        ClearEditTextHelper.setTargetView(phoneEdt, object : ClearEditTextHelper.TextChangeListener {
            override fun textChanged(isEmpty: Boolean) {
                mPhoneNumberEnabled = !isEmpty && CheckUtils.isMobile(phoneEdt.text.toString())
                loginTxt.isEnabled = !codeEdt.text?.toString().isNullOrEmpty() && mPhoneNumberEnabled
                mGetCodeTextView?.isEnabled = !mCountDowning && mPhoneNumberEnabled  //输入了验证码 获取验证码按钮才能点击
            }
        })
        ClearEditTextHelper.setTargetView(codeEdt, object : ClearEditTextHelper.TextChangeListener {
            override fun textChanged(isEmpty: Boolean) {
                loginTxt.isEnabled = mPhoneNumberEnabled && !isEmpty
            }
        })
        mGetCodeTextView?.setOnClickListener {
            it.isEnabled = false
            mListener?.onGetCode(phoneEdt.text.toString())
        }
        loginTxt.setOnClickListener {
            mListener?.doLogin(phoneEdt.text.toString(), codeEdt.text.toString())
        }
        if (CountTimerHelper.isLastRemain()) {  //如果存在上次倒计时记录
            executeCountDownTimer()
        }
    }

    private fun executeCountDownTimer() {
        mCountDowning = true
        CountTimerHelper.execute(TIME_OUT, object : CountTimerHelper.CountDownCallback {
            override fun onTick(millisUntilFinished: Long) {
                mGetCodeTextView?.isEnabled = false
                val second = millisUntilFinished / 1000
                mGetCodeTextView?.text = mContext.getString(R.string.text_get_code_after, second.toString())
            }

            override fun onFinish() {
                onCountDownFinish()
            }
        })
    }

    /*获取验证码成功*/
    fun onGetCodeSuccess() {
        executeCountDownTimer()
    }

    /*获取验证码失败*/
    fun onGetCodeFailure() {
        onCountDownFinish()
    }

    private fun onCountDownFinish() {
        mCountDowning = false
        mGetCodeTextView?.text = mContext.getString(R.string.text_get_verification_code)
        mGetCodeTextView?.isEnabled = mPhoneNumberEnabled
        CountTimerHelper.cancel()
    }

    fun setListener(listener: OnViewClickListener?) {
        this.mListener = listener
    }

    interface OnViewClickListener {
        fun onGetCode(phone: String)
        fun doLogin(phone: String, code: String)
    }
}