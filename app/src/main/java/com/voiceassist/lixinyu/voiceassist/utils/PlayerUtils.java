package com.voiceassist.lixinyu.voiceassist.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.voiceassist.lixinyu.voiceassist.common.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lilidan on 2018/1/23.
 */

public class PlayerUtils {

    private static final String TAG = "PlayerUtils";

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

    public void play(String filePath) {
        if (null == filePath || filePath.length() < 5) {
            ToastUtils.showToast("语音文件路径无效");
            return;
        }

        boolean success = playAssests(filePath);

        if (!success) {
            if (filePath.endsWith("m4a")) filePath = replaceAffix(filePath, "mp3");
            else if (filePath.endsWith("mp3")) filePath = replaceAffix(filePath, "m4a");

            success = playAssests(filePath);

            if (!success) {
                success = playSdCard(filePath);

                if (!success) {
                    if (filePath.endsWith("m4a")) filePath = replaceAffix(filePath, "mp3");
                    else if (filePath.endsWith("mp3")) filePath = replaceAffix(filePath, "m4a");

                    success = playSdCard(filePath);

                    if (!success) ToastUtils.showToast("音频文件未找到");
                }
            }
        }
    }

    public boolean playAssests(String filePath) {

        MediaPlayer mediaPlayer = null;

        try {
            AssetFileDescriptor fileDescriptor = mAssetManager.openFd(filePath);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());

            mediaPlayer.prepare();
            mediaPlayer.start();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean playSdCard(String filePath) {
        KGLog.i(TAG, "playSdCard--->filePath--->" + filePath);

        String sdCardFilePath = filePath;
        if (!filePath.contains(Constants.ROOT_PATH)) {
            sdCardFilePath = Constants.ROOT_PATH + filePath;
            KGLog.v(TAG, "playSdCard--->sdCardFilePath--->" + sdCardFilePath);
        }

        MediaPlayer mediaPlayer = null;

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(sdCardFilePath);

            mediaPlayer.prepare();
            mediaPlayer.start();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String replaceAffix(String filePath, String destAffix) {
        return filePath.substring(0, filePath.length() - 3) + destAffix;
    }


}
