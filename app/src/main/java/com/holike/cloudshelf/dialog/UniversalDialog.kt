package com.holike.cloudshelf.dialog

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.holike.cloudshelf.R
import pony.xcode.base.CommonDialog


class UniversalDialog(context: Context) : CommonDialog(context, R.style.AppDialogStyle) {

    private var mTitle: CharSequence? = null
    private var mMessage: CharSequence? = null
    private var mTextLeft: CharSequence? = null
    private var mLeftClickListener: OnViewClickListener? = null
    private var mTextRight: CharSequence? = null
    private var mRightClickListener: OnViewClickListener? = null

    override fun getLayoutResourceId(): Int = R.layout.dialog_universal
    override fun getWidth(): Int = mContext.resources.getDimensionPixelSize(R.dimen.dp_270)
    fun title(@StringRes titleId: Int): UniversalDialog {
        return title(mContext.getString(titleId))
    }

    fun title(title: CharSequence?): UniversalDialog {
        this.mTitle = title
        return this
    }

    fun message(@StringRes messageId: Int): UniversalDialog {
        return message(mContext.getString(messageId))
    }

    fun message(message: CharSequence?): UniversalDialog {
        this.mMessage = message
        return this
    }

    fun setLeft(@StringRes leftId: Int, l: OnViewClickListener?): UniversalDialog {
        return setLeft(mContext.getString(leftId), l)
    }

    fun setLeft(textLeft: CharSequence?, l: OnViewClickListener?): UniversalDialog {
        this.mTextLeft = textLeft
        this.mLeftClickListener = l
        return this
    }

    fun setRight(@StringRes rightId: Int, l: OnViewClickListener?): UniversalDialog {
        return setRight(mContext.getString(rightId), l)
    }

    fun setRight(textLeft: CharSequence?, l: OnViewClickListener?): UniversalDialog {
        this.mTextRight = textLeft
        this.mRightClickListener = l
        return this
    }

    override fun initView(contentView: View) {
        contentView.findViewById<TextView>(R.id.tv_title).text = mTitle
        contentView.findViewById<TextView>(R.id.tv_message).text = mMessage
        if (mTextLeft.isNullOrEmpty() && mTextRight.isNullOrEmpty()) {
            contentView.findViewById<LinearLayout>(R.id.ll_button_layout).visibility = View.GONE
        } else {
            contentView.findViewById<LinearLayout>(R.id.ll_button_layout).visibility = View.VISIBLE
            val textLeft = mTextLeft
            if (!textLeft.isNullOrEmpty()) {
                contentView.findViewById<TextView>(R.id.tv_left).apply {
                    text = textLeft
                    visibility = View.VISIBLE
                    setOnClickListener {
                        dismiss()
                        mLeftClickListener?.onClick(this@UniversalDialog, it)
                    }
                }
            }
            val textRight = mTextRight
            if (!textRight.isNullOrEmpty()) {
                contentView.findViewById<TextView>(R.id.tv_right).apply {
                    text = textRight
                    visibility = View.VISIBLE
                    setOnClickListener {
                        dismiss()
                        mRightClickListener?.onClick(this@UniversalDialog, it)
                    }
                }
            }
        }
    }

    interface OnViewClickListener {
        fun onClick(dialog: UniversalDialog, view: View)
    }
}