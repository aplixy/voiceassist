package com.voiceassist.lixinyu.voiceassist.main.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

/**
 * Created by lilidan on 2018/1/22.
 */

class MainPagerAdapter(list: ArrayList<View>?) : PagerAdapter() {
    private val mList: ArrayList<View>

    init {
        var list = list
        if (null == list) list = ArrayList()
        this.mList = list
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = mList[position]
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        container.addView(v, lp)
        return v
    }

    //    @Override
    //    public int getItemPosition(Object object) {
    //        return POSITION_NONE;
    //    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val v = mList[position]
        container.removeView(v)
    }
}
