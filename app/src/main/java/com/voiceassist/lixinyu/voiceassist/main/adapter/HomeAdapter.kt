package com.voiceassist.lixinyu.voiceassist.main.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R

/**
 * Created by lilidan on 2018/1/22.
 */

internal class HomeAdapter(private val mContext: Context, private val mData: List<String>) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.main_item_first_level, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv.text = mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    internal inner class MyViewHolder(view: View) : ViewHolder(view) {

        var tv: TextView

        init {
            tv = view.findViewById<View>(R.id.main_item_first_level_textview) as TextView
        }
    }
}
