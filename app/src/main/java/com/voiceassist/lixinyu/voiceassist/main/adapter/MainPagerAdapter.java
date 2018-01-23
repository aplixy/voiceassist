package com.voiceassist.lixinyu.voiceassist.main.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by lilidan on 2018/1/22.
 */

public class MainPagerAdapter extends PagerAdapter {
    private ArrayList<View> mList;

    public MainPagerAdapter(ArrayList<View> list) {
        if (null == list) list = new ArrayList<>();
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View v = mList.get(position);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(v, lp);
        return v;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        View v = mList.get(position);
        container.removeView(v);
    }
}
