package com.voiceassist.lixinyu.voiceassist.utils

import android.content.Context
import android.os.Environment

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

/**
 * Created by lixinyu on 2018/1/20.
 */

object FileUtils {

    private val SEPARATOR = File.separator//路径分隔符


    /**
     * 获取内置SD卡路径
     * @return
     */
    val innerSDCardPath: String
        get() = Environment.getExternalStorageDirectory().path

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    val extSDCardPath: List<String>
        get() {
            val lResult = ArrayList<String>()
            try {
                val rt = Runtime.getRuntime()
                val proc = rt.exec("mount")
                val `is` = proc.inputStream
                val isr = InputStreamReader(`is`)
                val br = BufferedReader(isr)
                var line: String?
                while (true) {
                    line = br.readLine()
                    if (line == null) break

                    if (line.contains("extSdCard")) {
                        val arr = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val path = arr[1]
                        val file = File(path)
                        if (file.isDirectory) {
                            lResult.add(path)
                        }
                    }
                }
                isr.close()
            } catch (e: Exception) {
            }

            return lResult
        }

    fun readAssetsNoSpace(context: Context, fileName: String): String? {

        var sb: StringBuilder? = null

        try {
            val `is` = context.resources.assets.open(fileName)
            val isr = InputStreamReader(`is`, "UTF-8")
            val br = BufferedReader(isr)
            var str: String?
            if (null == sb) {
                sb = StringBuilder()
            }
            while (true) {
                str = br.readLine()
                if (str == null) break

                sb.append(str.trim { it <= ' ' }.replace(" ".toRegex(), ""))  //把test文档中的内容显示在tv中
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb?.toString()
    }

    fun readRawNoSpace(context: Context, rawId: Int): String? {

        //KGLog.d("context--->$context")

        var sb: StringBuilder? = null

        var `is`: InputStream? = null
        var isr: InputStreamReader? = null
        var br: BufferedReader? = null

        try {
            `is` = context.resources.openRawResource(rawId)
            isr = InputStreamReader(`is`!!, "UTF-8")
            br = BufferedReader(isr)
            var str: String?

            if (null == sb) {
                sb = StringBuilder()
            }

            //KGLog.v("br.readText()--->${br.readText()}")

            while (true) {
                str = br.readLine()

                if (str == null) break

                sb.append(str.trim { it <= ' ' }.replace(" ".toRegex(), ""))
            }


        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != br)
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != isr)
                try {
                    isr.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != `is`)
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            //KGLog.i("sb--->${sb.toString()}")

        }

        //KGLog.d("#1--->readRawNoSpace--->${sb.toString()}")


        return sb?.toString()
    }


    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param rawId          资源ID
     * @param storagePath 目标文件夹的路径
     */
    fun copyFilesFromRawNoSpace(context: Context?, rawId: Int, storagePath: String) {

        if (context == null) return

        var file = File(storagePath)
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.parentFile.mkdirs()
        }

        if (file.isDirectory) file = File(storagePath + SEPARATOR + rawId)

        //File file = new File(storagePath + SEPARATOR + fileName);
        //writeInputStream(storagePath + SEPARATOR + fileName, is);

        var `is`: InputStream? = null
        var isr: InputStreamReader? = null
        var br: BufferedReader? = null
        var bw: BufferedWriter? = null

        try {
            `is` = context.resources.openRawResource(rawId)
            isr = InputStreamReader(`is`!!, "UTF-8")
            br = BufferedReader(isr)
            var str: String?

            bw = BufferedWriter(FileWriter(file))

            while (true) {
                str = br.readLine()
                if (str == null) break

                str = str.trim { it <= ' ' }.replace(" ".toRegex(), "")
                bw.write(str)
            }

            bw.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != `is`)
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != isr)
                try {
                    `is`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != br)
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != bw)
                try {
                    bw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }
    }

    fun readFileSingleLine(filePath: String?): String? {
        if (filePath == null) return null

        var reader: BufferedReader? = null

        val file = File(filePath)
        var sb: StringBuilder? = null
        try {
            reader = BufferedReader(FileReader(file))
            var line: String? = null

            sb = null

            while (true) {

                line = reader.readLine()
                if (line == null) break

                if (null == sb) sb = StringBuilder()

                sb.append(line.trim { it <= ' ' }.replace(" ".toRegex(), ""))
            }

            //reader.readLine()?.run { line = this; line != null} ?: false
            //{ line = reader.readLine(); line }() != null
//            val aa: (String) -> Boolean = {
//                line = it
//                it != null
//            }
//
//            while (aa(reader.readLine())) {
//                if (null == sb) sb = StringBuilder()
//
//                sb.append(line?.trim { it <= ' ' }?.replace(" ".toRegex(), ""))
//            }



//            while (reader.readLine()?.apply { line = this } != null) {
//                if (null == sb) sb = StringBuilder()
//
//                sb.append(line?.trim { it <= ' ' }?.replace(" ".toRegex(), ""))
//            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != reader)
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }

        //KGLog.d("2nd--->readFileSingleLine--->${sb.toString()}")


        return sb?.toString()
    }


    fun saveTextToFile(text: String?, filePath: String): Boolean {

        val file = File(filePath)
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.parentFile.mkdirs()
        }

        var fw: FileWriter? = null
        var bw: BufferedWriter? = null

        try {

            fw = FileWriter(file)

            bw = BufferedWriter(fw)

            bw.write(text)

            bw.flush()

            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != bw)
                try {
                    bw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            if (null != fw)
                try {
                    fw.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }

        return false
    }


}
