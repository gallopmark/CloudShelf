package com.holike.cloudshelf.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration : ItemDecoration {
    private var mSpanCount: Int
    private var mSpacing: Int
    private var mIncludeEdge: Boolean
    private var mHeaderNum: Int
    private var mFooterNum = 0

    constructor(spanCount: Int, space: Int, includeEdge: Boolean) : this(spanCount, space, includeEdge, 0) {
    }

    constructor(spanCount: Int, spacing: Int, includeEdge: Boolean, headerNum: Int) {
        mSpanCount = spanCount
        mSpacing = spacing
        mIncludeEdge = includeEdge
        mHeaderNum = headerNum
    }

    constructor(spanCount: Int, spacing: Int, includeEdge: Boolean, headerNum: Int, footerNum: Int) {
        mSpanCount = spanCount
        mSpacing = spacing
        mIncludeEdge = includeEdge
        mHeaderNum = headerNum
        mFooterNum = footerNum
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) - mHeaderNum // item position
        var lastPosition = -1
        if (parent.adapter != null) {
            lastPosition = parent.adapter!!.itemCount - mFooterNum
        }
        if (position >= 0 && lastPosition != -1 && position < lastPosition) {
            val column = position % mSpanCount // item column
            if (mIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount // mSpacing - column * ((1f / mSpanCount) * mSpacing)
                outRect.right = (column + 1) * mSpacing / mSpanCount // (column + 1) * ((1f / mSpanCount) * mSpacing)
                if (position < mSpanCount) { // top edge
                    outRect.top = mSpacing
                }
                outRect.bottom = mSpacing // item bottom
            } else {
                outRect.left = column * mSpacing / mSpanCount // column * ((1f / mSpanCount) * mSpacing)
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount // mSpacing - (column + 1) * ((1f /    mSpanCount) * mSpacing)
                if (position >= mSpanCount) {
                    outRect.top = mSpacing // item top
                }
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }
}