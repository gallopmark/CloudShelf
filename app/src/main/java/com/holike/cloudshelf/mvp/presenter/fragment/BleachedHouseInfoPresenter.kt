package com.holike.cloudshelf.mvp.presenter.fragment

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.holike.cloudshelf.base.PicturePagerPresenter
import com.holike.cloudshelf.bean.BleachedHouseInfoBean
import com.holike.cloudshelf.mvp.model.BleachedHouseModel
import com.holike.cloudshelf.mvp.view.fragment.BleachedHouseInfoView
import com.holike.cloudshelf.netapi.HttpRequestCallback
import io.reactivex.disposables.Disposable


class BleachedHouseInfoPresenter : PicturePagerPresenter<BleachedHouseModel, BleachedHouseInfoView>() {

    fun resizeContent(container: FrameLayout, infoLayout: LinearLayout) {
        val lp = container.layoutParams as FrameLayout.LayoutParams
        lp.width = mParamWidth
        lp.height = mParamHeight
        container.layoutParams = lp
        val ilp = infoLayout.layoutParams as FrameLayout.LayoutParams
        ilp.width = mParamWidth
        infoLayout.layoutParams = ilp
    }

    fun getBleachedHouseInfo(blueprintId: String?) {
        mModel.getBleachedHouseInfo(blueprintId, object : HttpRequestCallback<BleachedHouseInfoBean>() {
            override fun onStart(d: Disposable?) {
                view?.onShowLoading()
            }

            override fun onSuccess(result: BleachedHouseInfoBean, message: String?) {
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