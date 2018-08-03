package com.voiceassist.lixinyu.voiceassist.common.widget.grid

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

import com.voiceassist.lixinyu.voiceassist.utils.KGLog

/**
 * Created by lilidan on 2018/1/22.
 */

class MyGrid : LinearLayout {

    private var mContext: Context? = null

    private var mAdapter: BaseGridAdapter? = null


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {

        this.mContext = context

        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams = lp
        orientation = LinearLayout.VERTICAL
    }

    fun setAdapter(adapter: BaseGridAdapter) {
        this.mAdapter = adapter

        notifyView()
    }

    fun notifyView() {
        val count = mAdapter!!.count
        val columCount = mAdapter!!.columCount

        val lineCount = if (count % columCount == 0) count / columCount else count / columCount + 1
        val lineHeight = height / lineCount

        val width = width
        val itemWidth = width / columCount

        KGLog.d("aaa", "width--->$width")
        KGLog.i("aaa", "itemWidth--->$itemWidth")
        KGLog.v("aaa", "lineHeight--->$lineHeight")
        KGLog.w("aaa", "getHeight()--->$height")

        var ll: LinearLayout? = null
        for (i in 0 until count) {
            if (i % columCount == 0) {
                ll = LinearLayout(mContext)
                ll.orientation = LinearLayout.HORIZONTAL
                val lpLine = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lineHeight)
                addView(ll, lpLine)
            }


            val lpColum = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT)
            ll!!.addView(mAdapter!!.getView(i), lpColum)
        }
    }
}
