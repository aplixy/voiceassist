package com.voiceassist.lixinyu.voiceassist.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;


public class LoadingDialog extends Dialog {
	
	private TextView tvMessage;

	public LoadingDialog(Context context) {
		super(context, R.style.loading_dialog_style);
		this.setContentView(R.layout.loading_dialog_layout);
		this.setCanceledOnTouchOutside(false);
		
		tvMessage = (TextView) findViewById(R.id.loading_dialog_textview);
	}
	
	public void setText(CharSequence str){
		tvMessage.setText(str);
	}
	
	public void setText(int res){
		tvMessage.setText(res);
	}
   
}
