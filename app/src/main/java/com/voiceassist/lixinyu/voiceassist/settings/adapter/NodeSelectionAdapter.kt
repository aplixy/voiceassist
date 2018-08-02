package com.voiceassist.lixinyu.voiceassist.settings.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.entity.vo.NodeSelectVo

/**
 * Created by lilidan on 2018/1/22.
 */

internal class NodeSelectionAdapter(private val mContext: Context, private val mData: List<NodeSelectVo>?) : RecyclerView.Adapter<NodeSelectionAdapter.MyViewHolder>(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.node_selection_item, parent, false))

        holder.root.setOnClickListener(this)

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val vo = mData?.get(position)
        if (null != vo) {
            if (null != vo.node) holder.tv.text = vo.node.cnName

            holder.checkBox.isChecked = vo.isSelected

            holder.root.tag = position
        }

    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun onClick(v: View) {
        val position = v.tag as Int

        val vo = mData?.get(position)
        if (null != vo) {
            vo.isSelected = !vo.isSelected
            notifyDataSetChanged()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

    }

    internal inner class MyViewHolder(view: View) : ViewHolder(view) {

        var root: ViewGroup
        var checkBox: CheckBox
        var tv: TextView

        init {
            root = view.findViewById(R.id.node_selection_item_root)
            checkBox = view.findViewById(R.id.node_selection_item_checkbox)
            tv = view.findViewById(R.id.node_selection_item_textview)
        }
    }

    interface OnItemClickListener {
        fun onClick(adapter: NodeSelectionAdapter, position: Int, node: Node)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}
