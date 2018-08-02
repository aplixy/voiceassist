package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity

/**
 * Created by lilidan on 2018/1/25.
 */

class EditActivity : BaseActivity() {

    private var mBtnEditNode: Button? = null
    private var mBtnEditRelationship: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        mBtnEditNode = findViewById(R.id.edit_edit_node_button)
        mBtnEditRelationship = findViewById(R.id.edit_edit_relationship_button)
    }

    private fun initData() {
        title = "编辑节点与关系图"
    }

    private fun initListener() {
        mBtnEditNode?.setOnClickListener { startActivity(Intent(this@EditActivity, EditNodeActivity::class.java)) }

        mBtnEditRelationship?.setOnClickListener { startActivity(Intent(this@EditActivity, EditRelationshipActivity::class.java)) }
    }
}
