package com.voiceassist.lixinyu.voiceassist.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.utils.KGLog;

import java.util.Collections;
import java.util.List;

/**
 * Created by lilidan on 2018/2/2.
 */

public class RelationshipListAdapter extends NodeListAdapter implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private int mFromPosition = -1;
    private int mToPosition = -1;

    private OnItemMoveCompleteListener mOnItemMoveCompleteListener;

    public RelationshipListAdapter(Context context, List<Node> data) {
        super(context, data);
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
        source.itemView.setScaleX(0.9f);
        source.itemView.setScaleY(0.9f);
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
