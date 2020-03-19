package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.TableModelHouseBean
import com.holike.cloudshelf.mvp.presenter.MultiTypePresenter
import com.holike.cloudshelf.mvp.view.MultiTypeView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.activity_multi_type.*
import kotlinx.android.synthetic.main.include_backtrack2.*
import kotlinx.android.synthetic.main.include_main_layout.*

//方案库
class MultiTypeActivity : HollyActivity<MultiTypePresenter, MultiTypeView>(), MultiTypeView, OnLoadMoreListener {

    companion object {
        const val TYPE_PROGRAM = "type-program"  //方案库类型
        const val TYPE_PRODUCT = "type-product"  //产品大全

        fun open(act: BaseActivity, type: String?, @DrawableRes icon: Int, title: String?) {
            open(act, type, icon, title, null)
        }

        fun open(act: BaseActivity, type: String?, @DrawableRes icon: Int, title: String?, templateId: String?) {
            val intent = Intent(act, MultiTypeActivity::class.java)
            intent.type = type
            intent.putExtra("icon", icon)
            intent.putExtra("title", title)
            intent.putExtra("templateId", templateId)
            act.startActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_multi_type

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        mPresenter.init(this)
        mPresenter.initTitle(typeTView)
        mPresenter.initRView(this, centerRView, bottomRView)
        startAnim()
        mPresenter.initData()
        refreshLayout.setOnLoadMoreListener(this)
    }

    private fun startAnim() {
        topLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top))
        centerRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_slow))
        bottomRView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
        view_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bottom_to_top_more_slow))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter.onLoadMore()
    }

    override fun onTableModelHouseResponse(bean: TableModelHouseBean, isLoadMoreEnabled: Boolean) {
        countTView.text = String.format(getString(R.string.text_program_count, bean.total))
        hideDefaultPage()
        if (refreshLayout.visibility != View.VISIBLE) {
            refreshLayout.visibility = View.VISIBLE
        }
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(isLoadMoreEnabled)
    }

    override fun onTableModelHouseFailure(failReason: String?, isInit: Boolean) {
        refreshLayout.finishLoadMore()
        if (isInit) {
            countTView.text = String.format(getString(R.string.text_program_count, "0"))
            refreshLayout.visibility = View.GONE
            onNetworkError(failReason)
        } else {
            if (refreshLayout.visibility != View.VISIBLE) {
                refreshLayout.visibility = View.VISIBLE
            }
            showShortToast(failReason)
        }
    }

    override fun onShowLoading() {
        showLoading()
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    //无查询结果
    override fun onNoQueryResults() {
        refreshLayout.visibility = View.GONE
        onNoResult()
    }

    //打开方案详情页面
    override fun onOpenProgramLib(id: String?) {
        multiTypeContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_to_bottom))
        multiTypeContent.postDelayed({ PicturePreviewActivity.openProgramLib(this@MultiTypeActivity, id) }, 400)
    }

    override fun onReload() {
        mPresenter.initData()
    }
}