package com.voiceassist.lixinyu.voiceassist.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by xuan on 2016/6/8.
 */
public class RecordButton extends android.support.v7.widget.AppCompatButton {
    private static final String TAG = "RecordButton";

    private static final int VOLUME_WHAT_100 = 100;
    private static final int TIME_WHAT_101 = 101;
    private static final int CANCEL_RECORD_WHAT_102 = 102;

    private ImageView mImageView;
    private TextView mTitleTv;
    private TextView mTimeTv;
    private Dialog mDialog;

    private MediaRecorder mRecorder;
    private String mFilePath;

    /** 最短录音时间 */
    private int mMinRecordTime = 1000;
    /** 最长录音时间 */
    private int mMaxRecordTime = 1000 * 60;
    /** 开始录音时间，用于计算录音时长 */
    private long mStartTime;

    private ObtainDecibelThread mThread;
    private Handler mVolumeHandler;

    private int mStartY;
    private int mCancelLength = -200;// 上滑取消距离

    private boolean mIsAllowRecord;

    private OnFinishedRecordListener mOnFinishedRecordListener;
    private OnBeforeRecordListener mOnBeforeRecordListener;




    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler();
        //mCancelLength = -this.getMeasuredHeight();

        mDialog = new Dialog(getContext(), R.style.recordbutton_alert_dialog);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_recordbutton_alert_dialog, null);
        mImageView = (ImageView) contentView.findViewById(R.id.zeffect_recordbutton_dialog_imageview);
        mTimeTv = (TextView) contentView.findViewById(R.id.zeffect_recordbutton_dialog_time_tv);
        mTitleTv = (TextView) contentView.findViewById(R.id.zeffect_recordbutton_dialog_title_tv);
        mDialog.setContentView(contentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopRecording();
            }
        });
    }


    /**
     * 保存路径，为空取默认值
     *
     * @param path
     */
    public void setSavePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mFilePath = path;
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            setDefaultFilePath();
        }

    }

    /****
     * 设置最大时间。15秒-10分钟
     *
     * @param time 单位秒
     */
    public void setMaxRecordTime(int time) {
        if (time > 15 && time < 10 * 60) {
            mMaxRecordTime = time * 1000;
        }
    }

    private void setDefaultFilePath() {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".amr");
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFilePath = file.getAbsolutePath();
    }








    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) event.getY();
                //initDialogAndStartRecord();

                if (mIsAllowRecord = isAllowStartRecord()) {
                    mCancelLength = -this.getMeasuredHeight();
                    startRecording();
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsAllowRecord) return false;
                int endY = (int) event.getY();
                if (mStartY < 0)
                    return true;
                if (endY - mStartY < mCancelLength) {
                    cancelRecord();
                } else {
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsAllowRecord) return false;
                int tempNowY = (int) event.getY();
                if (mStartY < 0)
                    return true;
                if (tempNowY - mStartY < mCancelLength) {
                    mTitleTv.setText(getContext().getString(R.string.zeffect_recordbutton_releasing_finger_to_cancal_send));
                } else {
                    mTitleTv.setText(getContext().getString(R.string.zeffect_recordbutton_finger_up_to_cancal_send));
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mIsAllowRecord) return false;
                cancelRecord();
                break;
        }

        return true;
    }

    private boolean isAllowStartRecord() {
        if (null != mOnBeforeRecordListener) {
            return mOnBeforeRecordListener.isAllowStartRecord();
        }
        return true;
    }

    private void finishRecord() {
        stopRecording();
        mDialog.dismiss();
        long intervalTime = System.currentTimeMillis() - mStartTime;
        if (intervalTime < mMinRecordTime) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.zeffect_recordbutton_time_too_short), Toast.LENGTH_SHORT).show();
            File file = new File(mFilePath);
            if (file.exists()) {
                file.delete();
            }

            return;
        }

        if (mOnFinishedRecordListener != null) {
            mOnFinishedRecordListener.onFinishedRecord(mFilePath);
        }
    }

    private void cancelRecord() {
        stopRecording();
        mDialog.dismiss();
        File file = new File(mFilePath);
        if (file.exists())
            file.delete();
    }

    private void startRecording() {
        mDialog.show();

        if (null == mRecorder) {
            mRecorder = new MediaRecorder();
        } else {
            mRecorder.release();
        }

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        if (TextUtils.isEmpty(mFilePath)) setDefaultFilePath();
        if (!TextUtils.isEmpty(mFilePath) && mFilePath.endsWith(".m4a")) {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }


////        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
////        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
        mRecorder.setAudioSamplingRate(44100);
        //设置声音数据编码格式,音频通用格式是AAC
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置编码频率
        mRecorder.setAudioEncodingBitRate(96000);


        mRecorder.setOutputFile(mFilePath);

        mRecorder.setOnErrorListener(null);
        mRecorder.setOnInfoListener(null);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mStartTime = System.currentTimeMillis();
        try {
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            cancelRecord();
            mIsAllowRecord = false;
            Toast.makeText(getContext(), "设备异常", Toast.LENGTH_SHORT).show();
            return;
        }

        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        if (mRecorder != null) {

            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            mRecorder.release();
            mRecorder = null;
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mRecorder == null || !running) {
                    break;
                }
                if (System.currentTimeMillis() - mStartTime >= mMaxRecordTime) {
                    // 如果超过最长录音时间
                    mVolumeHandler.sendEmptyMessage(CANCEL_RECORD_WHAT_102);
                }
                //发送时间
                mVolumeHandler.sendEmptyMessage(TIME_WHAT_101);
                //
                int x = mRecorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (20 * Math.log(x) / Math.log(10));
                    Message msg = new Message();
                    msg.obj = f;
                    msg.what = VOLUME_WHAT_100;
                    mVolumeHandler.sendMessage(msg);
                }
            }
        }
    }

    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VOLUME_WHAT_100: {
                    setLevel((int) msg.obj);
                    break;
                }
                case TIME_WHAT_101: {
                    int time = ((int) (System.currentTimeMillis() - mStartTime) / 1000);
                    int second = time % 60;
                    int mil = time / 60;
                    if (mil < 10) {
                        if (second < 10)
                            mTimeTv.setText("0" + mil + ":0" + second);
                        else
                            mTimeTv.setText("0" + mil + ":" + second);
                    } else if (mil >= 10 && mil < 60) {
                        if (second < 10)
                            mTimeTv.setText(mil + ":0" + second);
                        else
                            mTimeTv.setText(mil + ":" + second);
                    }
                    break;
                }
                case CANCEL_RECORD_WHAT_102: {
                    //finishRecord();
                    cancelRecord();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private void setLevel(int level) {
        if (mImageView != null)
            mImageView.getDrawable().setLevel(3000 + 6000 * level / 100);
    }


    public interface OnFinishedRecordListener {
        void onFinishedRecord(String audioPath);
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        this.mOnFinishedRecordListener = listener;
    }


    public interface OnBeforeRecordListener {
        boolean isAllowStartRecord();
    }

    public void setOnBeforeRecordListener(OnBeforeRecordListener l) {
        this.mOnBeforeRecordListener = l;
    }
}
