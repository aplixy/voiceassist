package com.voiceassist.lixinyu.voiceassist.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

/**
 *
 *
 * Class Name: JsonUtils
 *
 * Description:Gson类库的封装工具类，专门负责解析json数据 ,内部实现了Gson对象的单例
 *
 * Sample: null
 *
 * Author: liwen
 *
 * Date: 2013-3-27
 *
 * Modified History: null
 */
object JsonUtils {

    /** 默认的 `JSON` 日期/时间字段的格式化模式。  */
    val DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"//"yyyy-MM-dd HH:mm:ss SSS" 精确到毫秒

    private val tag = "JsonUtils"
    /**
     * 把对象转换成Json格式
     * @param object 要转换的对象
     * @return
     */
    fun getJsonFromObject(`object`: Any?): String? {
        if (`object` == null) {
            return null
        }
        val gson = Gson()
        try {
            return gson.toJson(`object`)
        } catch (e: Exception) {
            KGLog.e(tag, "目标对象 " + `object`.javaClass.name + " 转换 JSON 字符串时，发生异常!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把对象集合转换成Json格式
     * @param list 要转换的目标集合
     * @return
     */
    fun <T> getJsonFromList(list: List<T>?): String? {
        if (list == null) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<List<T>>() {

        }.type
        try {
            return gson.toJson(list, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "目标对象转换 JSON 字符串时，发生异常!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把嵌套对象集合转换成Json格式
     * @param list 要转换的目标集合
     * @return
     */
    fun <T> getJsonFromNestedList(list: List<List<T>>?): String? {
        if (list == null) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<List<List<T>>>() {

        }.type
        try {
            return gson.toJson(list, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "目标对象转换 JSON 字符串时，发生异常!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成对象
     * @param jsonString
     * @param cls
     * @return
     */
    fun <T> getObjectFromJson(jsonString: String, cls: Class<T>): T? {
        return getObjectFromJson(jsonString, cls, null)
    }

    /**
     * 把Json数据转换成对象
     * @param jsonString
     * @param cls
     * @return
     */
    fun <T> getObjectFromJson(jsonString: String, cls: Class<T>, datePattern: String?): T? {
        var datePattern = datePattern
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val builder = GsonBuilder()
        if (StringUtil.isEmpty(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN
        }
        builder.setDateFormat(datePattern)
        val gson = builder.create()
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson(jsonString, cls)
        } catch (e: Exception) {
            KGLog.e(tag, jsonString + " 无法转换为 " + cls.name + " 对象!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成对象集合
     * @param jsonString
     * @return
     */
    fun <T> getListFromJson(jsonString: String): List<T>? {
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<List<T>>() {

        }.type
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson<List<T>>(jsonString, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "$jsonString 无法转换为对象集合!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成对象集合
     * @param jsonString
     * @return
     */
    fun <T> getListFromJson(jsonString: String, type: Type): List<T>? {
        return getListFromJson(jsonString, type, null)
    }

    /**
     * 把Json数据转换成对象集合
     * @param jsonString 要转换的json数据
     * @param type 类型
     * @param datePattern 日期格式
     * @return
     */
    fun <T> getListFromJson(jsonString: String, type: Type, datePattern: String?): List<T>? {
        var datePattern = datePattern
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val builder = GsonBuilder()
        if (StringUtil.isEmpty(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN
        }
        builder.setDateFormat(datePattern)
        val gson = builder.create()
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson<List<T>>(jsonString, type)
        } catch (e: Exception) {
            KGLog.e(tag, "$jsonString 无法转换为目标对象集合!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成String集合
     * @param jsonString
     * @return
     */
    fun getListStringFromJson(jsonString: String): List<String>? {
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<List<String>>() {

        }.type
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson<List<String>>(jsonString, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "$jsonString 无法转换为String集合!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成Map集合
     * @param jsonString
     * @return
     */
    fun getListMapFromJson(jsonString: String): List<Map<String, Any>>? {
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<List<Map<String, Any>>>() {

        }.type
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson<List<Map<String, Any>>>(jsonString, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "$jsonString 无法转换为Map集合!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 把Json数据转换成Map
     * @param jsonString
     * @return
     */
    fun getMapFromJson(jsonString: String): Map<String, Any>? {
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        val gson = Gson()
        val listType = object : TypeToken<Map<String, Any>>() {

        }.type
        try {
            normalizeJsonString(jsonString)
            return gson.fromJson<Map<String, Any>>(jsonString, listType)
        } catch (e: Exception) {
            KGLog.e(tag, "$jsonString 无法转换为Map集合!")
            e.printStackTrace()
            return null
        }

    }

    /**
     * 根据key 获取Json数据中的value
     * @param jsonString
     * @param key
     * @return
     */
    fun getJsonValue(jsonString: String, key: String): Any? {
        if (StringUtil.isEmpty(jsonString)) {
            return null
        }
        normalizeJsonString(jsonString)
        val map = getMapFromJson(jsonString)
        return if (map != null && map.size > 0) {
            map[key]
        } else null
    }

    private fun normalizeJsonString(jsonString: String) {
        //KGLog.d(tag, "jsonString--->"+jsonString);
        //jsonString = jsonString.replace("\\/", "/");
        //jsonString = jsonString.replace("\\\\/", "/");
    }
}