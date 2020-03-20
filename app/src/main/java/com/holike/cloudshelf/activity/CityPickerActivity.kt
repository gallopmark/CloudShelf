package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import com.holike.cloudshelf.base.HollyActivity
import com.holike.cloudshelf.bean.AMapLocationBean
import com.holike.cloudshelf.fragment.CityPickerFragment
import com.holike.cloudshelf.mvp.presenter.LocationPresenter
import com.holike.cloudshelf.mvp.view.LocationView
import kotlinx.android.synthetic.main.activity_citypicker.*
import pony.xcode.citypicker.adapter.OnPickListener
import pony.xcode.citypicker.model.City
import pony.xcode.citypicker.model.LocateState
import pony.xcode.citypicker.model.LocatedCity

//城市选择页面
class CityPickerActivity : HollyActivity<LocationPresenter, LocationView>(), LocationView {

    companion object {
        private const val TAG = "city-picker"

        //当前城市
        fun openForResult(act: BaseActivity, requestCode: Int) {
            act.openActivityForResult(CityPickerActivity::class.java, requestCode)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_citypicker

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        val width = CurrentApp.getInstance().getMaxPixels() / 2
        val lp = pickerLayout.layoutParams as FrameLayout.LayoutParams
        lp.width = width
        pickerLayout.layoutParams = lp
        val cityPickerFragment = CityPickerFragment()
        cityPickerFragment.setOnPickListener(object : OnPickListener {

            override fun onPick(position: Int, data: City?) {
                data?.let { setResult(it) }
            }

            override fun onCancel() {

            }

            override fun onLocate() {

            }
        })
        supportFragmentManager.beginTransaction().add(R.id.picker_container, cityPickerFragment, TAG).commitAllowingStateLoss()
        mPresenter.onLocate()
    }

    override fun onLocationSuccess(bean: AMapLocationBean) {
        val fragment = supportFragmentManager.findFragmentByTag(TAG)
        if (fragment != null && fragment is CityPickerFragment) {
            fragment.locationChanged(LocatedCity(bean.city, bean.province, bean.adcode), LocateState.SUCCESS)
        }
    }

    override fun onLocationFailure(failReason: String?) {
        showShortToast(failReason)
    }

    private fun setResult(city: City) {
        val intent = Intent()
        intent.putExtra("province", city.province)
        intent.putExtra("city", city.name)
        setResult(RESULT_OK, intent)
        finish()
    }
}