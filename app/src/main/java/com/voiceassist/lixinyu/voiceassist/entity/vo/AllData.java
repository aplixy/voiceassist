package com.voiceassist.lixinyu.voiceassist.entity.vo;

import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class AllData implements Serializable {

    public List<Node> nodes;
    public List<Relationship> relationship;
}
