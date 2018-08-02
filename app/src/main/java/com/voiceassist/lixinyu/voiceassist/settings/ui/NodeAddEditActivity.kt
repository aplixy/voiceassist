package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView

import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.common.Constants
import com.voiceassist.lixinyu.voiceassist.common.widget.RecordButton
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.utils.PlayerUtils
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

import java.io.File

import io.reactivex.functions.Consumer
import pub.devrel.easypermissions.AppSettingsDialog

/**
 * Created by lilidan on 2018/1/30.
 */

class NodeAddEditActivity : BaseActivity(), View.OnClickListener {

    private var mEtId: EditText? = null
    private var mEtCnName: EditText? = null
    private var mEtAudioPath: EditText? = null
    private var mEtIcon: EditText? = null

    private var mIvPlay: ImageView? = null

    private var mBtnRecord: RecordButton? = null

    private var mPosition: Int = 0
    private var mNode: Node? = null

    private var mTipDialog: CommonContentDialog? = null

    private var mRxPermissions: RxPermissions? = null

    private val resolveInfo: ResolveInfo
        get() {
            val intent = Intent(RecognizerIntent.ACTION_WEB_SEARCH)
            return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_node)

        initView()
        initData()
        initListener()


    }

    private fun initView() {
        mEtId = findViewById(R.id.add_node_id_edittext)
        mEtCnName = findViewById(R.id.add_node_cnname_edittext)
        mEtAudioPath = findViewById(R.id.add_node_audioPath_edittext)
        mEtIcon = findViewById(R.id.add_node_icon_edittext)

        mIvPlay = findViewById(R.id.add_node_play_imageview)

        mBtnRecord = findViewById(R.id.add_node_record_button)
    }

    private fun initData() {
        this.mPosition = intent.getIntExtra("position", -1)
        this.mNode = intent.getSerializableExtra("node") as Node

        if (null != mNode) {
            val id = mNode!!.id + ""
            mEtId!!.setText(mNode!!.id + "")
            mEtCnName!!.setText(mNode!!.cnName + "")
            mEtAudioPath!!.setText(mNode!!.audioPath + "")
            mEtIcon!!.setText(mNode!!.icon + "")

            mEtAudioPath!!.setSelection(mEtAudioPath!!.text.length)

            if (null != id && id.length > 0) mEtId!!.isEnabled = false
        } else {
            mEtId!!.text = null
            mEtCnName!!.text = null
            mEtAudioPath!!.text = null
            mEtIcon!!.text = null
            mEtId!!.isEnabled = true
        }

        requestPermissions()

    }

    private fun initListener() {

        mBtnRecord!!.setOnBeforeRecordListener(RecordButton.OnBeforeRecordListener {
            if (null == mRxPermissions) mRxPermissions = RxPermissions(this@NodeAddEditActivity)
            if (!mRxPermissions!!.isGranted(Manifest.permission.RECORD_AUDIO)) {
                requestPermissions()
                return@OnBeforeRecordListener false
            }

            val id = mEtId!!.text.toString()
            if (null == id || id.trim { it <= ' ' }.length == 0) {
                ToastUtils.showToast("id不能为空")
                return@OnBeforeRecordListener false
            }

            mBtnRecord!!.setSavePath(getFilePath(id))
            true
        })

        mBtnRecord!!.setOnFinishedRecordListener { audioPath ->
            if (null != audioPath && audioPath.length > 0) {
                ToastUtils.showToast("录制完成")
                mEtAudioPath!!.setText(audioPath + "")
                mEtAudioPath!!.setSelection(audioPath.length)
            }
        }

        mIvPlay!!.setOnClickListener(this)
    }

    private fun getFilePath(id: String): String {
        return Constants.AUDIO_RECORD_PATH + id + "." + RECORD_AUDIO_FORMAT
    }

    private fun requestPermissions() {
        if (null == mRxPermissions) mRxPermissions = RxPermissions(this@NodeAddEditActivity)


        mRxPermissions!!.requestEach(Manifest.permission.RECORD_AUDIO)
                .subscribe { permission ->
                    if (permission.granted) {
                        // 用户已经同意该权限

                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        AppSettingsDialog.Builder(this@NodeAddEditActivity).build().show()// 用户选择『不开提示』时引导用户手动开启权限
                    }
                }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.add_node_play_imageview -> {
                val audioPath = mEtAudioPath!!.text.toString()
                if (null != audioPath && audioPath.length > 0) {
                    PlayerUtils.getInstance(this).play(audioPath)
                } else {
                    ToastUtils.showToast("无效路径")
                }
            }

            else -> {
            }
        }
    }


    private fun save(): Boolean {
        if (verify()) {
            if (null == mNode) mNode = Node()

            mNode!!.id = mEtId!!.text.toString().trim { it <= ' ' }
            mNode!!.cnName = mEtCnName!!.text.toString().trim { it <= ' ' }
            mNode!!.audioPath = mEtAudioPath!!.text.toString().trim { it <= ' ' }
            mNode!!.icon = mEtIcon!!.text.toString().trim { it <= ' ' }

            val data = Intent()
            data.putExtra("position", mPosition)
            data.putExtra("node", mNode)
            setResult(Activity.RESULT_OK, data)

            return true
        }

        return false
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

    override fun onFinishCalled() {

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.add_node, menu)//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_save -> {
                if (save()) {
                    super.finish()
                } else {
                    ToastUtils.showToast("保存失败")
                }
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        exitTip()
    }

    private fun exitTip() {
        if (null == mTipDialog) {
            mTipDialog = CommonContentDialog.Builder(this)
                    .contentText("确认退出吗？")
                    .yesBtnText("容朕想想")
                    .noBtnText("去意已决")
                    .onNoClickListener {
                        val id = mEtId!!.text.toString().trim { it <= ' ' }
                        if (null != id && id.length > 0) {
                            val file = File(getFilePath(id))
                            if (file.exists()) {
                                file.delete()
                            }
                        }
                        mTipDialog!!.dismiss()

                        super@NodeAddEditActivity.finish()
                    }
                    .onYesClickListener { mTipDialog!!.dismiss() }
                    .build()
        }

        mTipDialog!!.show()
    }

    companion object {

        private val RECORD_AUDIO_FORMAT = "m4a"
    }
}
