package com.voiceassist.lixinyu.voiceassist.entity.dto;

import java.io.Serializable;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class SecondLevelNode implements Serializable, INodeId {
    public String secondLevelNodeId;

    @Override
    public String getNodeId() {
        return secondLevelNodeId;
    }
}
