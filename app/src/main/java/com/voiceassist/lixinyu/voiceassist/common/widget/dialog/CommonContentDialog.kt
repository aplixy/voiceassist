package com.voiceassist.lixinyu.voiceassist.common.widget.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R

/**
 * Created by lilidan on 2018/1/31.
 */

class CommonContentDialog(builder: Builder) : CommonDialog(builder), View.OnClickListener {

    var contentTextView: TextView? = null
        private set
    var yesButton: Button? = null
        private set
    var noButton: Button? = null
        private set

    private val contentText: String?

    private val yesBtnText: String?
    private val noBtnText: String?

//    private val onYesClickListener: View.OnClickListener?
//    private val onNoClickListener: View.OnClickListener?

    private var onYesClick: ((View) -> Unit)? = null
    private var onNoClick: ((View) -> Unit)? = null

    init {

        contentText = builder.contentText

        yesBtnText = builder.yesBtnText
        noBtnText = builder.noBtnText

//        onYesClickListener = builder.onYesClickListener
//        onNoClickListener = builder.onNoClickListener

        onYesClick = builder.onYesClick
        onNoClick = builder.onNoClick
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentTextView = view.findViewById(R.id.dialog_common_content_textview)
        yesButton = view.findViewById(R.id.dialog_common_button_yes)
        noButton = view.findViewById(R.id.dialog_common_button_no)

        contentTextView!!.text = contentText

        yesButton!!.setOnClickListener(this)
        noButton!!.setOnClickListener(this)

        if (!TextUtils.isEmpty(yesBtnText)) {
            yesButton!!.text = yesBtnText
        }

        if (!TextUtils.isEmpty(noBtnText)) {
            noButton!!.text = noBtnText
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_common_button_yes -> {
                //onYesClickListener?.onClick(v)

                onYesClick?.invoke(v)
            }
            R.id.dialog_common_button_no -> {
                //onNoClickListener?.onClick(v)
                onNoClick?.invoke(v)
            }
            else -> {
            }
        }
    }

    class Builder(context: Context) : CommonDialog.Builder(context) {

        var contentText: String? = null

        var yesBtnText: String? = null
        var noBtnText: String? = null

//        var onYesClickListener: View.OnClickListener? = null
//        var onNoClickListener: View.OnClickListener? = null

        var onYesClick: ((View) -> Unit)? = null
        var onNoClick: ((View) -> Unit)? = null

        fun contentText(contentText: String): Builder {
            this.contentText = contentText
            return this
        }

        fun yesBtnText(yesBtnText: String): Builder {
            this.yesBtnText = yesBtnText
            return this
        }

        fun noBtnText(noBtnText: String): Builder {
            this.noBtnText = noBtnText
            return this
        }

//        fun onYesClickListener(listener: View.OnClickListener): Builder {
//            this.onYesClickListener = listener
//            return this
//        }
//
//        fun onNoClickListener(listener: View.OnClickListener): Builder {
//            this.onNoClickListener = listener
//            return this
//        }

        fun onYesClickListener(listener: ((View) -> Unit)?): Builder {
            this.onYesClick = listener
            return this
        }

        fun onNoClickListener(listener: ((View) -> Unit)?): Builder {
            this.onNoClick = listener
            return this
        }

        override fun build(): CommonContentDialog {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null)
            return CommonContentDialog(this)
        }
    }
}
