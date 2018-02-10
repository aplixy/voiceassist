
package com.voiceassist.lixinyu.voiceassist.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 描述: App 通用工具
 * 
 * @author gongzhenjie
 * @since 2013年8月12日 上午10:40:08
 */
public class AppUtil {

    public static String Version_Url = "";

    /**
     * 获取当前版本号
     * 
     * @param context
     * @return versionCode
     */
    public static int getCurVersion(Context context) {
        try {
            PackageInfo pinfo;
            pinfo = context.getPackageManager().getPackageInfo("com.kugou.fm",
                    PackageManager.GET_CONFIGURATIONS);
            int versionCode = pinfo.versionCode;

            return versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 获取当前版本号
     * 
     * @param context
     * @return versionCode
     */
    public static String getCurVersionName(Context context) {
        try {
            PackageInfo pinfo;
            pinfo = context.getPackageManager().getPackageInfo("com.voiceassist.lixinyu.voiceassist",
                    PackageManager.GET_CONFIGURATIONS);
            String versionCode = pinfo.versionName;
            return "" + versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1.0";
    }
    
    /**
     * 获取纯粹的版本号
     * @param context
     * @return
     */
    public static String getCurVer(Context context){
    	try {
            PackageInfo pinfo;
            pinfo = context.getPackageManager().getPackageInfo("com.voiceassist.lixinyu.voiceassist",
                    PackageManager.GET_CONFIGURATIONS);
            String versionCode = pinfo.versionName;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "3.0.0";
    }

    /**
     * 获取AndroidManifest.xml中的meta_date值
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getMetaData(Context context, String key) {
        String metaData = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            metaData = bundle.getString(key);
        } catch (Exception e) {
        }
        return metaData;
    }

    /**
     * 获取应用名称
     * 
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String appName = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            appName = context.getString(ai.labelRes);
        } catch (Exception e) {
        }
        return appName;
    }

    /**
     * 判断是否有这个权限
     * @param permName
     * @return
     */
    public static boolean getAppPermission(Context context, String permName){
    	PackageManager pm = context.getPackageManager();  
        boolean permission = (PackageManager.PERMISSION_GRANTED ==   
                pm.checkPermission(permName, "com.kugou.fm"));  
        return permission;
    }
    
    
    /**
     * 新版本下载地址
     * 
     * @return
     */
    public static String getAppUrl() {
        return Version_Url;
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

    public static int[] getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        return new int[] {
                display.getWidth(), display.getHeight()
        };
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
     */
    public static boolean IsRunOnBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo != null && tasksInfo.size() > 0 && context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否锁屏
     * 
     * @return true 锁屏状态， false 非锁屏状态
     */
    public static boolean isScreenOff(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context
                .getSystemService(Activity.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    /**
     * 判断是否已经创建该电台快捷方式
     * 
     * @param context
     * @return
     */
    public static boolean isAddSingleRadioShortCut(Context context, String radioName) {
        boolean isInstallShortcut = false;
        final ContentResolver cr = context.getContentResolver();
        // 本人的2.2系统是”com.android.launcher2.settings”,网上见其他的为"com.android.launcher.settings"
        String AUTHORITY = "";
        if (android.os.Build.VERSION.SDK_INT < 8) {
            AUTHORITY = "com.android.launcher.settings";
        } else {
            AUTHORITY = "com.android.launcher2.settings";
        }
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI, new String[] {
                "title", "iconResource"
        }, "title=?", new String[] {
            radioName
        }, null);// XXX表示应用名称。
        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
            c.close();
        }
        return isInstallShortcut;
    }

    /**
     * @param context
     * @param path
     * @return
     */
    public static InputStream getAssetsFile(Context context, String path) {
        AssetManager am = context.getAssets();
        InputStream is;
        try {
            is = am.open(path);
        } catch (IOException e) {
            is = null;
        }
        return is;
    }

    /**
     * 获取手机内部存储
     * 
     * @param context
     * @return
     */
    public static long getInternalAvailableBlocks(Context context) {
        // String mTotalSize = "内部总容量：";
        // String mAvailSize = "内部剩余容量：";
        StatFs statInternal = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statInternal.getBlockSize();
        long totalBlocks = statInternal.getBlockCount() * blockSize;
        long availableBlocks = statInternal.getAvailableBlocks() * blockSize;
        // mTotalSize += Formatter.formatFileSize(context, totalBlocks *
        // blockSize);
        // mAvailSize += Formatter.formatFileSize(context, availableBlocks *
        // blockSize);
        return availableBlocks;
    }

    // /** 分类文件名称 */
    // public static final String CATEGORY_FILE_NAME = "category.txt";
    //
    // /** 地区文件名称 */
    // public static final String LOCATION_FILE_NAME = "location.txt";

    /**
     * 资源包文件,一定要和/assets/下的资源包名称一样
     */
    public static final String SOURCE_FILE_NAME = "source.zip";

    public static String getPackagePath(Context context) {
        return context.getFilesDir().getParent();
    }

    public static String getSourcePath(Context context) {
        return context.getFilesDir().getPath() + "/" + SOURCE_FILE_NAME;
    }

    public static String getImagePath(Context context) {
        return context.getFilesDir().getParent() + "/image/";
    }

    public static String getFilesPath(Context context) {
        return context.getFilesDir().getParent() + "/files/";
    }

    /**
     * 获取中等图片缓存的key
     * 
     * @param radioKey
     * @return
     */
    public static String getPlayImageKey(long radioKey) {
        return new StringBuilder().append(radioKey).append("_medium").toString();
    }

    public static String getPlayImageUrl(String url) {
        return url.replace("90x90", "160x160");
    }

    /**
     * 获取状态栏高度
     * 
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Application.TELEPHONY_SERVICE))
                .getDeviceId();
    }

    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Application.TELEPHONY_SERVICE))
                .getSubscriberId();
    }

    public static String getMCC(Context context) {
        String mcc = "";
        String imsi = getIMSI(context);
        if (!TextUtils.isEmpty(imsi) && imsi.length() > 2) {
            mcc = imsi.substring(0, 3);
        }
        return mcc;
    }

    public static String getMNC(Context context) {
        String mnc = "";
        String imsi = getIMSI(context);
        if (!TextUtils.isEmpty(imsi) && imsi.length() > 5) {
            mnc = imsi.substring(3, 5);
        }
        return mnc;
    }

    public static String getMobileProvidersName(Context context) {
        String providersName = null;
        String imsi = getIMSI(context);
        if (TextUtils.isEmpty(imsi)) {
            return "";
        }
        // imsi号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
            providersName = "中国移动";
        } else if (imsi.startsWith("46001")) {
            providersName = "中国联通";
        } else if (imsi.startsWith("46003")) {
            providersName = "中国电信";
        }
        return providersName;
    }

    /**
     * 安装APK
     * 
     * @param context
     * @param filePath APK存放路径
     */
    public static void installApk(Context context, String filePath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static String getTelImsi(Context context) {
        String imsi = "";
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imsi = tm.getSubscriberId();
        if (!StringUtil.isEmpty(imsi) && imsi.length() > 15) {
            imsi = imsi.substring(0, 15);
        }
        return StringUtil.isEmpty(imsi) ? "" : imsi;
    }

    public static String getTelImei(Context context) {
        String imei = "";
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        return StringUtil.isEmpty(imei) ? "" : imei;
    }

    /**
     * @return
     */
    public static long getRAMTotalSize() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * @return
     */
    public static long getROMTotalSize() {
        StatFs statInternal = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statInternal.getBlockSize();
        return statInternal.getBlockCount() * blockSize;
    }
    
    /**
     * 获取字符串的长度，中文占2个字符,英文数字占1个字符
     * 
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static double length(String value) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            String temp = value.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为2
                valueLength += 2;
            } else {
                // 其他字符长度为1
                valueLength += 1;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }
    
    /**
     * 判断Intent是否有效
     * 
     * @param context
     * @return true 有效
     */
    public static boolean isIntentAvailable(Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    
    /**
     * 判断一个程序是否显示在前端,根据测试此方法执行效率在11毫秒,无需担心此方法的执行效率
     *
     * @return true--->在前端,false--->不在前端
     */
    public static boolean isApplicationShowing(Context context) {
	     boolean result = false;
	     String packageName = context.getPackageName();
	     ActivityManager am = (ActivityManager) context
	       .getSystemService(Context.ACTIVITY_SERVICE);
	     List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
	     if (appProcesses != null) {
	      for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
	       if (runningAppProcessInfo.processName.equals(packageName)) {
	        int status = runningAppProcessInfo.importance;
	        if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
	          || status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
	         result = true;
	        }
	       }
	      }
	     }
	     return result;
    }

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        if (infos != null && infos.size() > 0) {
            for (RunningAppProcessInfo appProcess : infos) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

}
