package com.voiceassist.lixinyu.voiceassist.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.MediaPlayer

import com.voiceassist.lixinyu.voiceassist.common.Constants

import java.io.FileNotFoundException
import java.io.IOException

/**
 * Created by lilidan on 2018/1/23.
 */

class PlayerUtils private constructor(private val mContext: Context) {

    private val mAssetManager: AssetManager


    init {
        this.mAssetManager = mContext.assets
    }

    fun play(filePath: String?) {
        var filePath = filePath
        if (null == filePath || filePath.length < 5) {
            ToastUtils.showToast("语音文件路径无效")
            return
        }


        var success = false

        if (filePath.contains(Constants.SD_CARD_PATH)) {
            success = playSdCard(filePath)
            if (!success) {
                if (filePath.endsWith("m4a"))
                    filePath = replaceAffix(filePath, "mp3")
                else if (filePath.endsWith("mp3")) filePath = replaceAffix(filePath, "m4a")

                success = playSdCard(filePath)

                if (!success) ToastUtils.showToast("音频文件未找到")
            }
        } else {
            success = playAssests(filePath)

            if (!success) {
                if (filePath.endsWith("m4a"))
                    filePath = replaceAffix(filePath, "mp3")
                else if (filePath.endsWith("mp3")) filePath = replaceAffix(filePath, "m4a")

                success = playAssests(filePath)

                if (!success) {
                    success = playSdCard(filePath)

                    if (!success) {
                        if (filePath.endsWith("m4a"))
                            filePath = replaceAffix(filePath, "mp3")
                        else if (filePath.endsWith("mp3")) filePath = replaceAffix(filePath, "m4a")

                        success = playSdCard(filePath)

                        if (!success) ToastUtils.showToast("音频文件未找到")
                    }
                }
            }
        }


    }

    fun playAssests(filePath: String): Boolean {

        var mediaPlayer: MediaPlayer? = null

        try {
            val fileDescriptor = mAssetManager.openFd(filePath)

            mediaPlayer = MediaPlayer()
            mediaPlayer.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }

            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length)

            mediaPlayer.prepare()
            mediaPlayer.start()

            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    private fun playSdCard(filePath: String): Boolean {
        KGLog.i(TAG, "playSdCard--->filePath--->$filePath")

        var sdCardFilePath = filePath
        if (!filePath.contains(Constants.SD_CARD_PATH)) {
            sdCardFilePath = Constants.ROOT_PATH + filePath
            KGLog.v(TAG, "playSdCard--->sdCardFilePath--->$sdCardFilePath")
        }

        var mediaPlayer: MediaPlayer? = null

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }

            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(sdCardFilePath)

            mediaPlayer.prepare()
            mediaPlayer.start()

            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    private fun replaceAffix(filePath: String, destAffix: String): String {
        return filePath.substring(0, filePath.length - 3) + destAffix
    }

    companion object {

        private val TAG = "PlayerUtils"

        @Volatile
        private var mInstance: PlayerUtils? = null

        fun getInstance(context: Context): PlayerUtils? {
            if (mInstance == null) {
                synchronized(PlayerUtils::class.java) {
                    if (mInstance == null) {
                        mInstance = PlayerUtils(context)
                    }
                }
            }
            return mInstance
        }
    }


}
