package com.voiceassist.lixinyu.voiceassist.settings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipLevel2Adapter
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipListAdapter
import com.voiceassist.lixinyu.voiceassist.utils.KGLog
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils

import java.util.ArrayList
import java.util.HashSet

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * Created by lilidan on 2018/1/25.
 */

class EditSecondLevelRelationActivity : BaseActivity(), IEmptyable {

    private var mBtnAdd: ImageView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RelationshipLevel2Adapter? = null
    private var mNodeList: MutableList<Node>? = null

    private var mPosition: Int = 0
    private var mRelationship: Relationship? = null
    private var mFirstLevelNode: Node? = null


    private var mLoadingDialog: LoadingDialog? = null


    private var mIsAllowSort: Boolean = false
    private var mToPosition = -1
    private var mTipDialog: CommonContentDialog? = null

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    //private ViewGroup mRootViewGroup;

    private var mAnimOut: Animation? = null
    private var mAnimIn: Animation? = null
    private var mAlreayOut: Boolean = false

    private var mViewStub: ViewStub? = null
    private val mEmptyView: View? = null

    private val selectedRelationIds: ArrayList<String>
        get() {
            val ids = ArrayList<String>()
            if (null != mRelationship!!.secondLevelNodes) {
                for (secondLevelNode in mRelationship!!.secondLevelNodes) {
                    if (null == secondLevelNode) continue

                    ids.add(secondLevelNode.secondLevelNodeId)
                }
            }
            return ids
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_or_add_relation)

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
                    mAdapter!!.setAllowSortOrder(mIsAllowSort)
                } else {
                    mLoadingDialog!!.show()
                    Observable.create(ObservableOnSubscribe<Any> { e ->
                        sortOrder(mIsAllowSort, mToPosition)
                        mToPosition = -1

                        e.onNext(mToPosition)
                        e.onComplete()
                    })
                            .compose(RxHelper.rxSchedulerNewThreadHelper())
                            .subscribe({
                                mIsAllowSort = false
                                item.title = "排序"
                                mLoadingDialog!!.dismiss()
                                mAdapter!!.setAllowSortOrder(false)
                            }, {
                                mIsAllowSort = false
                                item.title = "排序"
                                mLoadingDialog!!.dismiss()
                                mAdapter!!.setAllowSortOrder(false)
                            })
                }
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mBtnAdd = findViewById(R.id.add_relation_button)
        mRecyclerView = findViewById(R.id.add_relation_recyclerview)
        mSwipeRefreshLayout = findViewById(R.id.add_relation_swiperefreshlayout)
        //mRootViewGroup = findViewById(R.id.add_relation_root_viewgroup);
        mViewStub = findViewById(R.id.add_relation_empty_view_stub)
        //mEmptyView = findViewById(R.id.add_relation_empty_view);

        mLoadingDialog = LoadingDialog(this)
        mSwipeRefreshLayout!!.isEnabled = false
    }

    private fun initData() {
        mPosition = intent.getIntExtra("position", -1)
        if (mPosition != -1) {
            mRelationship = MainActivity.mAllData!!.relationship[mPosition]
        }

        mFirstLevelNode = MainActivity.mNodesMap?.get(mRelationship?.firstLevelNodeId)
        if (null == mRelationship || null == mFirstLevelNode) {
            ToastUtils.showToast("找不到一级结点")
            finish()
            return
        }

        title = mFirstLevelNode!!.cnName + "的子节点"

        mAnimOut = AnimationUtils.loadAnimation(this, R.anim.out_to_bottom)
        mAnimIn = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom)


        mNodeList = ArrayList()
        mAdapter = RelationshipLevel2Adapter(this, mNodeList)

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



        if (null == mRelationship!!.secondLevelNodes || mRelationship!!.secondLevelNodes.size == 0) {
            //mRootViewGroup.setVisibility(View.VISIBLE);
            justifyDisplayEmptyView()
            return
        }


        mSwipeRefreshLayout!!.isRefreshing = true
        Observable.just(mRelationship!!.secondLevelNodes)
                .map { secondLevelNodes ->
                    val nodeList = ArrayList<Node>()
                    for (secondLevelNode in secondLevelNodes) {
                        if (null == secondLevelNode) continue

                        val node = MainActivity.mNodesMap!![secondLevelNode.secondLevelNodeId]
                        if (null != node) nodeList.add(node)
                    }
                    nodeList
                }
                .compose(RxHelper.rxSchedulerNewThreadHelper())
                .subscribe({ nodeList ->
                    if (null != nodeList) {
                        mNodeList!!.addAll(nodeList)
                        mAdapter!!.notifyDataSetChanged()
                        justifyDisplayEmptyView()
                    }

                    mSwipeRefreshLayout!!.isRefreshing = false
                    //mRootViewGroup.setVisibility(View.VISIBLE);
                }, {
                    mSwipeRefreshLayout!!.isRefreshing = false
                    //mRootViewGroup.setVisibility(View.VISIBLE);
                    justifyDisplayEmptyView()
                })

    }

    private fun initListener() {
        mBtnAdd!!.setOnClickListener {
            val intent = Intent(this@EditSecondLevelRelationActivity, NodeSelectionActivity::class.java)
            intent.putStringArrayListExtra("selected_nodes_id", selectedRelationIds)

            if (null != mFirstLevelNode) {
                intent.putExtra("title", "请选择" + mFirstLevelNode!!.cnName + "的子结点")
            }

            startActivityForResult(intent, REQ_ADD_NODE)
        }


        mAdapter!!.setOnItemMoveCompleteListener { fromPosition, toPosition -> mToPosition = toPosition }

        mRecyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) {
                    if (mAlreayOut) {
                        mBtnAdd!!.startAnimation(mAnimIn)
                        mAlreayOut = false
                        mBtnAdd!!.isClickable = true
                    }
                } else {
                    if (!mAlreayOut) {
                        mBtnAdd!!.startAnimation(mAnimOut)
                        mAlreayOut = true
                        mBtnAdd!!.isClickable = false
                    }
                }
            }
        })
    }

    private fun sortOrder(isAllowSort: Boolean, toPosition: Int) {
        if (!isAllowSort) return
        if (toPosition == -1) return


        var secondLevelNodes: MutableList<SecondLevelNode>? = null
        if (null == mRelationship) return

        secondLevelNodes = mRelationship?.secondLevelNodes
        if (null == secondLevelNodes) return

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
        for (secondLevelNode in secondLevelNodes!!) {
            if (null != secondLevelNode && null != secondLevelNode.secondLevelNodeId) {

                if (fromRelationPos == -1 && secondLevelNode.secondLevelNodeId == moveId) {
                    fromRelationPos = i
                }

                if (fromRelationPos != -1 && toRelationPos != -1) {
                    break// 源位置和目标位置都找到的话就要以退出循环了
                } else if (toRelationPos == -1 && null != preId && secondLevelNode.secondLevelNodeId == preId) {
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
        if (toRelationPos == secondLevelNodes.size) {
            secondLevelNodes.add(secondLevelNodes[fromRelationPos])
            secondLevelNodes.removeAt(fromRelationPos)
        } else {
            // 先把原值复制到新位置
            secondLevelNodes.add(toRelationPos, secondLevelNodes[fromRelationPos])

            // 再删除原值
            if (fromRelationPos < toRelationPos) {
                // 从上往下移，复制完之后不影响原位置的索引，直接删除原值
                secondLevelNodes.removeAt(fromRelationPos)
            } else {
                // 从下往上移，复制完之后原来的索引也会被“顶”下一个位置，所以删除原值时索引要加1
                secondLevelNodes.removeAt(fromRelationPos + 1)
            }
        }

        MainActivity.saveAllDatas()

    }


    private fun updateData(selectedNodeList: List<Node>) {
        mLoadingDialog!!.show()

        Observable.just(selectedNodeList)
                .observeOn(Schedulers.newThread())
                .map {
                    val selectedIdSet = HashSet<String>()
                    for (node in selectedNodeList) {
                        if (null == node) continue

                        selectedIdSet.add(node.id)
                    }


                    //KGLog.d("要找的一级节点--->" + mRelationship.firstLevelNodeId);

                    var realRelationship: Relationship? = null
                    for (relationship in MainActivity.mAllData!!.relationship) {
                        if (null == relationship) continue

                        //KGLog.i("内存中的一级节点--->" + relationship.firstLevelNodeId);

                        if (relationship.firstLevelNodeId == mRelationship!!.firstLevelNodeId) {
                            realRelationship = relationship
                            break
                        }
                    }

                    var secondLevelNodeList: MutableList<SecondLevelNode>? = null
                    if (null != realRelationship) {
                        secondLevelNodeList = realRelationship.secondLevelNodes
                        if (selectedNodeList.size == 0) {
                            realRelationship.secondLevelNodes = ArrayList()
                            secondLevelNodeList = realRelationship.secondLevelNodes
                        } else {
                            if (null == secondLevelNodeList) {
                                realRelationship.secondLevelNodes = ArrayList()
                                secondLevelNodeList = realRelationship.secondLevelNodes
                            }

                            //KGLog.v("1. secondLevelNodeList.size--->" + secondLevelNodeList.size());
                            val originalIdSet = HashSet<String>()
                            if (secondLevelNodeList!!.size > 0) {
                                var i = 0
                                var size = secondLevelNodeList.size
                                while (i < size) {
                                    val secondLevelNode = secondLevelNodeList[i]
                                    if (null == secondLevelNode) {
                                        i++
                                        continue
                                    }

                                    if (!selectedIdSet.contains(secondLevelNode.secondLevelNodeId)) {
                                        secondLevelNodeList.removeAt(i)
                                        size--
                                        continue
                                    }
                                    originalIdSet.add(secondLevelNode.secondLevelNodeId)
                                    i++
                                }
                            }

                            for (selectedId in selectedIdSet) {
                                if (!originalIdSet.contains(selectedId)) {
                                    val secondLevelNode = SecondLevelNode()
                                    secondLevelNode.secondLevelNodeId = selectedId
                                    secondLevelNodeList.add(secondLevelNode)
                                }
                            }
                        }
                    }


                    val realList = ArrayList<Node>()
                    if (null != secondLevelNodeList) {
                        for (secondLevelNode in secondLevelNodeList) {
                            if (null == secondLevelNode) continue
                            val node = MainActivity.mNodesMap!![secondLevelNode.secondLevelNodeId] ?: continue
                            realList.add(node)
                        }
                    }


                    realList
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ selectedList ->
                    mLoadingDialog!!.dismiss()
                    MainActivity.saveAllDatas()

                    mNodeList!!.clear()
                    mNodeList!!.addAll(selectedList)
                    mAdapter!!.notifyDataSetChanged()
                    justifyDisplayEmptyView()
                }, {
                    mLoadingDialog!!.dismiss()
                    ToastUtils.showToast("编辑失败")
                })
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
                            super@EditSecondLevelRelationActivity.finish()
                        }
                        .build()
            }

            mTipDialog!!.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQ_ADD_NODE -> {
                    var selectedNodeList: List<Node>? = data.getSerializableExtra("selected_node_list") as List<Node>
                    if (null == selectedNodeList) selectedNodeList = ArrayList()

                    updateData(selectedNodeList)
                }

                else -> {
                }
            }
        }
    }

    override fun justifyDisplayEmptyView() {
        if (null == mNodeList || mNodeList!!.size == 0) {
            mViewStub!!.visibility = View.VISIBLE
        } else {
            mViewStub!!.visibility = View.GONE
        }
    }

    companion object {

        private val REQ_ADD_NODE = 0x100
    }
}
