package com.voiceassist.lixinyu.voiceassist.common

import com.voiceassist.lixinyu.voiceassist.utils.FileUtils

import java.io.File

/**
 * Created by lixinyu on 2018/1/21.
 */

object Constants {

    val SD_CARD_PATH = FileUtils.innerSDCardPath

    val ROOT_PATH = SD_CARD_PATH + File.separator + "voice_assist" + File.separator

    val JSON_DATA_PATH = ROOT_PATH + "json_data.txt"

    val AUDIO_RECORD_PATH = ROOT_PATH + "record" + File.separator
}
