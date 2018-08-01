package com.voiceassist.lixinyu.voiceassist.main.adapter

import android.content.Context
import android.view.View
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo

/**
 * Created by lilidan on 2018/1/23.
 */

class GridAdapterFirstLevel(context: Context, data: List<GridViewVo>, columCount: Int) : GridAdapter(context, data, columCount) {

    override val layoutId: Int
        get() = R.layout.main_item_first_level

    override fun renderView(convertView: View, vo: GridViewVo) {
        val tv = convertView.findViewById<TextView>(R.id.main_item_first_level_textview)
        tv.text = vo.node.cnName
    }
}
