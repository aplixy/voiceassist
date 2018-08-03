package com.voiceassist.lixinyu.voiceassist.common.widget

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R


class LoadingDialog(context: Context) : Dialog(context, R.style.loading_dialog_style) {

    private val tvMessage: TextView

    init {
        this.setContentView(R.layout.loading_dialog_layout)
        this.setCanceledOnTouchOutside(false)

        tvMessage = findViewById(R.id.loading_dialog_textview)
    }

    fun setText(str: CharSequence) {
        tvMessage.text = str
    }

    fun setText(res: Int) {
        tvMessage.setText(res)
    }

}
