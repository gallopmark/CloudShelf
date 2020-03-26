package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.mvp.presenter.GeneralPresenter
import com.holike.cloudshelf.mvp.view.GeneralView
import kotlinx.android.synthetic.main.activity_picture_preview.*
import kotlinx.android.synthetic.main.include_bottom_images_layout.*
import kotlinx.android.synthetic.main.include_miniqr_layout.*


/*详情展示 多模块公用*/
class PicturePreviewActivity : HollyActivity<GeneralPresenter, GeneralView>(), GeneralView {

    companion object {
        const val TYPE_PROGRAM = "type-program"  //方案库类型

        //打开方案库详情
        fun openProgramLib(act: BaseActivity, id: String?) {
            val intent = Intent(act, PicturePreviewActivity::class.java).apply {
                type = TYPE_PROGRAM
                putExtra("id", id)
            }
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_picture_preview

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        when (intent.type) {
            TYPE_PROGRAM -> {
                val id = intent.getStringExtra("id")
                getTableModel(id)
            }
        }
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
    }

    override fun onPageSelected(position: Int, size: Int) {
        val s = String.format(getString(R.string.text_preview_index), position + 1, size)
        val sp = SpannableString(s)
        val span = AbsoluteSizeSpan(getDimensionPixelSize(R.dimen.sp_16))
        sp.setSpan(span, s.indexOf("第") + 1, s.indexOf("/"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        bottomTView.text = sp
    }

    override fun onBottomImageSelected(position: Int) {
        previewVP.currentItem = position
        bottomRView.smoothScrollToPosition(position)
    }

    private fun getTableModel(id: String?) {
        mPresenter.getTableModelDetail(id)
    }

    override fun onTableModelResponse(bean: TableModelDetailBean) {
        bottomLayout.visibility = View.VISIBLE
        titleTView.text = bean.title
        Glide.with(this).load(bean.miniQrCode).apply(RequestOptions().error(R.mipmap.ic_wxacode)).into(miniQrUrlIView)
        if (bean.obtainImages().size > 7) {
            val lp = bottomRView.layoutParams as LinearLayout.LayoutParams
            lp.width = getDimensionPixelSize(R.dimen.dp_100) * 6 + getDimensionPixelSize(R.dimen.dp_20) * 6 + getDimensionPixelSize(R.dimen.dp_130)
            bottomRView.layoutParams = lp
        }
    }

    override fun onTableModelFailure(failReason: String?) {
        showLongToast(failReason)
    }
}