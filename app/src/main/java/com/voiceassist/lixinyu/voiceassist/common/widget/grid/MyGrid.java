package com.voiceassist.lixinyu.voiceassist.common.widget.grid;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.voiceassist.lixinyu.voiceassist.utils.KGLog;

/**
 * Created by lilidan on 2018/1/22.
 */

public class MyGrid extends LinearLayout {

    private Context mContext;

    private BaseGridAdapter mAdapter;


    public MyGrid(Context context) {
        super(context);
        init(context);
    }

    public MyGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyGrid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.mContext = context;

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        setOrientation(VERTICAL);
    }

    public void setAdapter(BaseGridAdapter adapter) {
        this.mAdapter = adapter;

        notifyView();
    }

    public void notifyView() {
        int count = mAdapter.getCount();
        int columCount = mAdapter.getColumCount();

        int lineCount = count % columCount == 0 ? count / columCount : count / columCount + 1;
        int lineHeight = getHeight() / lineCount;

        int width = getWidth();
        int itemWidth = width / columCount;

        KGLog.d("aaa", "width--->" + width);
        KGLog.i("aaa", "itemWidth--->" + itemWidth);
        KGLog.v("aaa", "lineHeight--->" + lineHeight);
        KGLog.w("aaa", "getHeight()--->" + getHeight());

        LinearLayout ll = null;
        for (int i = 0; i < count; i++) {
            if (i % columCount == 0) {
                ll = new LinearLayout(mContext);
                ll.setOrientation(HORIZONTAL);
                LayoutParams lpLine = new LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
                addView(ll, lpLine);
            }


            LayoutParams lpColum = new LayoutParams(itemWidth, LayoutParams.MATCH_PARENT);
            ll.addView(mAdapter.getView(i), lpColum);
        }
    }
}
