package com.voiceassist.lixinyu.voiceassist.utils

import android.util.Log

/**
 * 日志工具
 *
 * @author chenys
 */
object KGLog {

    private val TAG = "voice"

    /**
     * 是否处于调试模式
     *
     * @param debug
     */
    var isDebug = true

    // ==========================================================

    /**
     * 是否在正式环境时打印LOG
     * @return
     */
    /**
     * 设置是否在正式环境时打印LOG
     * @param isShowLog
     */
    var isShowLog = true// 正式环境调试时如果想输出日志可以将该变量置为true

    var build = 5204

    fun d(msg: String) {
        if (isDebug || isShowLog) {
            Log.d(TAG + "", msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (isDebug || isShowLog) {
            Log.d(tag + "", msg)
        }
    }

    fun e(msg: String) {
        if (isDebug || isShowLog) {
            Log.e(TAG + "", msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (isDebug || isShowLog) {
            Log.e(tag + "", msg)
        }
    }

    fun v(msg: String) {
        if (isDebug || isShowLog) {
            Log.v(TAG + "", msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (isDebug || isShowLog) {
            Log.v(tag + "", msg)
        }
    }

    fun i(msg: String) {
        if (isDebug || isShowLog) {
            Log.i(TAG + "", msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isDebug || isShowLog) {
            Log.i(tag + "", msg)
        }
    }

    fun w(msg: String) {
        if (isDebug || isShowLog) {
            Log.w(TAG + "", msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (isDebug || isShowLog) {
            Log.w(tag + "", msg)
        }
    }

}
