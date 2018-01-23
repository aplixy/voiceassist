package com.voiceassist.lixinyu.voiceassist.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>Class Name: JsonUtils</p>
 * <p>Description:Gson类库的封装工具类，专门负责解析json数据 ,内部实现了Gson对象的单例   </p>
 * <p>Sample: null</p>
 * <p>Author: liwen</p>
 * <p>Date: 2013-3-27</p>
 * <p>Modified History: null</p>
 */
public class JsonUtils {
	
	/** 默认的 {@code JSON} 日期/时间字段的格式化模式。 */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";//"yyyy-MM-dd HH:mm:ss SSS" 精确到毫秒
    
    private static String tag = "JsonUtils";
	
	private JsonUtils(){
	}
	/**
	 * 把对象转换成Json格式
	 * @param object 要转换的对象
	 * @return
	 */
	public static String getJsonFromObject(Object object){
		if(object ==null){
			return null;
		}
		Gson gson = new Gson();
		try {
			 return gson.toJson(object);
		} catch (Exception e) {
			KGLog.e(tag, "目标对象 " + object.getClass().getName() + " 转换 JSON 字符串时，发生异常!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把对象集合转换成Json格式
	 * @param list 要转换的目标集合
	 * @return
	 */
	public static <T> String getJsonFromList(List<T> list){
		if(list ==null){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<T>>(){}.getType();
		try {
			return gson.toJson(list, listType);
		} catch (Exception e) {
			KGLog.e(tag, "目标对象转换 JSON 字符串时，发生异常!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把嵌套对象集合转换成Json格式
	 * @param list 要转换的目标集合
	 * @return
	 */
	public static <T> String getJsonFromNestedList(List<List<T>> list){
		if(list ==null){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<List<T>>>(){}.getType();
		try {
			return gson.toJson(list, listType);
		} catch (Exception e) {
			KGLog.e(tag, "目标对象转换 JSON 字符串时，发生异常!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把Json数据转换成对象
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T>T getObjectFromJson(String jsonString,Class<T> cls){
		return getObjectFromJson(jsonString, cls, null);
	}
	
	/**
	 * 把Json数据转换成对象
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T>T getObjectFromJson(String jsonString,Class<T> cls,String datePattern){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if(StringUtil.isEmpty(datePattern)){
			datePattern = DEFAULT_DATE_PATTERN;
		}
		builder.setDateFormat(datePattern);
		Gson gson = builder.create();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString,cls);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为 " + cls.getName() + " 对象!");
			e.printStackTrace();
			return null;
		}
		
	}
	/**
	 * 把Json数据转换成对象集合
	 * @param jsonString
	 * @return
	 */
	public static <T> List<T> getListFromJson(String jsonString){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<T>>(){}.getType();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString,listType);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为对象集合!");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 把Json数据转换成对象集合
	 * @param jsonString
	 * @return
	 */
	public static <T> List<T> getListFromJson(String jsonString,Type type){
		return getListFromJson(jsonString, type, null);
	}
	/**
	 * 把Json数据转换成对象集合
	 * @param jsonString 要转换的json数据
	 * @param type 类型
	 * @param datePattern 日期格式
	 * @return
	 */
	public static <T> List<T> getListFromJson(String jsonString,Type type,String datePattern){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if(StringUtil.isEmpty(datePattern)){
			datePattern = DEFAULT_DATE_PATTERN;
		}
		builder.setDateFormat(datePattern);
		Gson gson = builder.create();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString,type);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为目标对象集合!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把Json数据转换成String集合
	 * @param jsonString
	 * @return
	 */
	public static List<String> getListStringFromJson(String jsonString){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<String>>(){}.getType();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString, listType);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为String集合!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把Json数据转换成Map集合
	 * @param jsonString
	 * @return
	 */
	public static List<Map<String, Object>> getListMapFromJson(String jsonString){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Map<String,Object>>>(){}.getType();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString, listType);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为Map集合!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 把Json数据转换成Map
	 * @param jsonString
	 * @return
	 */
	public static Map<String,Object> getMapFromJson(String jsonString){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<Map<String,Object>>(){}.getType();
		try {
			normalizeJsonString(jsonString);
			return gson.fromJson(jsonString, listType);
		} catch (Exception e) {
			KGLog.e(tag, jsonString + " 无法转换为Map集合!");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 根据key 获取Json数据中的value
	 * @param jsonString
	 * @param key
	 * @return
	 */
	public static Object getJsonValue(String jsonString,String key){
		if(StringUtil.isEmpty(jsonString)){
			return null;
		}
		normalizeJsonString(jsonString);
		Map<String,Object> map = getMapFromJson(jsonString);
		if(map!=null && map.size()>0){
			return map.get(key);
		}
		return null;
	}
	
	private static void normalizeJsonString(String jsonString){
		//KGLog.d(tag, "jsonString--->"+jsonString);
		//jsonString = jsonString.replace("\\/", "/");
		//jsonString = jsonString.replace("\\\\/", "/");
	}
}