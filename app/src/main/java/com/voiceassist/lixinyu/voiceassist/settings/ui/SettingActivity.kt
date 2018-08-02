package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity
import com.voiceassist.lixinyu.voiceassist.utils.AppUtil
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

/**
 * Created by lilidan on 2018/1/25.
 */

class SettingActivity : BaseActivity(), View.OnClickListener {

    private var mNodeItemView: View? = null
    private var mRelationshipItemView: View? = null

    private var mVersionTv: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        mNodeItemView = findViewById(R.id.setting_item_node)
        mRelationshipItemView = findViewById(R.id.setting_item_relationship)
        mVersionTv = findViewById(R.id.setting_item_version_textview)
    }

    private fun initData() {
        title = "设置"
        mVersionTv!!.text = AppUtil.getCurVer(this)
    }

    private fun initListener() {
        mNodeItemView!!.setOnClickListener(this)
        mRelationshipItemView!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.setting_item_node -> {
                startActivity(Intent(this, EditNodeActivity::class.java))
            }
            R.id.setting_item_relationship -> {
                if (null == MainActivity.mAllData!!.nodes || MainActivity.mAllData!!.nodes.size == 0) {
                    ToastUtils.showToast("请先配置结点")
                } else {
                    startActivity(Intent(this, EditRelationshipActivity::class.java))
                }
            }
            else -> {
            }
        }
    }
}
