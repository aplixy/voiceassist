package com.voiceassist.lixinyu.voiceassist.entity.vo;

import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;

import java.io.Serializable;

/**
 * Created by lilidan on 2018/1/23.
 */

public class GridViewVo implements Serializable {

    public GridViewVo() {

    }

    public GridViewVo(Node node, Relationship relationship) {
        this.node = node;
        this.relationship = relationship;
    }

    public Node node;
    public Relationship relationship;
}
