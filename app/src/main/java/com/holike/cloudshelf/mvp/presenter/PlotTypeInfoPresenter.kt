package com.holike.cloudshelf.mvp.presenter

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.holike.cloudshelf.base.PicturePagerPresenter
import com.holike.cloudshelf.bean.PlotTypeInfoBean
import com.holike.cloudshelf.mvp.model.PlotTypeInfoModel
import com.holike.cloudshelf.mvp.view.PlotTypeInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.disposables.Disposable


class PlotTypeInfoPresenter : PicturePagerPresenter<PlotTypeInfoModel, PlotTypeInfoView>() {

    fun resizeContent(container: FrameLayout, infoLayout: LinearLayout) {
        val lp = container.layoutParams as FrameLayout.LayoutParams
        lp.width = mParamWidth
        lp.height = mParamHeight
        container.layoutParams = lp
        val ilp = infoLayout.layoutParams as FrameLayout.LayoutParams
        ilp.width = mParamWidth
        infoLayout.layoutParams = ilp
    }

    fun getPlotTypeInfo(houseTypeId: String?) {
        mModel.getPlotTypeInfoById(houseTypeId, object : HttpRequestCallback<PlotTypeInfoBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: PlotTypeInfoBean, message: String?) {
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