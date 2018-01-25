package com.voiceassist.lixinyu.voiceassist.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;

import java.util.List;

/**
 * Created by lilidan on 2018/1/22.
 */

public class NodeListAdapter extends RecyclerView.Adapter<NodeListAdapter.MyViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<Node> mData;

    private OnItemClickListener mOnItemClickListener;

    public NodeListAdapter(Context context, List<Node> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.edit_node_item, parent, false));

        holder.root.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Node node = mData.get(position);
        if (null != node) holder.tv.setText(node.cnName);

        holder.root.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (null != mOnItemClickListener) {
            mOnItemClickListener.onClick(this, position, mData.get(position));
        }
    }

    class MyViewHolder extends ViewHolder {

        TextView tv;
        LinearLayout root;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.edit_node_item_textview);
            root = view.findViewById(R.id.edit_node_item_root);
        }
    }

    public interface OnItemClickListener {
        void onClick(NodeListAdapter adapter, int position, Node node);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
