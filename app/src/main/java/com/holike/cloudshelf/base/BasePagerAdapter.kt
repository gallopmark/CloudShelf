package com.holike.cloudshelf.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
//viewPager baseAdapter
abstract class BasePagerAdapter<T>(var context: Context, var dataList: MutableList<T>) : PagerAdapter() {
    var mInflater = LayoutInflater.from(context)
    override fun getCount(): Int = dataList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = mInflater.inflate(getLayoutResourceId(), container, false)
        convert(view, dataList[position], position)
        container.addView(view)
        return view
    }

    protected abstract fun getLayoutResourceId(): Int
    protected abstract fun convert(convertView: View, bean: T, position: Int)
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}