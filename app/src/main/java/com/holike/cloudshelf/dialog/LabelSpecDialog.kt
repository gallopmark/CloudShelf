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
import com.holike.cloudshelf.enumc.ProductCatalog
import pony.xcode.base.CommonDialog


class LabelSpecDialog(context: Context) : CommonDialog(context, R.style.AppDialogStyle) {

    private val mDataList = ArrayList<LabelSpec>()
    private var mSelected: ArrayMap<String, LabelSpec.Spec>? = null
    private var mDictCode: String? = null
    private var mListener: OnConfirmListener? = null
    private var mDataChanged = false

    override fun getLayoutResourceId(): Int = R.layout.dialog_optional

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getWindowAnimations(): Int = R.style.BottomDialogAnimation

    override fun getWidth(): Int = CurrentApp.getInstance().getMaxPixels()

    fun withData(data: MutableList<LabelSpec>?, selected: ArrayMap<String, LabelSpec.Spec>?): LabelSpecDialog {
        if (!data.isNullOrEmpty()) {
            mDataList.addAll(data)
        }
        this.mSelected = selected
        return this
    }

    fun withDictCode(dictCode: String?): LabelSpecDialog {
        this.mDictCode = dictCode
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
        adapter = if (!selected.isNullOrEmpty()) {
            SpecListAdapter(mContext, mDataList, selected)
        } else {
            SpecListAdapter(mContext, mDataList)
        }
        adapter.setOnSpecItemClickListener(object : SpecListAdapter.OnSpecItemClickListener() {
            override fun onSpecSelected(targetPos: Int, selected: ArrayMap<String, LabelSpec.Spec>) {
                mDataChanged = true
                if (mDictCode == ProductCatalog.AMBRY && targetPos == 0) {
                    val spec = selected[ProductCatalog.DICT_CUPBOARD_TYPE]
                    spec?.let {
                        val dictCode = it.id
                        if (dictCode == ProductCatalog.AMBRY_CUSTOM_MADE || dictCode == ProductCatalog.AMBRY_APPLIANCES) {
                            adapter.onCupboardTypeSelected(dictCode, null)
                        }
                    }
                }
            }
        })
        recyclerView.adapter = adapter
        contentView.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dismiss() }
        contentView.findViewById<TextView>(R.id.tv_finish).setOnClickListener {
            dismiss()
            mListener?.onConfirm(adapter.getSelected(), mDataChanged)
        }
    }

    interface OnConfirmListener {
        fun onConfirm(selected: ArrayMap<String, LabelSpec.Spec>, isDataChanged: Boolean)
    }
}