package com.voiceassist.lixinyu.voiceassist.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixinyu on 2018/1/20.
 */

public class FileUtils {

    private static final String SEPARATOR = File.separator;//路径分隔符

    public static String readAssetsNoSpace(Context context, String fileName) {

        StringBuilder sb = null;

        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str;
            if (null == sb) {
                sb = new StringBuilder();
            }
            while ((str = br.readLine()) != null) {
                sb.append(str.trim().replaceAll(" ", ""));  //把test文档中的内容显示在tv中
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null == sb ? null : sb.toString();
    }

    public static String readRawNoSpace(Context context, int rawId) {

        StringBuilder sb = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            is = context.getResources().openRawResource(rawId);
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            String str;

            if (null == sb) {
                sb = new StringBuilder();
            }

            while ((str = br.readLine()) != null) {
                sb.append(str.trim().replaceAll(" ", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != isr) try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != is) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null == sb ? null : sb.toString();
    }




    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param rawId          资源ID
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRawNoSpace(Context context, int rawId, String storagePath) {

        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.getParentFile().mkdirs();
        }

        if (file.isDirectory()) file = new File(storagePath + SEPARATOR + rawId);

        //File file = new File(storagePath + SEPARATOR + fileName);
        //writeInputStream(storagePath + SEPARATOR + fileName, is);

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        BufferedWriter bw  = null;

        try {
            is = context.getResources().openRawResource(rawId);
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            String str;

            bw  = new BufferedWriter(new FileWriter(file));

            while ((str = br.readLine()) != null) {
                str = str.trim().replaceAll(" ", "");
                bw.write(str);
            }

            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != isr) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != br) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != bw) try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFileSingleLine(String filePath) {
        if (filePath == null) return null;

        BufferedReader reader = null;

        File file = new File(filePath);
        StringBuilder sb = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            sb = null;

            while ((line = reader.readLine()) != null) {
                if (null == sb) sb = new StringBuilder();

                sb.append(line.trim().replaceAll(" ", ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null == sb ? null : sb.toString();
    }


    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }


}
