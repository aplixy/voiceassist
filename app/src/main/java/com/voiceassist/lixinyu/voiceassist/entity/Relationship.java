package com.voiceassist.lixinyu.voiceassist.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class Relationship implements Serializable {

    public String firstLevelNodeId;
    public List<SecondLevelNode> secondLevelNodes;
}
