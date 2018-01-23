package com.voiceassist.lixinyu.voiceassist.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.vo.GridViewVo;

import java.util.List;

/**
 * Created by lilidan on 2018/1/23.
 */

public class GridAdapterSecondLevel extends GridAdapter {

    public GridAdapterSecondLevel(Context context, List<GridViewVo> data, int columCount) {
        super(context, data, columCount);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_item_second_level;
    }

    @Override
    protected void renderView(View convertView, GridViewVo vo) {
        TextView tv = convertView.findViewById(R.id.main_item_second_level_textview);
        tv.setText(vo.node.cnName);
    }
}
