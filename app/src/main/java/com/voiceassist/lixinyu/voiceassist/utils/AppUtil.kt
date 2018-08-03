package com.voiceassist.lixinyu.voiceassist.utils


import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.RunningTaskInfo
import android.app.Application
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.Display
import android.view.WindowManager

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream

/**
 * 描述: App 通用工具
 *
 * @author gongzhenjie
 * @since 2013年8月12日 上午10:40:08
 */
object AppUtil {

    /**
     * 新版本下载地址
     *
     * @return
     */
    var appUrl = ""

    // /** 分类文件名称 */
    // public static final String CATEGORY_FILE_NAME = "category.txt";
    //
    // /** 地区文件名称 */
    // public static final String LOCATION_FILE_NAME = "location.txt";

    /**
     * 资源包文件,一定要和/assets/下的资源包名称一样
     */
    val SOURCE_FILE_NAME = "source.zip"

    /**
     * @return
     */
    val ramTotalSize: Long
        get() {
            val str1 = "/proc/meminfo"
            val str2: String
            val arrayOfString: Array<String>
            var initial_memory: Long = 0
            try {
                val localFileReader = FileReader(str1)
                val localBufferedReader = BufferedReader(localFileReader, 8192)
                str2 = localBufferedReader.readLine()
                arrayOfString = str2.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                initial_memory = (Integer.valueOf(arrayOfString[1])!!.toInt() * 1024).toLong()
                localBufferedReader.close()
                return initial_memory
            } catch (e: IOException) {
                return -1
            }

        }

    /**
     * @return
     */
    val romTotalSize: Long
        get() {
            val statInternal = StatFs(Environment.getDataDirectory().path)
            val blockSize = statInternal.blockSize.toLong()
            return statInternal.blockCount * blockSize
        }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return versionCode
     */
    fun getCurVersion(context: Context): Int {
        try {
            val pinfo: PackageInfo
            pinfo = context.packageManager.getPackageInfo("com.kugou.fm",
                    PackageManager.GET_CONFIGURATIONS)

            return pinfo.versionCode

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return versionCode
     */
    fun getCurVersionName(context: Context): String {
        try {
            val pinfo: PackageInfo
            pinfo = context.packageManager.getPackageInfo("com.voiceassist.lixinyu.voiceassist",
                    PackageManager.GET_CONFIGURATIONS)
            val versionCode = pinfo.versionName
            return "" + versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "1.0"
    }

    /**
     * 获取纯粹的版本号
     * @param context
     * @return
     */
    fun getCurVer(context: Context): String {
        try {
            val pinfo: PackageInfo
            pinfo = context.packageManager.getPackageInfo("com.voiceassist.lixinyu.voiceassist",
                    PackageManager.GET_CONFIGURATIONS)
            return pinfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "3.0.0"
    }

    /**
     * 获取AndroidManifest.xml中的meta_date值
     *
     * @param context
     * @param key
     * @return
     */
    fun getMetaData(context: Context, key: String): String? {
        var metaData: String? = null
        try {
            val ai = context.packageManager.getApplicationInfo(
                    context.packageName, PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            metaData = bundle.getString(key)
        } catch (e: Exception) {
        }

        return metaData
    }

    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    fun getAppName(context: Context): String? {
        var appName: String? = null
        try {
            val ai = context.packageManager.getApplicationInfo(
                    context.packageName, PackageManager.GET_META_DATA)
            appName = context.getString(ai.labelRes)
        } catch (e: Exception) {
        }

        return appName
    }

    /**
     * 判断是否有这个权限
     * @param permName
     * @return
     */
    fun getAppPermission(context: Context, permName: String): Boolean {
        val pm = context.packageManager
        return PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, "com.kugou.fm")
    }

    // /**
    // * 显示Toast
    // *
    // * @param activity
    // * @param msg
    // * @param duration
    // */
    // public static void showCustomToast(Activity activity, String msg, int
    // duration) {
    // // 获取LayoutInflater对象，该对象可以将布局文件转换成与之一致的view对象
    //
    // LayoutInflater inflater = activity.getLayoutInflater();
    // // 将布局文件转换成相应的View对象
    // View layout = inflater.inflate(R.layout.toast_text_widget, null);
    //
    // ((TextView) layout.findViewById(R.id.toasttext)).setText(msg);
    // // 实例化一个Toast对象
    // Toast toast = new Toast(activity.getApplicationContext());
    // toast.setDuration(duration);
    // toast.setGravity(Gravity.BOTTOM, 0, 200);
    // toast.setView(layout);
    // toast.show();
    // }

    fun getScreenSize(context: Context): IntArray {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay

        return intArrayOf(display.width, display.height)
    }

    // /**
    // * 重新计算图片要显示的宽高
    // *
    // * @param context
    // * @return
    // */
    // public static int[] getImageSize(Context context) {
    // int width = getScreenSize(context)[0];
    // int padding = context.getResources().getDimensionPixelSize(
    // R.dimen.trend_topic_layout_padding);
    // int newWidth = width - 2 * padding;
    // int newHeight = newWidth / 3 * 2;
    // return new int[] {
    // newWidth, newHeight
    // };
    // }
    //
    // public static void CommonAnimStartActivity(Activity activity, Class<?>
    // toClass) {
    // activity.startActivity(new Intent(activity, toClass));
    // CommonStartAnim(activity);
    // }

    // public static void CommonStartAnim(Activity activity) {
    // activity.overridePendingTransition(R.anim.activity_r2m_slide,
    // R.anim.activity_m2l_slide);
    // }

    // public static void CommonAnimFinishActivity(Activity activity) {
    // activity.finish();
    // CommonFinishAnim(activity);
    // }

    // public static void CommonFinishAnim(Activity activity) {
    // // activity.finish();
    // activity.overridePendingTransition(R.anim.activity_l2m_slide,
    // R.anim.activity_m2r_slide);
    // }

    /**
     * 应用是否被隐藏<在后台运行>
     *
     * @param context
     * @return
    </在后台运行> */
    fun IsRunOnBackground(context: Context): Boolean {
        val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasksInfo = activityManager.getRunningTasks(1)
        return if (tasksInfo != null && tasksInfo.size > 0 && context.packageName == tasksInfo[0].topActivity.packageName) {
            false
        } else true
    }

    /**
     * 判断是否锁屏
     *
     * @return true 锁屏状态， false 非锁屏状态
     */
    fun isScreenOff(context: Context): Boolean {
        val mKeyguardManager = context
                .getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
        return mKeyguardManager.inKeyguardRestrictedInputMode()
    }

    /**
     * 判断是否已经创建该电台快捷方式
     *
     * @param context
     * @return
     */
    fun isAddSingleRadioShortCut(context: Context, radioName: String): Boolean {
        var isInstallShortcut = false
        val cr = context.contentResolver
        // 本人的2.2系统是”com.android.launcher2.settings”,网上见其他的为"com.android.launcher.settings"
        var AUTHORITY = ""
        if (android.os.Build.VERSION.SDK_INT < 8) {
            AUTHORITY = "com.android.launcher.settings"
        } else {
            AUTHORITY = "com.android.launcher2.settings"
        }
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/favorites?notify=true")
        val c = cr.query(CONTENT_URI, arrayOf("title", "iconResource"), "title=?", arrayOf(radioName), null)// XXX表示应用名称。
        if (c != null && c.count > 0) {
            isInstallShortcut = true
            c.close()
        }
        return isInstallShortcut
    }

    /**
     * @param context
     * @param path
     * @return
     */
    fun getAssetsFile(context: Context, path: String): InputStream? {
        val am = context.assets
        var `is`: InputStream?
        try {
            `is` = am.open(path)
        } catch (e: IOException) {
            `is` = null
        }

        return `is`
    }

    /**
     * 获取手机内部存储
     *
     * @param context
     * @return
     */
    fun getInternalAvailableBlocks(context: Context): Long {
        // String mTotalSize = "内部总容量：";
        // String mAvailSize = "内部剩余容量：";
        val statInternal = StatFs(Environment.getDataDirectory().path)
        val blockSize = statInternal.blockSize.toLong()
        val totalBlocks = statInternal.blockCount * blockSize
// mTotalSize += Formatter.formatFileSize(context, totalBlocks *
        // blockSize);
        // mAvailSize += Formatter.formatFileSize(context, availableBlocks *
        // blockSize);
        return statInternal.availableBlocks * blockSize
    }

    fun getPackagePath(context: Context): String {
        return context.filesDir.parent
    }

    fun getSourcePath(context: Context): String {
        return context.filesDir.path + "/" + SOURCE_FILE_NAME
    }

    fun getImagePath(context: Context): String {
        return context.filesDir.parent + "/image/"
    }

    fun getFilesPath(context: Context): String {
        return context.filesDir.parent + "/files/"
    }

    /**
     * 获取中等图片缓存的key
     *
     * @param radioKey
     * @return
     */
    fun getPlayImageKey(radioKey: Long): String {
        return StringBuilder().append(radioKey).append("_medium").toString()
    }

    fun getPlayImageUrl(url: String): String {
        return url.replace("90x90", "160x160")
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    fun getStatusHeight(activity: Activity): Int {
        var statusHeight = 0
        val localRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(localRect)
        statusHeight = localRect.top
        if (0 == statusHeight) {
            val localClass: Class<*>
            try {
                localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject)
                        .toString())
                statusHeight = activity.resources.getDimensionPixelSize(i5)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

        }
        return statusHeight
    }

    fun getIMEI(context: Context): String {
        return (context.getSystemService(Application.TELEPHONY_SERVICE) as TelephonyManager)
                .deviceId
    }

    fun getIMSI(context: Context): String {
        return (context.getSystemService(Application.TELEPHONY_SERVICE) as TelephonyManager)
                .subscriberId
    }

    fun getMCC(context: Context): String {
        var mcc = ""
        val imsi = getIMSI(context)
        if (!TextUtils.isEmpty(imsi) && imsi.length > 2) {
            mcc = imsi.substring(0, 3)
        }
        return mcc
    }

    fun getMNC(context: Context): String {
        var mnc = ""
        val imsi = getIMSI(context)
        if (!TextUtils.isEmpty(imsi) && imsi.length > 5) {
            mnc = imsi.substring(3, 5)
        }
        return mnc
    }

    fun getMobileProvidersName(context: Context): String? {
        var providersName: String? = null
        val imsi = getIMSI(context)
        if (TextUtils.isEmpty(imsi)) {
            return ""
        }
        // imsi号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
            providersName = "中国移动"
        } else if (imsi.startsWith("46001")) {
            providersName = "中国联通"
        } else if (imsi.startsWith("46003")) {
            providersName = "中国电信"
        }
        return providersName
    }

    /**
     * 安装APK
     *
     * @param context
     * @param filePath APK存放路径
     */
    fun installApk(context: Context, filePath: String) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.fromFile(File(filePath)),
                "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    fun getTelImsi(context: Context): String {
        var imsi = ""
        val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imsi = tm.subscriberId
        if (!StringUtil.isEmpty(imsi) && imsi.length > 15) {
            imsi = imsi.substring(0, 15)
        }
        return if (StringUtil.isEmpty(imsi)) "" else imsi
    }

    fun getTelImei(context: Context): String {
        var imei = ""
        val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imei = tm.deviceId
        return if (StringUtil.isEmpty(imei)) "" else imei
    }

    /**
     * 获取字符串的长度，中文占2个字符,英文数字占1个字符
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    fun length(value: String): Double {
        var valueLength = 0.0
        val chinese = "[\u4e00-\u9fa5]"
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (i in 0 until value.length) {
            // 获取一个字符
            val temp = value.substring(i, i + 1)
            // 判断是否为中文字符
            if (temp.matches(chinese.toRegex())) {
                // 中文字符长度为2
                valueLength += 2.0
            } else {
                // 其他字符长度为1
                valueLength += 1.0
            }
        }
        // 进位取整
        return Math.ceil(valueLength)
    }

    /**
     * 判断Intent是否有效
     *
     * @param context
     * @return true 有效
     */
    fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        val packageManager = context.packageManager
        val list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    /**
     * 判断一个程序是否显示在前端,根据测试此方法执行效率在11毫秒,无需担心此方法的执行效率
     *
     * @return true--->在前端,false--->不在前端
     */
    fun isApplicationShowing(context: Context): Boolean {
        var result = false
        val packageName = context.packageName
        val am = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = am.runningAppProcesses
        if (appProcesses != null) {
            for (runningAppProcessInfo in appProcesses) {
                if (runningAppProcessInfo.processName == packageName) {
                    val status = runningAppProcessInfo.importance
                    if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE || status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = am.runningAppProcesses
        if (infos != null && infos.size > 0) {
            for (appProcess in infos) {
                if (appProcess.pid == pid) {
                    return appProcess.processName
                }
            }
        }
        return ""
    }

}
