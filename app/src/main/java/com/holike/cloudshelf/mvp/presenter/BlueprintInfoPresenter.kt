package com.holike.cloudshelf.mvp.presenter

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.holike.cloudshelf.base.PicturePagerPresenter
import com.holike.cloudshelf.bean.BlueprintInfoBean
import com.holike.cloudshelf.mvp.model.BlueprintModel
import com.holike.cloudshelf.mvp.view.BlueprintInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.disposables.Disposable


class BlueprintInfoPresenter : PicturePagerPresenter<BlueprintModel, BlueprintInfoView>() {

    fun resizeContent(container: FrameLayout, infoLayout: LinearLayout) {
        val lp = container.layoutParams as FrameLayout.LayoutParams
        lp.width = mParamWidth
        lp.height = mParamHeight
        container.layoutParams = lp
        val ilp = infoLayout.layoutParams as FrameLayout.LayoutParams
        ilp.width = mParamWidth
        infoLayout.layoutParams = ilp
    }

    fun getBlueprintInfo(blueprintId: String?) {
        mModel.getBlueprintInfo(blueprintId, object : HttpRequestCallback<BlueprintInfoBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: BlueprintInfoBean, message: String?) {
                view?.onSuccess(result)
                updatePreviewImages(result.obtainImageList(mBottomImageWidth, mBottomImageHeight))
            }

            override fun onFailure(code: Int, failReason: String?) {
                view?.onFailure(failReason)
            }

            override fun onCompleted() {
                view?.onDismissLoading()
            }
        })
    }

    override fun onPageSelected(position: Int, size: Int) {
        view?.onPageSelected(position, size)
    }

    override fun onBottomImageSelected(position: Int) {
        view?.onBottomImageSelected(position)
    }
}