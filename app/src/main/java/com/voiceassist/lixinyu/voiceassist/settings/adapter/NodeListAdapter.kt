package com.voiceassist.lixinyu.voiceassist.settings.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node

/**
 * Created by lilidan on 2018/1/22.
 */

internal class NodeListAdapter(protected var mContext: Context, protected var mData: List<Node>?) : RecyclerView.Adapter<NodeListAdapter.MyViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_edit_node, parent, false))

        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val node = mData?.get(position)
        if (null != node) holder.tv.text = node.cnName

        holder.itemView.tag = position
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

    override fun onLongClick(v: View): Boolean {
        val position = v.tag as Int

        if (null != mOnItemClickListener) {
            mOnItemClickListener!!.onLongClick(this, position, mData?.get(position))
        }

        return false
    }

    internal inner class MyViewHolder(view: View) : ViewHolder(view) {

        var tv: TextView
        var root: LinearLayout

        init {
            tv = view.findViewById<View>(R.id.edit_node_item_textview) as TextView
            root = view.findViewById(R.id.edit_node_item_root)
        }
    }

    interface OnItemClickListener {
        fun onClick(adapter: NodeListAdapter, position: Int, node: Node?)
        fun onLongClick(adapter: NodeListAdapter, position: Int, node: Node?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}
