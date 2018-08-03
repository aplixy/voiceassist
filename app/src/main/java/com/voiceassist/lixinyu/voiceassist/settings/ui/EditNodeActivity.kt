package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.common.Constants
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils
import java.io.File

/**
 * Created by lilidan on 2018/1/25.
 */

class EditNodeActivity : BaseActivity(), IEmptyable {

    private var mBtnAddNode: ImageView? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: NodeListAdapter? = null

    private var mNodeList: MutableList<Node>? = null

    private var mDeleteDialog: CommonContentDialog? = null

    private var mAnimOut: Animation? = null
    private var mAnimIn: Animation? = null
    private var mAlreayOut: Boolean = false

    private var mEmptyViewStub: ViewStub? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_node)

        initView()
        initData()
        initListener()
    }

    private fun initView() {
        mBtnAddNode = findViewById(R.id.edit_node_add_button)
        mRecyclerView = findViewById(R.id.edit_node_recyclerview)
        mEmptyViewStub = findViewById(R.id.edit_node_empty_view_stub)
    }

    private fun initData() {
        title = "添加或编辑节点"

        mNodeList = MainActivity.mAllData!!.nodes

        mAdapter = NodeListAdapter(this@EditNodeActivity, mNodeList)

        mRecyclerView!!.layoutManager = LinearLayoutManager(this@EditNodeActivity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.addItemDecoration(RecyclerViewDivider(this@EditNodeActivity, LinearLayoutManager.HORIZONTAL))
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.adapter = mAdapter

        justifyDisplayEmptyView()

        mAnimOut = AnimationUtils.loadAnimation(this, R.anim.out_to_bottom)
        mAnimIn = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom)

        ToastUtils.showToast("长按可删除")
    }

    private fun initListener() {
        mAdapter!!.setOnItemClickListener(object : NodeListAdapter.OnItemClickListener {


            override var onClick: ((NodeListAdapter, Int, Node?) -> Unit)? = {
                adapter, position, node ->

                val intent = Intent(this@EditNodeActivity, NodeAddEditActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("node", node)
                startActivityForResult(intent, REQ_ADD_EDIT)
            }

            override var onLongClick: ((NodeListAdapter, Int, Node?) -> Unit)? = {
                adapter, position, node ->

                if (mDeleteDialog == null) {
                    mDeleteDialog = CommonContentDialog.Builder(this@EditNodeActivity)
                            .contentText("确认删除" + node?.cnName + "节点吗？")
                            .onYesClickListener { v ->
                                val objPos = v.getTag(R.id.node_delete_position_tag)
                                val objNode = v.getTag(R.id.node_delete_entity_tag)

                                var pos = -1
                                if (null != objPos && objPos is Int) {
                                    pos = objPos
                                } else {
                                    pos = position
                                }

                                var realNode: Node? = null
                                if (null != objNode && objNode is Node) {
                                    realNode = objNode
                                } else {
                                    realNode = node
                                }

                                mDeleteDialog!!.dismiss()

                                // start delete
                                mNodeList!!.removeAt(pos)
                                mAdapter!!.notifyDataSetChanged()
                                justifyDisplayEmptyView()

                                if (null != realNode) {
                                    deleteRelationship(realNode)
                                    deleteVoiceFile(realNode.audioPath)
                                }

                                MainActivity.saveAllDatas()
                            }
                            .onNoClickListener { mDeleteDialog!!.dismiss() }
                            .build()
                } else {
                    mDeleteDialog?.contentTextView?.text = "确认删除“" + node?.cnName + "”节点吗？"
                }

                val yesButton = mDeleteDialog!!.yesButton
                if (null != yesButton) {
                    yesButton.setTag(R.id.node_delete_position_tag, position)
                    yesButton.setTag(R.id.node_delete_entity_tag, node)
                }
                mDeleteDialog!!.show()
            }
        })

        mBtnAddNode!!.setOnClickListener { startActivityForResult(Intent(this@EditNodeActivity, NodeAddEditActivity::class.java), REQ_ADD_EDIT) }


        mRecyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) {
                    if (mAlreayOut) {
                        mBtnAddNode!!.startAnimation(mAnimIn)
                        mAlreayOut = false
                        mBtnAddNode!!.isClickable = true
                    }
                } else {
                    if (!mAlreayOut) {
                        mBtnAddNode!!.startAnimation(mAnimOut)
                        mAlreayOut = true
                        mBtnAddNode!!.isClickable = false
                    }
                }
            }
        })


    }

    private fun deleteRelationship(node: Node?) {
        if (null == node) return

        val deleteId = node.id
        MainActivity.mNodesMap!!.remove(deleteId)

        // 删除关系列表中相关内容
        val relationshipList = MainActivity.mAllData!!.relationship
        if (null != relationshipList) {
            var i = 0
            var level1Size = relationshipList.size
            while (i < level1Size) {
                val relationship = relationshipList[i]
                if (null == relationship) {
                    i++
                    continue
                }
                if (relationship.firstLevelNodeId == deleteId) {
                    relationshipList.removeAt(i)
                    level1Size--
                    continue
                } else {
                    val secondLevelNodeList = relationship.secondLevelNodes
                    if (null != secondLevelNodeList) {
                        var j = 0
                        var level2Size = secondLevelNodeList.size
                        while (j < level2Size) {
                            val secondLevelNode = secondLevelNodeList[j]
                            if (null == secondLevelNode) {
                                j++
                                continue
                            }
                            if (secondLevelNode.secondLevelNodeId == deleteId) {
                                secondLevelNodeList.removeAt(j)
                                level2Size--
                                continue
                            }
                            j++
                        }
                    }
                }
                i++
            }
        }
    }

    private fun deleteVoiceFile(filePath: String?) {
        var filePath = filePath
        if (null == filePath || filePath.length == 0) return

        if (!filePath.contains(Constants.ROOT_PATH)) {
            filePath = Constants.ROOT_PATH + filePath
        }

        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return

        if (Activity.RESULT_OK != resultCode) return

        when (requestCode) {
            REQ_ADD_EDIT -> {
                val position = data.getIntExtra("position", -1)
                val node = data.getSerializableExtra("node") as Node

                if (position != -1) {
                    mNodeList!![position] = node
                } else {
                    mNodeList!!.add(0, node)
                }

                MainActivity.mNodesMap!![node.id] = node
                mAdapter!!.notifyDataSetChanged()
                justifyDisplayEmptyView()
                MainActivity.saveAllDatas()
            }

            else -> {
            }
        }
    }

    override fun justifyDisplayEmptyView() {
        if (null == mNodeList || mNodeList!!.size == 0) {
            mEmptyViewStub!!.visibility = View.VISIBLE
        } else {
            mEmptyViewStub!!.visibility = View.GONE
        }
    }

    companion object {

        private val REQ_ADD_EDIT = 0x100
    }
}
