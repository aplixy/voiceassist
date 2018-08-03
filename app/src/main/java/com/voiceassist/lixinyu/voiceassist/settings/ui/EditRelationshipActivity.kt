package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout

import com.voiceassist.lixinyu.voiceassist.R
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity
import com.voiceassist.lixinyu.voiceassist.common.rx.RxHelper
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog
import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipListAdapter
import com.voiceassist.lixinyu.voiceassist.utils.KGLog

import java.util.ArrayList
import java.util.HashSet

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

/**
 * Created by lilidan on 2018/1/25.
 */

class EditRelationshipActivity : BaseActivity(), IEmptyable {

    private var mBtnAddRelationship: ImageView? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RelationshipListAdapter? = null
    private var mNodeList: ArrayList<Node>? = null

    private var relationships: MutableList<Relationship>? = null

    private var mIsAllowSort: Boolean = false
    private var mToPosition = -1
    private var mTipDialog: CommonContentDialog? = null


    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    //private ViewGroup mRootViewGroup;

    private var mAnimOut: Animation? = null
    private var mAnimIn: Animation? = null
    private var mAlreayOut: Boolean = false

    private var mEmptyViewStub: ViewStub? = null

    private val selectedRelationIds: ArrayList<String>
        get() {
            val ids = ArrayList<String>()
            if (null != relationships) {
                for (relationship in relationships!!) {
                    if (null == relationship) continue

                    ids.add(relationship.firstLevelNodeId)
                }
            }
            return ids
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_relationship)

        initView()
        initData()
        initListener()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.sort_order, menu)//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_order -> {
                if (!mIsAllowSort) {
                    item.title = "保存排序"
                    mIsAllowSort = true
                } else {
                    sortOrder(mIsAllowSort, mToPosition)
                    mToPosition = -1

                    mIsAllowSort = false
                    item.title = "排序"
                }

                mAdapter!!.setAllowSortOrder(mIsAllowSort)
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mBtnAddRelationship = findViewById(R.id.edit_relationship_add_button)
        mRecyclerView = findViewById(R.id.edit_relationship_recyclerview)
        mSwipeRefreshLayout = findViewById(R.id.edit_relationship_swiperefreshlayout)
        mEmptyViewStub = findViewById(R.id.edit_relationship_empty_view_stub)
    }

    private fun initData() {
        title = "一级节点列表"

        relationships = MainActivity.mAllData!!.relationship
        if (null == relationships) {
            MainActivity.mAllData!!.relationship = ArrayList()
            relationships = MainActivity.mAllData!!.relationship
        }

        mNodeList = ArrayList()

        mAnimOut = AnimationUtils.loadAnimation(this, R.anim.out_to_bottom)
        mAnimIn = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom)


        mSwipeRefreshLayout!!.isEnabled = false
        mSwipeRefreshLayout!!.isRefreshing = true
        Observable.just<List<Relationship>>(relationships!!)
                .map(Function<List<Relationship>, List<Node>> { relationships ->
                    if (null != relationships) {
                        val nodeList = ArrayList<Node>()
                        for (relationship in relationships) {
                            if (null == relationship) continue
                            val node = MainActivity.mNodesMap!![relationship.firstLevelNodeId]
                            if (null != node) {
                                nodeList.add(node)
                            }
                        }
                        return@Function nodeList
                    }
                    null
                })
                .compose(RxHelper.rxSchedulerNewThreadHelper())
                .subscribe({ nodeList ->
                    if (null != nodeList) {
                        mNodeList!!.addAll(nodeList)
                    }
                    mAdapter!!.notifyDataSetChanged()
                    justifyDisplayEmptyView()

                    mSwipeRefreshLayout!!.isRefreshing = false
                }, {
                    mSwipeRefreshLayout!!.isRefreshing = false
                    justifyDisplayEmptyView()
                })

        //        Observable.fromIterable(relationships)
        //                .compose(RxHelper.<Relationship>rxSchedulerNewThreadHelper())
        //                .subscribe(new Observer<Relationship>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //                        mLoadingDialog.show();
        //                    }
        //
        //                    @Override
        //                    public void onNext(Relationship relationship) {
        //                        if (null == relationship) return;
        //
        //                        Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
        //                        if (null != node) {
        //                            mNodeList.add(node);
        //                        }
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        mLoadingDialog.dismiss();
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        mLoadingDialog.dismiss();
        //                        mAdapter.notifyDataSetChanged();
        //                    }
        //                });

        //        for (Relationship relationship : relationships) {
        //            if (null == relationship) continue;
        //            Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
        //            if (null != node) {
        //                mNodeList.add(node);
        //            }
        //        }

        mAdapter = RelationshipListAdapter(this, mNodeList)

        mRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL))
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.adapter = mAdapter


        //创建SimpleItemTouchHelperCallback
        val callback = SimpleItemTouchHelperCallback(mAdapter)
        //用Callback构造ItemtouchHelper
        val touchHelper = ItemTouchHelper(callback)

        mAdapter!!.setItemTouchHelper(touchHelper)

        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(mRecyclerView)


    }

    private fun initListener() {
        mAdapter!!.setOnItemClickListener(object : RelationshipListAdapter.OnItemClickListener {
            override fun onClick(adapter: RelationshipListAdapter, position: Int, node: Node?) {
                var realPosition = -1
                if (null != relationships) {
                    var i = 0
                    for (relationship in relationships!!) {
                        if (null != relationship && null != node && node.id == relationship.firstLevelNodeId) {
                            realPosition = i
                            break
                        }

                        i++
                    }
                }

                val intent = Intent(this@EditRelationshipActivity, EditSecondLevelRelationActivity::class.java)
                intent.putExtra("position", realPosition)
                startActivity(intent)
            }
        })

//        mAdapter!!.setOnItemMoveCompleteListener(object : RelationshipListAdapter.OnItemMoveCompleteListener {
//            override fun onComplete(formPosition: Int, toPosition: Int) {
//                mToPosition = toPosition
//            }
//        })

        mAdapter!!.setOnItemMoveCompleteListener {
            formPosition, toPosition -> mToPosition = toPosition
        }

        mBtnAddRelationship!!.setOnClickListener {
            val intent = Intent(this@EditRelationshipActivity, NodeSelectionActivity::class.java)
            intent.putStringArrayListExtra("selected_nodes_id", selectedRelationIds)
            intent.putExtra("title", "请选择一级结点")

            startActivityForResult(intent, REQ_EDIT_ADD)
        }

        mRecyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) {
                    if (mAlreayOut) {
                        mBtnAddRelationship!!.startAnimation(mAnimIn)
                        mAlreayOut = false
                        mBtnAddRelationship!!.isClickable = true
                    }
                } else {
                    if (!mAlreayOut) {
                        mBtnAddRelationship!!.startAnimation(mAnimOut)
                        mAlreayOut = true
                        mBtnAddRelationship!!.isClickable = false
                    }
                }
            }
        })
    }

    private fun sortOrder(isAllowSort: Boolean, toPosition: Int) {
        if (!isAllowSort) return
        if (toPosition == -1) return

        // 最终目的是要移动relationship列表中的项
        if (null == relationships) return

        // 准备移动的前提条件（prerequisites）
        val moveNode = mNodeList!![toPosition]
        if (null == moveNode || null == moveNode.id) return
        val moveId = moveNode.id

        val preNode = if (toPosition > 0) mNodeList!![toPosition - 1] else null
        val preId = preNode?.id

        // 查找移动的开始及终止位置
        var fromRelationPos = -1
        var toRelationPos = if (null != preId) -1 else 0// 如果没有找到前一项的id，说明目标位置就在列表最开始处
        var preRelationPos = -1

        var i = 0
        for (relationship in relationships!!) {
            if (relationship?.firstLevelNodeId != null) {

                if (fromRelationPos == -1 && relationship.firstLevelNodeId == moveId) {
                    fromRelationPos = i
                }

                if (fromRelationPos != -1 && toRelationPos != -1) {
                    break// 源位置和目标位置都找到的话就要以退出循环了
                } else if (toRelationPos == -1 && null != preId && relationship.firstLevelNodeId == preId) {
                    preRelationPos = i
                }
            }
            i++
        }

        // 确定最终的开始及结束位置
        if (fromRelationPos == -1) return

        if (toRelationPos == -1) {
            if (preRelationPos == -1) {
                // 一直没有找到被移动的结点在移动之后的前一项应该是谁，说明在移动之后该结点没有前一项，那就是在列表最开始处了
                toRelationPos = 0
            } else {
                // 确定了移动完成之后前一项应该的位置，那么被移动的结点在移动之后就应该位于该结点的下一项，哪怕在列表尾也先加1，后续再处理
                toRelationPos = preRelationPos + 1
            }
        }


        // 开始移动
        if (toRelationPos == relationships!!.size) {
            relationships!!.add(relationships!![fromRelationPos])
            relationships!!.removeAt(fromRelationPos)
        } else {
            // 先把原值复制到新位置
            relationships!!.add(toRelationPos, relationships!![fromRelationPos])

            // 再删除原值
            if (fromRelationPos < toRelationPos) {
                // 从上往下移，复制完之后不影响原位置的索引，直接删除原值
                relationships!!.removeAt(fromRelationPos)
            } else {
                // 从下往上移，复制完之后原来的索引也会被“顶”下一个位置，所以删除原值时索引要加1
                relationships!!.removeAt(fromRelationPos + 1)
            }
        }

        MainActivity.saveAllDatas()
    }

    override fun finish() {
        if (!mIsAllowSort) {
            super.finish()
        } else {
            if (null == mTipDialog) {
                mTipDialog = CommonContentDialog.Builder(this)
                        .contentText("排序未保存，确定退出吗？")
                        .noBtnText("取消")
                        .yesBtnText("确定退出")
                        .onNoClickListener { mTipDialog!!.dismiss() }
                        .onYesClickListener {
                            mTipDialog!!.dismiss()
                            super@EditRelationshipActivity.finish()
                        }
                        .build()
            }

            KGLog.d("mTipDialog--->$mTipDialog")
            mTipDialog!!.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQ_EDIT_ADD -> {
                    val selectedNodeList = data.getSerializableExtra("selected_node_list") as List<Node>

                    mNodeList!!.clear()
                    //mNodeList.addAll(selectedNodeList);
                    //mAdapter.notifyDataSetChanged();

                    val selectedIds = HashSet<String>()
                    for (node in selectedNodeList) {
                        if (null == node) continue

                        selectedIds.add(node.id)
                    }

                    val nodeMap = MainActivity.mNodesMap

                    val originalIds = HashSet<String>()
                    //List<Relationship> allDataRelations = MainActivity.mAllData.relationship;
                    var size = relationships!!.size
                    var i = 0
                    while (i < size) {
                        val relationship = relationships!![i]
                        if (null != relationship) {
                            if (!selectedIds.contains(relationship.firstLevelNodeId)) {
                                relationships!!.removeAt(i)
                                size--
                                continue
                            }

                            originalIds.add(relationship.firstLevelNodeId)

                            val node = nodeMap!![relationship.firstLevelNodeId]
                            if (null != node) mNodeList!!.add(node)
                        }

                        i++
                    }

                    for (selectedId in selectedIds) {
                        if (!originalIds.contains(selectedId)) {
                            val relationship = Relationship()
                            relationship.firstLevelNodeId = selectedId
                            relationships!!.add(relationship)

                            val node = nodeMap!![selectedId]
                            if (null != node) mNodeList!!.add(node)
                        }
                    }

                    mAdapter!!.notifyDataSetChanged()
                    justifyDisplayEmptyView()

                    MainActivity.saveAllDatas()
                }

                else -> {
                }
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

        private val REQ_EDIT_ADD = 0x100
    }
}
