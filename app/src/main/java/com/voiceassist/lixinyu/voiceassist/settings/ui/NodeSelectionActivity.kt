package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.entity.vo.NodeSelectVo
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeSelectionAdapter

import java.util.ArrayList

/**
 * 选择结点页面，这是一个工具型页面，跳转到该页面即可通过勾选的方式选择结点，可用于编辑一级结点和二级结点<br></br>
 * @input title - StringExtra 标题
 * @input selected_nodes_id - StringList 已选中的结点id列表
 * @author Created by lilidan on 2018/1/25.
 */

class NodeSelectionActivity : BaseActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: NodeSelectionAdapter? = null
    private var mVoList: MutableList<NodeSelectVo>? = null

    private var mTipDialog: CommonContentDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_selection)

        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.node_selection_recyclerview)
    }

    private fun initData() {
        val title = intent.getStringExtra("title")
        if (null != title && title.length > 0) {
            setTitle(title)
        } else {
            setTitle("请选择要添加的节点")
        }

        val selectedIdList = intent.getSerializableExtra("selected_nodes_id") as List<String>

        mVoList = ArrayList()
        for (node in MainActivity.mAllData!!.nodes) {
            if (null == node) continue

            val vo = NodeSelectVo()
            vo.isSelected = null != selectedIdList && selectedIdList.contains(node.id)
            vo.node = node
            mVoList!!.add(vo)
        }

        mAdapter = NodeSelectionAdapter(this, mVoList)

        mRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL))
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.adapter = mAdapter
    }

    private fun initEvent() {

    }

    private fun setResult() {
        val selectedList = ArrayList<Node>()
        for (vo in mVoList!!) {
            if (null == vo) continue

            if (vo.isSelected) selectedList.add(vo.node)
        }

        setResult(Activity.RESULT_OK, Intent().putExtra("selected_node_list", selectedList))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.add_node, menu)//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                setResult()
                super.finish()
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
                        mTipDialog!!.dismiss()
                        super@NodeSelectionActivity.finish()
                    }
                    .onYesClickListener { mTipDialog!!.dismiss() }
                    .build()
        }

        mTipDialog!!.show()
    }
}
