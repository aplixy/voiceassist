package com.voiceassist.lixinyu.voiceassist.settings.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.utils.KGLog

import java.util.Collections

/**
 * Created by lilidan on 2018/2/2.
 */

open class RelationshipListAdapter(protected var mContext: Context, protected var mData: MutableList<Node>?) : RecyclerView.Adapter<RelationshipListAdapter.MyViewHolder>(), SimpleItemTouchHelperCallback.ItemTouchHelperAdapter, View.OnClickListener {

    private var mFromPosition = -1
    private var mToPosition = -1

    //private var mOnItemMoveCompleteListener: OnItemMoveCompleteListener? = null

    private var mTouchHelper: ItemTouchHelper? = null


    private var mIsAllowSortOrder: Boolean = false

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_relationship, parent, false))

        holder.itemView.setOnClickListener(this)

        holder.itemView.setOnTouchListener { v, event ->
            if (mIsAllowSortOrder && null != mTouchHelper && event.action == MotionEvent.ACTION_DOWN) {
                mTouchHelper!!.startDrag(holder)
            }

            false
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val node = mData?.get(position)
        holder.tv.text = node?.cnName

        holder.itemView.tag = position

        if (mIsAllowSortOrder) {
            holder.ivDrag.visibility = View.VISIBLE
            holder.vgLevel.visibility = View.INVISIBLE
        } else {
            holder.ivDrag.visibility = View.INVISIBLE
            holder.vgLevel.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun onClick(v: View) {
        val position = v.tag as Int
        if (null != mOnItemClickListener) {
            mOnItemClickListener!!.onClick(this, position, mData?.get(position))
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivIcon: ImageView
        var tv: TextView
        var ivDrag: ImageView

        var vgLevel: ViewGroup

        init {
            ivIcon = view.findViewById(R.id.item_relationship_imageview_icon)
            tv = view.findViewById(R.id.item_relationship_textview)
            ivDrag = view.findViewById(R.id.item_relationship_imageview_drag)
            vgLevel = view.findViewById(R.id.item_relationship_viewgroup_level)
        }
    }

    interface OnItemClickListener {
        fun onClick(adapter: RelationshipListAdapter, position: Int, node: Node?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    //=======================================================

    fun setItemTouchHelper(touchHelper: ItemTouchHelper) {
        this.mTouchHelper = touchHelper
    }

    fun setAllowSortOrder(isAllowSortOrder: Boolean) {
        this.mIsAllowSortOrder = isAllowSortOrder
        notifyDataSetChanged()
    }

    override fun onItemMove(source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromPosition = source.adapterPosition
        val toPosition = target.adapterPosition
        if (fromPosition < mData?.size ?: 0 && toPosition < mData?.size ?: 0) {
            //交换数据位置
            Collections.swap(mData, fromPosition, toPosition)
            //刷新位置交换
            notifyItemMoved(fromPosition, toPosition)

            val sourceTag = source.itemView.tag
            val targetTag = target.itemView.tag

            source.itemView.tag = targetTag
            target.itemView.tag = sourceTag
        }

        mToPosition = toPosition

        //移动过程中移除view的放大效果
        //onItemClear(source);
    }

    override fun onItemDissmiss(source: RecyclerView.ViewHolder) {
        val position = source.adapterPosition
        mData?.removeAt(position) //移除数据
        notifyItemRemoved(position)//刷新数据移除
    }

    override fun onItemSelect(source: RecyclerView.ViewHolder?) {
        KGLog.d("--->onItemSelect")

        if (source == null) return

        mFromPosition = source.adapterPosition

        //当拖拽选中时放大选中的view
        source.itemView.scaleX = 1.05f
        source.itemView.scaleY = 1.05f
    }

    override fun onItemClear(source: RecyclerView.ViewHolder) {
        KGLog.i("--->onItemClear")

        mToPosition = source.adapterPosition

        //拖拽结束后恢复view的状态
        source.itemView.scaleX = 1.0f
        source.itemView.scaleY = 1.0f

//        if (null != mOnItemMoveCompleteListener) {
//            mOnItemMoveCompleteListener!!.onComplete(mFromPosition, mToPosition)
//        }

        onMoveComplete?.invoke(mFromPosition, mToPosition)

        mFromPosition = -1
        mToPosition = -1
    }


    interface OnItemMoveCompleteListener {
        fun onComplete(formPosition: Int, toPosition: Int)
    }

//    fun setOnItemMoveCompleteListener(listener: OnItemMoveCompleteListener) {
//        mOnItemMoveCompleteListener = listener
//    }

    private var onMoveComplete: ((Int, Int) -> Unit)? = null

    fun setOnItemMoveCompleteListener(onMoveComplete: (Int, Int) -> Unit) {
        this.onMoveComplete = onMoveComplete
    }
}
