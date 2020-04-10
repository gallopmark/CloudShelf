package com.holike.cloudshelf.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.activity.VideoPlayerActivity
import com.holike.cloudshelf.activity.WebViewActivity
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyFragment
import com.holike.cloudshelf.bean.ProductCatalogInfoBean
import com.holike.cloudshelf.bean.TableModelDetailBean
import com.holike.cloudshelf.mvp.presenter.fragment.ContentInfoPresenter
import com.holike.cloudshelf.mvp.view.fragment.ContentInfoView
import kotlinx.android.synthetic.main.fragment_content_info.*
import kotlinx.android.synthetic.main.include_bottom_images_layout.*
import kotlinx.android.synthetic.main.include_qrcode_layout.*

//详情页-方案库各方案详情、产品大全各模块的方案详情
class ContentInfoFragment : HollyFragment<ContentInfoPresenter, ContentInfoView>(), ContentInfoView {
    //底部弹出动画
    private lateinit var mAnimation: Animation

    override fun getLayoutResourceId(): Int = R.layout.fragment_content_info
    override fun getBacktrackResource(): Int = R.layout.include_backtrack

    override fun setup(savedInstanceState: Bundle?) {
        mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_from_bottom)
        mPresenter.resizeContainer(pictureContainer)
        mPresenter.initVp(previewVP)
        mPresenter.initBottomRV(bottomRView)
        getDataByType()
        backtrackTView.setOnClickListener { onBackPressed() }
        preIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getLeftScrollNum()) }
        nextIView.setOnClickListener { bottomRView.scrollToPosition(mPresenter.getRightScrollNum()) }
    }

    private fun startLayoutAnimation() {
        miniQrLayout.startAnimation(mAnimation)
        backtrackTView.startAnimation(mAnimation)
    }

    private fun getDataByType() {
        arguments?.let {
            val contentType = it.getString("content-type")
            val id = it.getString("id")
            if (contentType == "type-program") {
                mPresenter.getTableModelDetail(id)
            } else if (contentType == "type-product") {
                mPresenter.getProductCatalogInfo(id)
            }
        }
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

    override fun onShowLoading() {
        showLoading(true)
    }

    override fun onDismissLoading() {
        dismissLoading()
    }

    override fun onRequestSuccess() {
        removeBacktrack()
        showContentView()
        startLayoutAnimation()
    }

    override fun onTableModelResp(bean: TableModelDetailBean) {
        titleTView.text = bean.title
        Glide.with(this).load(bean.miniQrCode).apply(RequestOptions().error(R.mipmap.ic_wxacode)).into(miniQrUrlIView)
        updatePanoramic(bean.viewUrl)
        updateBottomRView(bean.obtainImages())
    }

    override fun onProductCatalogResp(bean: ProductCatalogInfoBean) {
        Glide.with(this).load(bean.miniQrUrl).apply(RequestOptions().error(R.mipmap.ic_wxacode)).into(miniQrUrlIView)
        bean.info?.let {
            titleTView.text = it.title
            updatePanoramic(it.vrUrl)
            updateVideo(it.videoPic, it.videoUrl)
            updateBottomRView(it.getImageList())
        }
    }

    private fun updatePanoramic(vrUrl: String?) {
        if (!vrUrl.isNullOrEmpty()) { //全景url不为空 则显示全景图按钮
            panoramicTView.visibility = View.VISIBLE
            panoramicTView.startAnimation(mAnimation)
            panoramicTView.setOnClickListener { WebViewActivity.open(mContext as BaseActivity, vrUrl) }
        } else {
            panoramicTView.visibility = View.GONE
        }
    }

    private fun updateVideo(videoPic: String?, videoUrl: String?) {
        if (!videoUrl.isNullOrEmpty()) {
            videoTView.visibility = View.VISIBLE
            videoTView.startAnimation(mAnimation)
            videoTView.setOnClickListener { VideoPlayerActivity.open(mContext as BaseActivity, videoUrl, videoPic) }
        } else {
            videoTView.visibility = View.GONE
        }
    }

    private fun updateBottomRView(images: MutableList<String>) {
        if (images.isNotEmpty()) {
            bottomLayout.visibility = View.VISIBLE
            bottomLayout.startAnimation(mAnimation)
            if (images.size > 7) {
                val lp = bottomRView.layoutParams as LinearLayout.LayoutParams
                lp.width = getDimensionPixelSize(R.dimen.dp_100) * 6 + getDimensionPixelSize(R.dimen.dp_20) * 6 + getDimensionPixelSize(
                        R.dimen.dp_130
                )
                bottomRView.layoutParams = lp
            }
        }
    }

    override fun onFailure(failReason: String?) {
        showLongToast(failReason)
    }
}