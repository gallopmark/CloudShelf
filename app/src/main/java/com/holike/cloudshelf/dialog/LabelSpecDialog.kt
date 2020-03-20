package com.holike.cloudshelf.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.adapter.SpecListAdapter
import com.holike.cloudshelf.bean.internal.LabelSpec
import pony.xcode.base.CommonDialog


class LabelSpecDialog(context: Context) : CommonDialog(context, R.style.AppDialogStyle) {

    private val mDataList = ArrayList<LabelSpec>()
    private var mSelected: ArrayMap<String, LabelSpec.Spec>? = null
    private var mListener: OnConfirmListener? = null

    override fun getLayoutResourceId(): Int = R.layout.dialog_optional

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getWindowAnimations(): Int = R.style.BottomDialogAnimation
    override fun getWidth(): Int = CurrentApp.getInstance().getMaxPixels()
    fun withData(
        data: MutableList<LabelSpec>?,
        selected: ArrayMap<String, LabelSpec.Spec>?
    ): LabelSpecDialog {
        if (!data.isNullOrEmpty()) {
            mDataList.addAll(data)
        }
        this.mSelected = selected
        return this
    }

    fun listen(l: OnConfirmListener?): LabelSpecDialog {
        this.mListener = l
        return this
    }

    override fun initView(contentView: View) {
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val selected = mSelected
        val adapter: SpecListAdapter
        if (!selected.isNullOrEmpty()) {
            adapter = SpecListAdapter(mContext, mDataList, selected)
        } else {
            adapter = SpecListAdapter(mContext, mDataList)
        }
        recyclerView.adapter = adapter
        contentView.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dismiss() }
        contentView.findViewById<TextView>(R.id.tv_finish).setOnClickListener {
            dismiss()
            mListener?.onConfirm(adapter.getSelected())
        }
    }

    interface OnConfirmListener {
        fun onConfirm(map: ArrayMap<String, LabelSpec.Spec>)
    }
}