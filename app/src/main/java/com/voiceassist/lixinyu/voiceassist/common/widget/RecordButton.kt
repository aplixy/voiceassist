package com.voiceassist.lixinyu.voiceassist.common.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

import java.io.File
import java.io.IOException


/**
 * Created by xuan on 2016/6/8.
 */
class RecordButton : android.support.v7.widget.AppCompatButton {

    private var mImageView: ImageView? = null
    private var mTitleTv: TextView? = null
    private var mTimeTv: TextView? = null
    private var mDialog: Dialog? = null

    private var mRecorder: MediaRecorder? = null
    private var mFilePath: String? = null

    /** 最短录音时间  */
    private val mMinRecordTime = 1000
    /** 最长录音时间  */
    private var mMaxRecordTime = 1000 * 60
    /** 开始录音时间，用于计算录音时长  */
    private var mStartTime: Long = 0

    private var mThread: ObtainDecibelThread? = null
    private var mVolumeHandler: Handler? = null

    private var mStartY: Int = 0
    private var mCancelLength = -200// 上滑取消距离

    private var mIsAllowRecord: Boolean = false

    //private var mOnFinishedRecordListener: OnFinishedRecordListener? = null
    //private var mOnBeforeRecordListener: OnBeforeRecordListener? = null

//    private val isAllowStartRecord: Boolean
//        get() = if (null != mOnBeforeRecordListener) {
//            mOnBeforeRecordListener!!.isAllowStartRecord
//        } else true

    private var isAllowStartRecord: (() -> Boolean) = {true}



    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        mVolumeHandler = ShowVolumeHandler()
        //mCancelLength = -this.getMeasuredHeight();

        mDialog = Dialog(context, R.style.recordbutton_alert_dialog)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_recordbutton_alert_dialog, null)
        mImageView = contentView.findViewById<View>(R.id.zeffect_recordbutton_dialog_imageview) as ImageView
        mTimeTv = contentView.findViewById<View>(R.id.zeffect_recordbutton_dialog_time_tv) as TextView
        mTitleTv = contentView.findViewById<View>(R.id.zeffect_recordbutton_dialog_title_tv) as TextView
        mDialog!!.setContentView(contentView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        mDialog!!.setOnDismissListener { stopRecording() }
    }


    /**
     * 保存路径，为空取默认值
     *
     * @param path
     */
    fun setSavePath(path: String) {
        if (!TextUtils.isEmpty(path)) {
            mFilePath = path
            val file = File(path)
            if (!file.exists()) {
                try {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else {
            setDefaultFilePath()
        }

    }

    /****
     * 设置最大时间。15秒-10分钟
     *
     * @param time 单位秒
     */
    fun setMaxRecordTime(time: Int) {
        if (time > 15 && time < 10 * 60) {
            mMaxRecordTime = time * 1000
        }
    }

    private fun setDefaultFilePath() {
        val file = File(Environment.getExternalStorageDirectory(), System.currentTimeMillis().toString() + ".amr")
        if (!file.exists())
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        mFilePath = file.absolutePath
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mStartY = event.y.toInt()
                //initDialogAndStartRecord();

                mIsAllowRecord = isAllowStartRecord()
                if (mIsAllowRecord) {
                    mCancelLength = -this.measuredHeight
                    startRecording()
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!mIsAllowRecord) return false
                val endY = event.y.toInt()
                if (mStartY < 0)
                    return true
                if (endY - mStartY < mCancelLength) {
                    cancelRecord()
                } else {
                    finishRecord()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsAllowRecord) return false
                val tempNowY = event.y.toInt()
                if (mStartY < 0)
                    return true
                if (tempNowY - mStartY < mCancelLength) {
                    mTitleTv!!.text = context.getString(R.string.zeffect_recordbutton_releasing_finger_to_cancal_send)
                } else {
                    mTitleTv!!.text = context.getString(R.string.zeffect_recordbutton_finger_up_to_cancal_send)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (!mIsAllowRecord) return false
                cancelRecord()
            }
        }

        return true
    }

    private fun finishRecord() {
        stopRecording()
        mDialog!!.dismiss()
        val intervalTime = System.currentTimeMillis() - mStartTime
        if (intervalTime < mMinRecordTime) {
            Toast.makeText(context, context.resources.getString(R.string.zeffect_recordbutton_time_too_short), Toast.LENGTH_SHORT).show()
            val file = File(mFilePath!!)
            if (file.exists()) {
                file.delete()
            }

            return
        }

//        if (mOnFinishedRecordListener != null) {
//            mOnFinishedRecordListener!!.onFinishedRecord(mFilePath)
//        }

        onFinishedRecord?.invoke(mFilePath)
    }

    private fun cancelRecord() {
        stopRecording()
        mDialog!!.dismiss()
        val file = File(mFilePath!!)
        if (file.exists())
            file.delete()
    }

    private fun startRecording() {
        mDialog!!.show()

        if (null == mRecorder) {
            mRecorder = MediaRecorder()
        } else {
            mRecorder!!.release()
        }

        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        if (TextUtils.isEmpty(mFilePath)) setDefaultFilePath()
        if (!TextUtils.isEmpty(mFilePath) && mFilePath!!.endsWith(".m4a")) {
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        } else {
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }


        ////        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        ////        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
        mRecorder!!.setAudioSamplingRate(44100)
        //设置声音数据编码格式,音频通用格式是AAC
        //        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置编码频率
        mRecorder!!.setAudioEncodingBitRate(96000)


        mRecorder!!.setOutputFile(mFilePath)

        mRecorder!!.setOnErrorListener(null)
        mRecorder!!.setOnInfoListener(null)

        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mStartTime = System.currentTimeMillis()
        try {
            mRecorder!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
            cancelRecord()
            mIsAllowRecord = false
            Toast.makeText(context, "设备异常", Toast.LENGTH_SHORT).show()
            return
        }

        mThread = ObtainDecibelThread()
        mThread!!.start()

    }

    private fun stopRecording() {
        if (mThread != null) {
            mThread!!.exit()
            mThread = null
        }
        if (mRecorder != null) {

            try {
                mRecorder!!.stop()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }

            mRecorder!!.release()
            mRecorder = null
        }
    }

    private inner class ObtainDecibelThread : Thread() {

        @Volatile
        private var running = true

        fun exit() {
            running = false
        }

        override fun run() {
            while (running) {
                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (mRecorder == null || !running) {
                    break
                }
                if (System.currentTimeMillis() - mStartTime >= mMaxRecordTime) {
                    // 如果超过最长录音时间
                    mVolumeHandler!!.sendEmptyMessage(CANCEL_RECORD_WHAT_102)
                }
                //发送时间
                mVolumeHandler!!.sendEmptyMessage(TIME_WHAT_101)
                //
                val x = mRecorder!!.maxAmplitude
                if (x != 0) {
                    val f = (20 * Math.log(x.toDouble()) / Math.log(10.0)).toInt()
                    val msg = Message()
                    msg.obj = f
                    msg.what = VOLUME_WHAT_100
                    mVolumeHandler!!.sendMessage(msg)
                }
            }
        }
    }

    internal inner class ShowVolumeHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                VOLUME_WHAT_100 -> {
                    setLevel(msg.obj as Int)
                }
                TIME_WHAT_101 -> {
                    val time = (System.currentTimeMillis() - mStartTime).toInt() / 1000
                    val second = time % 60
                    val mil = time / 60
                    if (mil < 10) {
                        if (second < 10)
                            mTimeTv!!.text = "0$mil:0$second"
                        else
                            mTimeTv!!.text = "0$mil:$second"
                    } else if (mil >= 10 && mil < 60) {
                        if (second < 10)
                            mTimeTv!!.text = mil.toString() + ":0" + second
                        else
                            mTimeTv!!.text = mil.toString() + ":" + second
                    }
                }
                CANCEL_RECORD_WHAT_102 -> {
                    //finishRecord();
                    cancelRecord()
                }
                else -> {
                }
            }
        }
    }

    private fun setLevel(level: Int) {
        if (mImageView != null)
            mImageView!!.drawable.level = 3000 + 6000 * level / 100
    }


//    interface OnFinishedRecordListener {
//        fun onFinishedRecord(audioPath: String?)
//    }
//
//    fun setOnFinishedRecordListener(listener: OnFinishedRecordListener) {
//        this.mOnFinishedRecordListener = listener
//    }



    private var onFinishedRecord: ((audioPath: String?) -> Unit)? = null

    fun setOnFinishedRecordListener(listener: ((audioPath: String?) -> Unit)?) {
        this.onFinishedRecord = listener
    }


//    interface OnBeforeRecordListener {
//        val isAllowStartRecord: Boolean
//    }
//
//    fun setOnBeforeRecordListener(l: OnBeforeRecordListener) {
//        this.mOnBeforeRecordListener = l
//    }


    fun setOnBeforeRecordListener(l: () -> Boolean) {
        this.isAllowStartRecord = l
    }

    companion object {
        private val TAG = "RecordButton"

        private val VOLUME_WHAT_100 = 100
        private val TIME_WHAT_101 = 101
        private val CANCEL_RECORD_WHAT_102 = 102
    }
}
