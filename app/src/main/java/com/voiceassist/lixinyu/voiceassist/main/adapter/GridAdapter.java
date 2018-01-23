package com.voiceassist.lixinyu.voiceassist.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo;
import com.voiceassist.lixinyu.voiceassist.utils.PlayerUtils;

import java.util.List;

/**
 * Created by lilidan on 2018/1/22.
 */

public abstract class GridAdapter extends BaseAdapter implements View.OnClickListener {

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
        //convertView = LayoutInflater.from(mContext).inflate(R.layout.main_item_first_level, null);
        convertView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);

        convertView.setLayoutParams(new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, parent.getHeight() / 3));

        convertView.setTag(R.id.grid_index_tag, position);
        convertView.setOnClickListener(this);

//        TextView tv = convertView.findViewById(R.id.main_item_textview);
//        tv.setText(mData.get(position).node.cnName);

        renderView(convertView, mData.get(position));

        return convertView;
    }

    protected abstract int getLayoutId();
    protected abstract void renderView(View convertView, GridViewVo vo);

    @Override
    public void onClick(View v) {
        int position = (int) (v.getTag(R.id.grid_index_tag));

        GridViewVo vo = mData.get(position);

        if (null != vo && null != vo.node && null != vo.node.audioPath) {
            PlayerUtils.getInstance(mContext).playSound(vo.node.audioPath);
        }


        if (null != mOnItemClickListener) {
            mOnItemClickListener.onClick(position, vo);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, GridViewVo vo);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }
}
