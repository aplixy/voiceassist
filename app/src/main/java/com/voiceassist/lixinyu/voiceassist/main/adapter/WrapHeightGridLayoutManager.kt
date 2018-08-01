package com.voiceassist.lixinyu.voiceassist.main.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by lilidan on 2018/1/22.
 */

class WrapHeightGridLayoutManager(context: Context, private val mChildPerLines: Int) : GridLayoutManager(context, mChildPerLines) {
    private val mMeasuredDimension = IntArray(2)

    override fun onMeasure(recycler: RecyclerView.Recycler?, state: RecyclerView.State?, widthSpec: Int, heightSpec: Int) {

        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)
        var height = 0
        var i = 0
        while (i < itemCount) {
            measureScrapChild(recycler!!, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension)
            height = height + mMeasuredDimension[1]
            i = i + mChildPerLines
        }

        // If child view is more than screen size, there is no need to make it wrap content. We can use original onMeasure() so we can scroll view.
        if (height > heightSize) {
            when (heightMode) {
                View.MeasureSpec.EXACTLY -> height = heightSize
            }
            setMeasuredDimension(widthSize, height)
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        }
    }

    private fun measureScrapChild(recycler: RecyclerView.Recycler, position: Int, widthSpec: Int,
                                  heightSpec: Int, measuredDimension: IntArray) {

        val view = recycler.getViewForPosition(position)

        // For adding Item Decor Insets to view
        super.measureChildWithMargins(view!!, 0, 0)
        if (view != null) {
            val p = view.layoutParams as RecyclerView.LayoutParams
            val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                    paddingLeft + paddingRight + getDecoratedLeft(view) + getDecoratedRight(view), p.width)
            val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    paddingTop + paddingBottom + paddingBottom + getDecoratedBottom(view), p.height)
            view.measure(childWidthSpec, childHeightSpec)

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin
            recycler.recycleView(view)
        }
    }
}
