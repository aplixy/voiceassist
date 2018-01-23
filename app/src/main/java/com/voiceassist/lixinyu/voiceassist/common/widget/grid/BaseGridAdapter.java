package com.voiceassist.lixinyu.voiceassist.common.widget.grid;

import android.view.View;

/**
 * Created by lilidan on 2018/1/22.
 */

public interface BaseGridAdapter {

    int getColumCount();
    int getCount();
    View getView(int position);
}
