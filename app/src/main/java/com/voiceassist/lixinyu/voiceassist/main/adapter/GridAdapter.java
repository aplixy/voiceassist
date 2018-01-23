package com.voiceassist.lixinyu.voiceassist.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;

import java.util.List;

/**
 * Created by lilidan on 2018/1/22.
 */

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData;

    private int mLineCount;


    public GridAdapter(Context context, List<String> data, int columCount) {
        this.mContext = context;
        this.mData = data;

        int size = null != data ? data.size() : 0;
        this.mLineCount = size % columCount == 0 ? size / columCount : size / columCount + 1;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_item, null);



            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.main_item_textview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tv.setText(mData.get(position));

        //核心代码
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() / mLineCount);
        convertView.setLayoutParams(param);


        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
