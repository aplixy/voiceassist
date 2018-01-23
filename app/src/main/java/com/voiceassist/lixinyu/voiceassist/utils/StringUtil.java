
package com.voiceassist.lixinyu.voiceassist.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

/**
 * 描述:字符操作
 * 
 * @author chenys
 * @since 2013-7-22 下午5:53:50
 */
public class StringUtil {

    /**
     * 字符串转整数
     * 
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 字符串转长整数
     * 
     * @param str
     * @param defValue
     * @return
     */
    public static long toLong(String str, long defValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 判断字符串是否为空
     * 
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s) || "null".equals(s);
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
    public static int dipToPx(Context context, int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                .getResources().getDisplayMetrics());
    }

    /**
     * 转换px为dip
     * 
     * @param context
     * @param px
     * @return
     */
    public static int pxToDip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * 截取图片名称
     * 
     * @param savePath
     * @return
     */
    public static String spiltImageName(String savePath) {
        if (TextUtils.isEmpty(savePath)) {
            return null;
        }
        int end = savePath.lastIndexOf("/");
        if (end == -1) {
            return null;
        }
        return savePath.substring(end + 1, savePath.length());

    }

    private static String appVersionKey;

    public static String getAppVersionKey(Context context) {
        if (appVersionKey == null) {
            int code = AppUtil.getCurVersion(context);
            String codeStirng = "";
            if (code < 10000) {
                codeStirng = "0" + code;
            } else {
                codeStirng = String.valueOf(code);
            }
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < codeStirng.length(); i++) {
                char c = codeStirng.charAt(i);
                buffer.append(c).append(".");
            }
            String s = buffer.substring(0, buffer.length() - 1);
            if (KGLog.isDebug()) {
                appVersionKey = "Android/" + s + "/debug";
            } else {
                appVersionKey = "Android/" + s + "/release";
            }
        }

        return appVersionKey;
    }

    /**
     * 酷狗登录时使用的versionKey，比之前的多加了渠道号字段
     * 
     * @param context
     * @return
     */
    public static String getKGAppVersionKey(Context context) {
        if (appVersionKey == null) {
            int code = AppUtil.getCurVersion(context);
            String codeStirng = "";
            if (code < 10000) {
                codeStirng = "0" + code;
            } else {
                codeStirng = String.valueOf(code);
            }
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < codeStirng.length(); i++) {
                char c = codeStirng.charAt(i);
                buffer.append(c).append(".");
            }
            String s = buffer.substring(0, buffer.length() - 1);
            if (KGLog.isDebug()) {
                appVersionKey = "Android/" + s + "/360/debug";
            } else {
                appVersionKey = "Android/" + s + "/360/release";
            }
        }

        return appVersionKey;
    }

    /**
     * 格式化文件路径（去除一些特殊字符）
     * 
     * @param filePath
     * @return
     */
    public static String formatFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return filePath.replace("\\", "").replace("/", "").replace("*", "").replace("?", "")
                .replace(":", "").replace("\"", "").replace("<", "").replace(">", "")
                .replace("|", "");
    }
}
