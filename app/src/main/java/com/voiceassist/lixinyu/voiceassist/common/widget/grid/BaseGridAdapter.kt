package com.voiceassist.lixinyu.voiceassist.common.widget.grid

import android.view.View

/**
 * Created by lilidan on 2018/1/22.
 */

interface BaseGridAdapter {

    val columCount: Int
    val count: Int
    fun getView(position: Int): View
}
