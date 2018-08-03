package com.voiceassist.lixinyu.voiceassist.utils


import android.content.Context
import android.text.TextUtils
import android.util.TypedValue

/**
 * 描述:字符操作
 *
 * @author chenys
 * @since 2013-7-22 下午5:53:50
 */
object StringUtil {

    private var appVersionKey: String? = null

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    fun toInt(str: String, defValue: Int): Int {
        try {
            return Integer.parseInt(str)
        } catch (e: Exception) {
        }

        return defValue
    }

    /**
     * 字符串转长整数
     *
     * @param str
     * @param defValue
     * @return
     */
    fun toLong(str: String, defValue: Long): Long {
        try {
            return java.lang.Long.parseLong(str)
        } catch (e: Exception) {
        }

        return defValue
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    fun isEmpty(s: String?): Boolean {
        return TextUtils.isEmpty(s) || "null" == s
    }

    // /**
    // * 获取图片下载地址
    // *
    // * @param imageHash
    // * @return
    // */
    // public static String getImageUrl(String imageHash) {
    // return UrlPref.getInstance().getImageUrl() + "/" + imageHash;
    // }
    //
    // /**
    // * 获取语音文件下载地址
    // *
    // * @param voiceHash
    // * @return
    // */
    // public static String getVoiceUrl(String voiceHash) {
    // return UrlPref.getInstance().getVoicecUrl() + "/" + voiceHash;
    // }
    //
    // /**
    // * 获取录制文件名
    // *
    // * @param hashValue
    // * @return
    // */
    // public static String getRecordFileName(String hashValue) {
    // return SDCardUtil.RECORD_AUDIO_FOLDER + hashValue +
    // AudioConfig.RECORD_FILE_EXTEND;
    // }

    // /**
    // * 获取录音文件路径包含后缀
    // *
    // * @param hashValue
    // * @return
    // */
    // public static String getRecordFilePath(String path) {
    // return path + AudioConfig.RECORD_FILE_EXTEND;
    // }
    //
    // /**
    // * 获取语音缓存路径
    // *
    // * @param hashValue
    // * @return
    // */
    // public static String getAudioCacheFilePath(String hashValue) {
    // StringBuilder downloadFilePath = new StringBuilder();
    // downloadFilePath.append(SDCardUtil.MEDIA_CACHE_FOLDER);
    // downloadFilePath.append(hashValue);
    // downloadFilePath.append(AudioConfig.RECORD_FILE_EXTEND);
    // downloadFilePath.append(".kgtmp");
    // return downloadFilePath.toString();
    // }

    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    fun dipToPx(context: Context, dipValue: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue.toFloat(), context
                .resources.displayMetrics).toInt()
    }

    /**
     * 转换px为dip
     *
     * @param context
     * @param px
     * @return
     */
    fun pxToDip(context: Context, px: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f * if (px >= 0) 1 else -1).toInt()
    }

    /**
     * 截取图片名称
     *
     * @param savePath
     * @return
     */
    fun spiltImageName(savePath: String): String? {
        if (TextUtils.isEmpty(savePath)) {
            return null
        }
        val end = savePath.lastIndexOf("/")
        return if (end == -1) {
            null
        } else savePath.substring(end + 1, savePath.length)

    }

    fun getAppVersionKey(context: Context): String? {
        if (appVersionKey == null) {
            val code = AppUtil.getCurVersion(context)
            var codeStirng = ""
            if (code < 10000) {
                codeStirng = "0$code"
            } else {
                codeStirng = code.toString()
            }
            val buffer = StringBuffer()
            for (i in 0 until codeStirng.length) {
                val c = codeStirng[i]
                buffer.append(c).append(".")
            }
            val s = buffer.substring(0, buffer.length - 1)
            if (KGLog.isDebug) {
                appVersionKey = "Android/$s/debug"
            } else {
                appVersionKey = "Android/$s/release"
            }
        }

        return appVersionKey
    }

    /**
     * 酷狗登录时使用的versionKey，比之前的多加了渠道号字段
     *
     * @param context
     * @return
     */
    fun getKGAppVersionKey(context: Context): String? {
        if (appVersionKey == null) {
            val code = AppUtil.getCurVersion(context)
            var codeStirng = ""
            if (code < 10000) {
                codeStirng = "0$code"
            } else {
                codeStirng = code.toString()
            }
            val buffer = StringBuffer()
            for (i in 0 until codeStirng.length) {
                val c = codeStirng[i]
                buffer.append(c).append(".")
            }
            val s = buffer.substring(0, buffer.length - 1)
            if (KGLog.isDebug) {
                appVersionKey = "Android/$s/360/debug"
            } else {
                appVersionKey = "Android/$s/360/release"
            }
        }

        return appVersionKey
    }

    /**
     * 格式化文件路径（去除一些特殊字符）
     *
     * @param filePath
     * @return
     */
    fun formatFilePath(filePath: String): String? {
        return if (TextUtils.isEmpty(filePath)) {
            null
        } else filePath.replace("\\", "").replace("/", "").replace("*", "").replace("?", "")
                .replace(":", "").replace("\"", "").replace("<", "").replace(">", "")
                .replace("|", "")
    }
}
