package com.voiceassist.lixinyu.voiceassist.settings.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;

import java.util.List;

/**
 * Created by lilidan on 2018/2/3.
 */

public class RelationshipLevel2Adapter extends RelationshipListAdapter {
    public RelationshipLevel2Adapter(Context context, List<Node> data) {
        super(context, data);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

        viewHolder.ivIcon.setImageResource(R.drawable.level2);

        return viewHolder;
    }
}
