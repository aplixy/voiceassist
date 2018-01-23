
package com.voiceassist.lixinyu.voiceassist.utils;

import android.util.Log;

/**
 * 日志工具
 * 
 * @author chenys
 */
public class KGLog {

    private static final String TAG = "voice";

    private static boolean isDebug = true;
    
    private static boolean isShowLog = true;// 正式环境调试时如果想输出日志可以将该变量置为true
    
    public static int build = 5204;
    
    // ==========================================================

    /**
     * 是否在正式环境时打印LOG
     * @return
     */
    public static boolean isShowLog() {
		return isShowLog;
	}

    /**
     * 设置是否在正式环境时打印LOG
     * @param isShowLog
     */
	public static void setShowLog(boolean isShowLog) {
		KGLog.isShowLog = isShowLog;
	}

	/**
     * 是否处于调试模式
     * 
     * @param debug
     */
    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void d(String msg) {
        if (isDebug || isShowLog) 
        {
            Log.d(TAG + "", msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug || isShowLog)
        {
            Log.d(tag + "", msg);
        }
    }

    public static void e(String msg) {
        if (isDebug || isShowLog) 
        {
            Log.e(TAG + "", msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug || isShowLog)
        {
            Log.e(tag + "", msg);
        }
    }
    
    public static void v(String msg) {
    	if (isDebug || isShowLog) {
    		Log.v(TAG + "", msg);
    	}
    }
    
    public static void v(String tag, String msg) {
    	if (isDebug || isShowLog) {
    		Log.v(tag + "", msg);
    	}
    }
    
    public static void i(String msg) {
    	if (isDebug || isShowLog) {
    		Log.i(TAG + "", msg);
    	}
    }
    
    public static void i(String tag, String msg) {
    	if (isDebug || isShowLog) {
    		Log.i(tag + "", msg);
    	}
    }
    
    public static void w(String msg) {
    	if (isDebug || isShowLog) {
    		Log.w(TAG + "", msg);
    	}
    }
    
    public static void w(String tag, String msg) {
    	if (isDebug || isShowLog) {
    		Log.w(tag + "", msg);
    	}
    }

}
