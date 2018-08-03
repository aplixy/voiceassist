package com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * 处理RecycleView的选中,拖拽移动,拖拽删除的实现类
 * Created by mChenys on 2017/2/16.
 */
class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter?) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //允许上下的拖动
        //int dragFlags =ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT; //允许左右的拖动
        //int swipeFlags = ItemTouchHelper.LEFT; //只允许从右向左侧滑
        //int swipeFlags = ItemTouchHelper.DOWN; //只允许从上向下侧滑
        //一般使用makeMovementFlags(int,int)或makeFlag(int, int)来构造我们的返回值
        //makeMovementFlags(dragFlags, swipeFlags)

        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT //允许上下左右的拖动
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false//长按启用拖拽
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false //不启用拖拽删除
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        //通过接口传递拖拽交换数据的起始位置和目标位置的ViewHolder
        mAdapter?.onItemMove(source, target)
        return true
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //移动删除回调,如果不用可以不用理
        // mAdapter.onItemDissmiss(viewHolder);
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //当滑动或者拖拽view的时候通过接口返回该ViewHolder
            mAdapter?.onItemSelect(viewHolder)
        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (!recyclerView!!.isComputingLayout) {
            //当需要清除之前在onSelectedChanged或者onChildDraw,onChildDrawOver设置的状态或者动画时通过接口返回该ViewHolder
            mAdapter?.onItemClear(viewHolder)
        }
    }


    /**
     * 定义RecycleView的Adapter和SimpleItemTouchHelperCallback直接交互的接口方法
     * Created by mChenys on 2017/2/16.
     */
    interface ItemTouchHelperAdapter {

        //数据交换
        fun onItemMove(source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)

        //数据删除
        fun onItemDissmiss(source: RecyclerView.ViewHolder)

        //drag或者swipe选中
        fun onItemSelect(source: RecyclerView.ViewHolder?)

        //状态清除
        fun onItemClear(source: RecyclerView.ViewHolder)
    }
}
