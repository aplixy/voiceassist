package com.voiceassist.lixinyu.voiceassist.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.utils.DisplayUtils

/**
 * Created by lilidan on 2018/1/25.
 */

open class CommonDialog private constructor(builder: Builder, resStyle: Int) : Dialog(builder.context, resStyle) {

    protected var mContext: Context
    protected var height: Int = 0
    protected var width: Int = 0
    protected var cancelTouchout: Boolean = false
    protected var view: View


    constructor(builder: Builder) : this(builder, R.style.editDialog) {}

    init {
        mContext = builder.context
        height = builder.height
        width = builder.width
        cancelTouchout = builder.cancelTouchout
        view = builder.view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view)

        setCanceledOnTouchOutside(cancelTouchout)


        val win = window
        val lp = win!!.attributes
        lp.gravity = Gravity.CENTER

        if (height > 0) lp.height = height
        if (width > 0) lp.width = width

        win.attributes = lp
    }


    open class Builder(var context: Context) {
        var height: Int = 0
        var width: Int = 0
        var cancelTouchout: Boolean = false
        lateinit var view: View
        protected var resStyle = -1

        fun view(resView: Int): Builder {
            view = LayoutInflater.from(context).inflate(resView, null)
            return this
        }

        fun heightpx(`val`: Int): Builder {
            height = `val`
            return this
        }

        fun widthpx(`val`: Int): Builder {
            width = `val`
            return this
        }

        fun heightdp(`val`: Int): Builder {
            height = DisplayUtils.dip2px(context, `val`.toFloat())
            return this
        }

        fun widthdp(`val`: Int): Builder {
            width = DisplayUtils.dip2px(context, `val`.toFloat())
            return this
        }

        fun heightDimenRes(dimenRes: Int): Builder {
            height = context.resources.getDimensionPixelOffset(dimenRes)
            return this
        }

        fun widthDimenRes(dimenRes: Int): Builder {
            width = context.resources.getDimensionPixelOffset(dimenRes)
            return this
        }

        fun style(resStyle: Int): Builder {
            this.resStyle = resStyle
            return this
        }

        fun cancelTouchout(`val`: Boolean): Builder {
            cancelTouchout = `val`
            return this
        }

        fun addViewOnclick(viewRes: Int, listener: View.OnClickListener): Builder {
            view.findViewById<View>(viewRes).setOnClickListener(listener)
            return this
        }


        open fun build(): CommonDialog {
            return if (resStyle != -1) {
                CommonDialog(this, resStyle)
            } else {
                CommonDialog(this)
            }
        }
    }
}
