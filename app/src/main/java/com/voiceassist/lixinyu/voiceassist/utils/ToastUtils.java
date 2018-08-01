package com.voiceassist.lixinyu.voiceassist.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.voiceassist.lixinyu.voiceassist.common.AssistApplication;

public class ToastUtils {
	private static int screenWidth;
	private static int screenHeight;
	
	private static Activity toastActivity;
	private static View commontToastView;
	private static TextView commontToastTextView;
	private static ImageView commontToastImageView;
	
	private static boolean singleToastView = false;
	
	/**
	 * 显示一个Toast
	 * 
	 * @param text
	 */
	public static void showToast(Activity activity, String text) {
		initToastAndShow(activity, text, null, false, null);
	}

	/**
	 * 显示一个Toast
	 * 
	 * @param textRes
	 */
	public static void showToast(Activity activity, int textRes) {
		initToastAndShow(activity, null, textRes, false, null);
	}

	/**
	 * 显示一个Toast
	 * 
	 * @param textRes
	 * @param isShowImg
	 *            是否在文字上方显示一个“无网络”的图片
	 */
	public static void showToast(Activity activity, int textRes, boolean isShowImg) {
		initToastAndShow(activity, null, textRes, true, null);
	}

	/**
	 * 显示一个Toast
	 * 
	 * @param text
	 * @param isShowImg
	 *            是否在文字上方显示一个“无网络”的图片
	 */
	public static void showToast(Activity activity, String text, boolean isShowImg) {
		initToastAndShow(activity, text, null, true, null);
	}

	/**
	 * 显示一个Toast
	 * 
	 * @param textRes
	 * @param imgRes
	 *            在文字上方显示的图片的资源
	 */
	public static void showToast(Activity activity, int textRes, int imgRes) {
		initToastAndShow(activity, null, textRes, true, imgRes);
	}

	/**
	 * 显示一个Toast
	 * 
	 * @param text
	 * @param imgRes
	 *            在文字上方显示的图片的资源
	 */
	public static void showToast(Activity activity, String text, int imgRes) {
		initToastAndShow(activity, text, null, true, imgRes);
	}

	private static void initToastAndShow(Activity activity, String text,
			Integer textRes, boolean isShowImg, Integer imgRes) {
		if(activity == null) return;
		
		
//		if(!singleToastView || (commontToastView == null || toastActivity == null || (toastActivity != null && toastActivity.isFinishing()))){
//			toastActivity = activity;
//			commontToastView = LayoutInflater.from(activity).inflate(R.layout.common_toast_view, null);
//			commontToastTextView = (TextView) commontToastView.findViewById(R.id.common_toast_textview);
//			commontToastImageView = (ImageView) commontToastView.findViewById(R.id.common_toast_imageview);
//		}
//
//		if (isShowImg) {
//			commontToastImageView.setVisibility(View.VISIBLE);
//			if (imgRes != null)
//				commontToastImageView.setImageResource(imgRes);
//			else
//				commontToastImageView.setImageResource(R.drawable.no_net_face);
//		} else {
//			commontToastImageView.setVisibility(View.GONE);
//		}
//
//		Toast toast = new Toast(activity);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		if (text != null)
//			commontToastTextView.setText(text);
//		else if (textRes != null)
//			commontToastTextView.setText(textRes);
//		toast.setView(commontToastView);
//		toast.show();
	}

	public static void showToast(String text) {
		Toast.makeText(AssistApplication.Companion.getInstance(), "" + text, Toast.LENGTH_SHORT).show();
	}
}
