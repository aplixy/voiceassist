package com.voiceassist.lixinyu.voiceassist.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo;
import com.voiceassist.lixinyu.voiceassist.utils.KGLog;

import java.util.List;

/**
 * Created by lilidan on 2018/1/22.
 */

public class GridAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "GridAdapter";

    private Context mContext;
    private List<GridViewVo> mData;

    private int mLineCount;

    private OnItemClickListener mOnItemClickListener;


    public GridAdapter(Context context, List<GridViewVo> data, int columCount) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.main_item, null);

        convertView.setLayoutParams(new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, parent.getHeight() / mLineCount));

        convertView.setTag(R.id.grid_index_tag, position);
        convertView.setOnClickListener(this);

        TextView tv = convertView.findViewById(R.id.main_item_textview);
        tv.setText(mData.get(position).node.cnName);





        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (int) (v.getTag(R.id.grid_index_tag));
        KGLog.d(TAG, "position--->" + position);

        if (null != mOnItemClickListener) {
            mOnItemClickListener.onClick(position, mData.get(position));
        }
    }


    class ViewHolder {
        TextView tv;
    }

    public interface OnItemClickListener {
        void onClick(int position, GridViewVo vo);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }
}
