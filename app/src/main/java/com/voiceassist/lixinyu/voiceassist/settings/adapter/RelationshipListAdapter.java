package com.voiceassist.lixinyu.voiceassist.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.utils.KGLog;

import java.util.Collections;
import java.util.List;

/**
 * Created by lilidan on 2018/2/2.
 */

public class RelationshipListAdapter extends RecyclerView.Adapter<RelationshipListAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter, View.OnClickListener {

    private int mFromPosition = -1;
    private int mToPosition = -1;

    private OnItemMoveCompleteListener mOnItemMoveCompleteListener;

    private ItemTouchHelper mTouchHelper;


    private boolean mIsAllowSortOrder;



    protected Context mContext;
    protected List<Node> mData;

    private OnItemClickListener mOnItemClickListener;

    public RelationshipListAdapter(Context context, List<Node> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_relationship, parent, false));

        holder.itemView.setOnClickListener(this);

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mIsAllowSortOrder && null != mTouchHelper && event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchHelper.startDrag(holder);
                }

                return false;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Node node = mData.get(position);
        if (null != node) holder.tv.setText(node.cnName);

        holder.itemView.setTag(position);

        if (mIsAllowSortOrder) {
            holder.ivDrag.setVisibility(View.VISIBLE);
            holder.vgLevel.setVisibility(View.INVISIBLE);
        } else {
            holder.ivDrag.setVisibility(View.INVISIBLE);
            holder.vgLevel.setVisibility(View.VISIBLE);
        }
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

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tv;
        ImageView ivDrag;

        ViewGroup vgLevel;

        public MyViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.item_relationship_imageview_icon);
            tv = view.findViewById(R.id.item_relationship_textview);
            ivDrag = view.findViewById(R.id.item_relationship_imageview_drag);
            vgLevel = view.findViewById(R.id.item_relationship_viewgroup_level);
        }
    }

    public interface OnItemClickListener {
        void onClick(RelationshipListAdapter adapter, int position, Node node);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //=======================================================

    public void setItemTouchHelper(ItemTouchHelper touchHelper) {
        this.mTouchHelper = touchHelper;
    }

    public void setAllowSortOrder(boolean isAllowSortOrder) {
        this.mIsAllowSortOrder = isAllowSortOrder;
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < mData.size() && toPosition < mData.size()) {
            //交换数据位置
            Collections.swap(mData, fromPosition, toPosition);
            //刷新位置交换
            notifyItemMoved(fromPosition, toPosition);

            Object sourceTag = source.itemView.getTag();
            Object targetTag = target.itemView.getTag();

            source.itemView.setTag(targetTag);
            target.itemView.setTag(sourceTag);
        }

        mToPosition = toPosition;

        //移动过程中移除view的放大效果
        //onItemClear(source);
    }

    @Override
    public void onItemDissmiss(RecyclerView.ViewHolder source) {
        int position = source.getAdapterPosition();
        mData.remove(position); //移除数据
        notifyItemRemoved(position);//刷新数据移除
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder source) {
        KGLog.d("--->onItemSelect");

        mFromPosition = source.getAdapterPosition();

        //当拖拽选中时放大选中的view
        source.itemView.setScaleX(1.05f);
        source.itemView.setScaleY(1.05f);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder source) {
        KGLog.i("--->onItemClear");

        mToPosition = source.getAdapterPosition();

        //拖拽结束后恢复view的状态
        source.itemView.setScaleX(1.0f);
        source.itemView.setScaleY(1.0f);

        if (null != mOnItemMoveCompleteListener) {
            mOnItemMoveCompleteListener.onComplete(mFromPosition, mToPosition);
        }

        mFromPosition = -1;
        mToPosition = -1;
    }


    public interface OnItemMoveCompleteListener {
        void onComplete(int formPosition, int toPosition);
    }

    public void setOnItemMoveCompleteListener(OnItemMoveCompleteListener listener) {
        mOnItemMoveCompleteListener = listener;
    }
}
