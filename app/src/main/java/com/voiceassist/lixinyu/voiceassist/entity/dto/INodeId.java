package com.voiceassist.lixinyu.voiceassist.entity.dto;

/**
 * Created by lilidan on 2018/1/23.
 */

public interface INodeId {

    int NODE_TYPE_FIRST_LEVEL = 0x100;
    int NODE_TYPE_SECOND_LEVEL = 0x101;

    String getNodeId();
    int getNodeType();
}
