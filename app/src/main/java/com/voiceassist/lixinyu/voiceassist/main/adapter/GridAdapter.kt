package com.voiceassist.lixinyu.voiceassist.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.BaseAdapter

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo
import com.voiceassist.lixinyu.voiceassist.utils.PlayerUtils

/**
 * Created by lilidan on 2018/1/22.
 */

abstract class GridAdapter(private val mContext: Context, private val mData: List<GridViewVo>?, columCount: Int) : BaseAdapter(), View.OnClickListener {

    private val mLineCount: Int

    private var mOnItemClickListener: OnItemClickListener? = null

    protected abstract val layoutId: Int


    init {

        val size = mData?.size ?: 0
        this.mLineCount = if (size % columCount == 0) size / columCount else size / columCount + 1
    }

    override fun getCount(): Int {
        return mData!!.size
    }

    override fun getItem(position: Int): Any {
        return mData!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //convertView = LayoutInflater.from(mContext).inflate(R.layout.main_item_first_level, null);
        convertView = LayoutInflater.from(mContext).inflate(layoutId, null)

        convertView?.layoutParams = AbsListView.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, parent.height / 3 - 18)

        convertView?.setTag(R.id.grid_index_tag, position)
        convertView?.setOnClickListener(this)

        //        TextView tv = convertView.findViewById(R.id.main_item_textview);
        //        tv.setText(mData.get(position).node.cnName);

        renderView(convertView, mData!![position])

        return convertView
    }

    protected abstract fun renderView(convertView: View, vo: GridViewVo)

    override fun onClick(v: View) {
        val position = v.getTag(R.id.grid_index_tag) as Int

        val vo = mData!![position]

        if (null != vo && null != vo.node && null != vo.node.audioPath) {
            PlayerUtils.getInstance(mContext)?.play(vo.node.audioPath)
        }


        if (null != mOnItemClickListener) {
            mOnItemClickListener!!.onClick(position, vo)
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int, vo: GridViewVo?)
    }

    fun setOnItemClickListener(l: OnItemClickListener?) {
        this.mOnItemClickListener = l
    }

    companion object {

        private val TAG = "GridAdapter"
    }
}
