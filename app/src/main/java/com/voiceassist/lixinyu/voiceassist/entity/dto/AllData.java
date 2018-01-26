package com.voiceassist.lixinyu.voiceassist.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class AllData implements Serializable {

    public int version;

    public List<Node> nodes;
    public List<Relationship> relationship;
}
