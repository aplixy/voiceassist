package com.voiceassist.lixinyu.voiceassist.settings.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

/**
 * Created by lilidan on 2018/1/25.
 */

class EditNodeDialog(context: Context) : Dialog(context, R.style.editDialog), View.OnClickListener {

    private var mEtId: EditText? = null
    private var mEtCnName: EditText? = null
    private var mEtAudioPath: EditText? = null
    private var mEtIcon: EditText? = null

    private var mBtnNo: Button? = null
    private var mBtnYes: Button? = null

    private var mNode: Node? = null

    private var mPosition: Int = 0

    private var mListener: OnPositiveButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_edit_node)

        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mEtId = findViewById(R.id.dialog_edit_node_id_edittext)
        mEtCnName = findViewById(R.id.dialog_edit_node_cnname_edittext)
        mEtAudioPath = findViewById(R.id.dialog_edit_node_audioPath_edittext)
        mEtIcon = findViewById(R.id.dialog_edit_node_icon_edittext)

        mBtnNo = findViewById(R.id.dialog_edit_node_button_no)
        mBtnYes = findViewById(R.id.dialog_edit_node_button_yes)
    }

    private fun initData() {
        renderView()

    }

    private fun initEvent() {
        mBtnNo!!.setOnClickListener(this)
        mBtnYes!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_edit_node_button_no -> {
                dismiss()
            }

            R.id.dialog_edit_node_button_yes -> {
                save()
            }

            else -> {
            }
        }
    }

    private fun save() {
        if (null == mNode) mNode = Node()

        if (verify()) {
            mNode!!.id = mEtId!!.text.toString().trim { it <= ' ' }
            mNode!!.cnName = mEtCnName!!.text.toString().trim { it <= ' ' }
            mNode!!.audioPath = mEtAudioPath!!.text.toString().trim { it <= ' ' }
            mNode!!.icon = mEtIcon!!.text.toString().trim { it <= ' ' }

            if (null != mListener) {
                mListener!!.onDialogPositiveClick(mPosition, mNode)
            }

            dismiss()
        }
    }

    private fun verify(): Boolean {
        val id = mEtId!!.text.toString().trim { it <= ' ' }
        if (null == id || id.length == 0) {
            ToastUtils.showToast("id不能为空")
            return false
        }

        val cnName = mEtCnName!!.text.toString().trim { it <= ' ' }
        if (null == cnName || cnName.length == 0) {
            ToastUtils.showToast("cnName不能为空")
            return false
        }

        val audioPath = mEtAudioPath!!.text.toString().trim { it <= ' ' }
        if (null == audioPath || audioPath.length == 0) {
            ToastUtils.showToast("audioPath不能为空")
            return false
        }

        return true
    }


    interface OnPositiveButtonClickListener {
        fun onDialogPositiveClick(position: Int, node: Node?)
    }

    fun setOnPositiveButtonClickListener(listener: OnPositiveButtonClickListener) {
        this.mListener = listener
    }

    fun setData(position: Int, node: Node?) {
        this.mPosition = position
        this.mNode = node

        if (null != mEtId) {
            renderView()
        }
    }

    private fun renderView() {


        if (null != mNode) {
            val id = mNode!!.id + ""
            mEtId!!.setText(mNode!!.id + "")
            mEtCnName!!.setText(mNode!!.cnName + "")
            mEtAudioPath!!.setText(mNode!!.audioPath + "")
            mEtIcon!!.setText(mNode!!.icon + "")

            if (null != id && id.length > 0) mEtId!!.isEnabled = false
        } else {
            mEtId!!.text = null
            mEtCnName!!.text = null
            mEtAudioPath!!.text = null
            mEtIcon!!.text = null
            mEtId!!.isEnabled = true
        }
    }
}
