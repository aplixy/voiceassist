package com.voiceassist.lixinyu.voiceassist.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.vo.NodeSelectVo;

import java.util.List;

/**
 * Created by lilidan on 2018/1/22.
 */

public class NodeSelectionAdapter extends RecyclerView.Adapter<NodeSelectionAdapter.MyViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    private List<NodeSelectVo> mData;

    private OnItemClickListener mOnItemClickListener;

    public NodeSelectionAdapter(Context context, List<NodeSelectVo> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.node_selection_item, parent, false));

        holder.root.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NodeSelectVo vo = mData.get(position);
        if (null != vo) {
            if (null != vo.node) holder.tv.setText(vo.node.cnName);

            holder.checkBox.setChecked(vo.isSelected);

            holder.root.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        NodeSelectVo vo = mData.get(position);
        if (null != vo) {
            vo.isSelected = !vo.isSelected;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    class MyViewHolder extends ViewHolder {

        ViewGroup root;
        CheckBox checkBox;
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            root = view.findViewById(R.id.node_selection_item_root);
            checkBox = view.findViewById(R.id.node_selection_item_checkbox);
            tv = view.findViewById(R.id.node_selection_item_textview);
        }
    }

    public interface OnItemClickListener {
        void onClick(NodeSelectionAdapter adapter, int position, Node node);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
