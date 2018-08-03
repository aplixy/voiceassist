package com.voiceassist.lixinyu.voiceassist.utils

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.voiceassist.lixinyu.voiceassist.common.AssistApplication

object ToastUtils {
    private val screenWidth: Int = 0
    private val screenHeight: Int = 0

    private val toastActivity: Activity? = null
    private val commontToastView: View? = null
    private val commontToastTextView: TextView? = null
    private val commontToastImageView: ImageView? = null

    private val singleToastView = false

    /**
     * 显示一个Toast
     *
     * @param text
     */
    fun showToast(activity: Activity, text: String) {
        initToastAndShow(activity, text, null, false, null)
    }

    /**
     * 显示一个Toast
     *
     * @param textRes
     */
    fun showToast(activity: Activity, textRes: Int) {
        initToastAndShow(activity, null, textRes, false, null)
    }

    /**
     * 显示一个Toast
     *
     * @param textRes
     * @param isShowImg
     * 是否在文字上方显示一个“无网络”的图片
     */
    fun showToast(activity: Activity, textRes: Int, isShowImg: Boolean) {
        initToastAndShow(activity, null, textRes, true, null)
    }

    /**
     * 显示一个Toast
     *
     * @param text
     * @param isShowImg
     * 是否在文字上方显示一个“无网络”的图片
     */
    fun showToast(activity: Activity, text: String, isShowImg: Boolean) {
        initToastAndShow(activity, text, null, true, null)
    }

    /**
     * 显示一个Toast
     *
     * @param textRes
     * @param imgRes
     * 在文字上方显示的图片的资源
     */
    fun showToast(activity: Activity, textRes: Int, imgRes: Int) {
        initToastAndShow(activity, null, textRes, true, imgRes)
    }

    /**
     * 显示一个Toast
     *
     * @param text
     * @param imgRes
     * 在文字上方显示的图片的资源
     */
    fun showToast(activity: Activity, text: String, imgRes: Int) {
        initToastAndShow(activity, text, null, true, imgRes)
    }

    private fun initToastAndShow(activity: Activity?, text: String?,
                                 textRes: Int?, isShowImg: Boolean, imgRes: Int?) {
        if (activity == null) return


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

    fun showToast(text: String) {
        Toast.makeText(AssistApplication.instance, "" + text, Toast.LENGTH_SHORT).show()
    }
}
