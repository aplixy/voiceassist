package com.voiceassist.lixinyu.voiceassist.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lilidan on 2018/1/23.
 */

public class PlayerUtils {

    private static volatile PlayerUtils mInstance = null;

    private Context mContext;
    private AssetManager mAssetManager;


    private PlayerUtils(Context context){
        this.mContext = context;
        this.mAssetManager = context.getAssets();
    }

    public static PlayerUtils getInstance(Context context){
        if(mInstance == null){
            synchronized (PlayerUtils.class){
                if(mInstance == null){
                    mInstance = new PlayerUtils(context);
                }
            }
        }
        return mInstance;
    }

    public void playSound(String filePath) {

        try {
            AssetFileDescriptor fileDescriptor = mAssetManager.openFd(filePath);

            MediaPlayer mediaPlayer = new MediaPlayer();
            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showToast("未找到语音文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
