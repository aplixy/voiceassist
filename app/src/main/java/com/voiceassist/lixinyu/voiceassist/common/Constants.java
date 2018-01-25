package com.voiceassist.lixinyu.voiceassist.common;

import com.voiceassist.lixinyu.voiceassist.utils.FileUtils;

import java.io.File;

/**
 * Created by lixinyu on 2018/1/21.
 */

public class Constants {

    public static final String ROOT_PATH = FileUtils.getInnerSDCardPath() + File.separator + "voice_assist" + File.separator;

    public static final String JSON_DATA_PATH = ROOT_PATH + "json_data.txt";

    public static final String AUDIO_RECORD_PATH = ROOT_PATH + "record" + File.separator;
}
