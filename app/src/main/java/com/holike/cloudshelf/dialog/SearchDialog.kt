package com.holike.cloudshelf.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.helper.ClearEditTextHelper
import pony.xcode.base.CommonDialog


class SearchDialog(context: Context) : CommonDialog(context) {
    private val mHeight: Int = (CurrentApp.getInstance().getScreenHeight() * 0.48f).toInt()
    private var mHint: CharSequence? = null
    private var mOnSearchListener: OnSearchListener? = null

    override fun getLayoutResourceId(): Int = R.layout.dialog_search

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getWindowAnimations(): Int = R.style.BottomDialogAnimation
    override fun getWidth(): Int = CurrentApp.getInstance().getMaxPixels()

    override fun initView(contentView: View) {
        val contentLayout = contentView.findViewById<LinearLayout>(R.id.contentLayout)
        val lp = contentLayout.layoutParams as FrameLayout.LayoutParams
        lp.height = mHeight
        contentLayout.layoutParams = lp
        val tipTView = contentView.findViewById<TextView>(R.id.tipTView)
        tipTView.text = mHint
        val searchEText = contentView.findViewById<EditText>(R.id.searchEText)
        searchEText.requestFocus()
        searchEText.isFocusable = true
        ClearEditTextHelper.setTargetView(searchEText, object : ClearEditTextHelper.TextChangeListener {
            override fun textChanged(isEmpty: Boolean) {
                if (isEmpty) {
                    tipTView.visibility = View.VISIBLE
                } else {
                    tipTView.visibility = View.GONE
                }
            }
        })
        contentView.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dismiss() }
        contentView.findViewById<TextView>(R.id.tv_search).setOnClickListener {
            val content = searchEText.text.toString()
            mOnSearchListener?.onSearch(content)
            dismiss()
        }
    }

    fun setHint(hint: CharSequence?): SearchDialog {
        this.mHint = hint
        return this
    }

    fun setOnSearchListener(l: OnSearchListener?): SearchDialog {
        this.mOnSearchListener = l
        return this
    }

    interface OnSearchListener {
        fun onSearch(content: String?)
    }

    override fun show() {
        super.show()
        mContentView.postDelayed({
            if (isShowing) {
                val im =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.showSoftInput(mContentView.findViewById(R.id.searchEText), 0)
            }
        }, 550)
    }

    override fun dismiss() {
        val view = currentFocus
        if (view is TextView) {
            val mInputMethodManager: InputMethodManager =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
        super.dismiss()
    }
}