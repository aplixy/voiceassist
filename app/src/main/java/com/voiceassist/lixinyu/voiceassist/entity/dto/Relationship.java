package com.voiceassist.lixinyu.voiceassist.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class Relationship implements Serializable, INodeId {

    public String firstLevelNodeId;
    public List<SecondLevelNode> secondLevelNodes;

    @Override
    public String getNodeId() {
        return firstLevelNodeId;
    }

    @Override
    public int getNodeType() {
        return NODE_TYPE_FIRST_LEVEL;
    }
}
