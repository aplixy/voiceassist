package com.voiceassist.lixinyu.voiceassist.settings.adapter

import android.content.Context
import android.view.ViewGroup

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node

/**
 * Created by lilidan on 2018/2/3.
 */

class RelationshipLevel2Adapter(context: Context, data: MutableList<Node>?) : RelationshipListAdapter(context, data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelationshipListAdapter.MyViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)

        viewHolder.ivIcon.setImageResource(R.drawable.level2)

        return viewHolder
    }
}
