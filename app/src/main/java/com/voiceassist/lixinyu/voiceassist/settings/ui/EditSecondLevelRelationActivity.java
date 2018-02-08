package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.rx.RxHelper;
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog;
import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipLevel2Adapter;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipListAdapter;
import com.voiceassist.lixinyu.voiceassist.utils.KGLog;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditSecondLevelRelationActivity extends BaseActivity {

    private static final int REQ_ADD_NODE = 0x100;

    private Button mBtnAdd;
    private RecyclerView mRecyclerView;
    private RelationshipLevel2Adapter mAdapter;
    private List<Node> mNodeList;

    private int mPosition;
    private Relationship mRelationship;
    private Node mFirstLevelNode;


    private LoadingDialog mLoadingDialog;


    private boolean mIsAllowSort;
    private int mToPosition = -1;
    private CommonContentDialog mTipDialog;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mRootViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_or_add_relation);

        initView();
        initData();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_order, menu);//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_order: {
                if (!mIsAllowSort) {
                    item.setTitle("保存排序");
                    mIsAllowSort = true;
                    mAdapter.setAllowSortOrder(mIsAllowSort);
                } else {
                    mLoadingDialog.show();
                    Observable.create(new ObservableOnSubscribe<Object>() {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception {
                            sortOrder(mIsAllowSort, mToPosition);
                            mToPosition = -1;

                            e.onNext(mToPosition);
                            e.onComplete();
                        }
                    })
                    .compose(RxHelper.<Object>rxSchedulerNewThreadHelper())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            mIsAllowSort = false;
                            item.setTitle("排序");
                            mLoadingDialog.dismiss();
                            mAdapter.setAllowSortOrder(false);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mIsAllowSort = false;
                            item.setTitle("排序");
                            mLoadingDialog.dismiss();
                            mAdapter.setAllowSortOrder(false);
                        }
                    });
                }

                break;
            }

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mBtnAdd = findViewById(R.id.add_relation_button);
        mRecyclerView = findViewById(R.id.add_relation_recyclerview);
        mSwipeRefreshLayout = findViewById(R.id.add_relation_swiperefreshlayout);
        mRootViewGroup = findViewById(R.id.add_relation_root_viewgroup);

        mLoadingDialog = new LoadingDialog(this);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void initData() {
        mPosition = getIntent().getIntExtra("position", -1);
        if (mPosition != -1) {
            mRelationship = MainActivity.mAllData.relationship.get(mPosition);
        }

        if (null == mRelationship || null == (mFirstLevelNode = MainActivity.mNodesMap.get(mRelationship.firstLevelNodeId))) {
            ToastUtils.showToast("找不到一级结点");
            finish();
            return;
        }

        setTitle(mFirstLevelNode.cnName + "的子节点");


        mNodeList = new ArrayList<>();
        mAdapter = new RelationshipLevel2Adapter(this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        //创建SimpleItemTouchHelperCallback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        mAdapter.setItemTouchHelper(touchHelper);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(mRecyclerView);



        if (null == mRelationship.secondLevelNodes || mRelationship.secondLevelNodes.size() == 0) {
            mRootViewGroup.setVisibility(View.VISIBLE);
            return;
        }


        mSwipeRefreshLayout.setRefreshing(true);
        Observable.just(mRelationship.secondLevelNodes)
                .map(new Function<List<SecondLevelNode>, List<Node>>() {
                    @Override
                    public List<Node> apply(List<SecondLevelNode> secondLevelNodes) throws Exception {
                        List<Node> nodeList = new ArrayList<>();
                        for (SecondLevelNode secondLevelNode : secondLevelNodes) {
                            if (null == secondLevelNode) continue;

                            Node node = MainActivity.mNodesMap.get(secondLevelNode.secondLevelNodeId);
                            if (null != node) nodeList.add(node);
                        }
                        return nodeList;
                    }
                })
                .compose(RxHelper.<List<Node>>rxSchedulerNewThreadHelper())
                .subscribe(new Consumer<List<Node>>() {
                    @Override
                    public void accept(List<Node> nodeList) throws Exception {
                        if (null != nodeList) {
                            mNodeList.addAll(nodeList);
                            mAdapter.notifyDataSetChanged();
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        mRootViewGroup.setVisibility(View.VISIBLE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRootViewGroup.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void initListener() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSecondLevelRelationActivity.this, NodeSelectionActivity.class);
                intent.putStringArrayListExtra("selected_nodes_id", getSelectedRelationIds());

                if (null != mFirstLevelNode) {
                    intent.putExtra("title", "请选择" + mFirstLevelNode.cnName + "的子结点");
                }

                startActivityForResult(intent, REQ_ADD_NODE);
            }
        });


        mAdapter.setOnItemMoveCompleteListener(new RelationshipListAdapter.OnItemMoveCompleteListener() {
            @Override
            public void onComplete(int fromPosition, int toPosition) {
                mToPosition = toPosition;
            }
        });
    }

    private ArrayList<String> getSelectedRelationIds() {
        ArrayList<String> ids = new ArrayList<>();
        if (null != mRelationship.secondLevelNodes) {
            for (SecondLevelNode secondLevelNode : mRelationship.secondLevelNodes) {
                if (null == secondLevelNode) continue;

                ids.add(secondLevelNode.secondLevelNodeId);
            }
        }
        return ids;
    }

    private void sortOrder(boolean isAllowSort, final int toPosition) {
        if (!isAllowSort) return;
        if (toPosition == -1) return;


        List<SecondLevelNode> secondLevelNodes = null;
        if (null == mRelationship) return;
        if (null == (secondLevelNodes = mRelationship.secondLevelNodes)) return;

        // 准备移动的前提条件（prerequisites）
        Node moveNode = mNodeList.get(toPosition);
        if (null == moveNode || null == moveNode.id) return;
        String moveId = moveNode.id;

        Node preNode = toPosition > 0 ? mNodeList.get(toPosition - 1) : null;
        String preId = null != preNode ? preNode.id : null;


        // 查找移动的开始及终止位置
        int fromRelationPos = -1;
        int toRelationPos = null != preId ? -1 : 0;// 如果没有找到前一项的id，说明目标位置就在列表最开始处
        int preRelationPos = -1;

        int i = 0;
        for (SecondLevelNode secondLevelNode : secondLevelNodes) {
            if (null != secondLevelNode && null != secondLevelNode.secondLevelNodeId) {

                if (fromRelationPos == -1 && secondLevelNode.secondLevelNodeId.equals(moveId)) {
                    fromRelationPos = i;
                }

                if (fromRelationPos != -1 && toRelationPos != -1) {
                    break;// 源位置和目标位置都找到的话就要以退出循环了
                } else if (toRelationPos == -1 && null != preId && secondLevelNode.secondLevelNodeId.equals(preId)) {
                    preRelationPos = i;
                }
            }
            i++;
        }

        // 确定最终的开始及结束位置
        if (fromRelationPos == -1) return;

        if (toRelationPos == -1) {
            if (preRelationPos == -1) {
                // 一直没有找到被移动的结点在移动之后的前一项应该是谁，说明在移动之后该结点没有前一项，那就是在列表最开始处了
                toRelationPos = 0;
            } else {
                // 确定了移动完成之后前一项应该的位置，那么被移动的结点在移动之后就应该位于该结点的下一项，哪怕在列表尾也先加1，后续再处理
                toRelationPos = preRelationPos + 1;
            }
        }


        // 开始移动
        if (toRelationPos == secondLevelNodes.size()) {
            secondLevelNodes.add(secondLevelNodes.get(fromRelationPos));
            secondLevelNodes.remove(fromRelationPos);
        } else {
            // 先把原值复制到新位置
            secondLevelNodes.add(toRelationPos, secondLevelNodes.get(fromRelationPos));

            // 再删除原值
            if (fromRelationPos < toRelationPos) {
                // 从上往下移，复制完之后不影响原位置的索引，直接删除原值
                secondLevelNodes.remove(fromRelationPos);
            } else {
                // 从下往上移，复制完之后原来的索引也会被“顶”下一个位置，所以删除原值时索引要加1
                secondLevelNodes.remove(fromRelationPos + 1);
            }
        }

        MainActivity.saveAllDatas();

    }


    private void updateData(final List<Node> selectedNodeList) {
        mLoadingDialog.show();

        Observable.just(selectedNodeList)
                .observeOn(Schedulers.newThread())
                .map(new Function<List<Node>, List<Node>>() {
                    @Override
                    public List<Node> apply(List<Node> nodes) throws Exception {
                        HashSet<String> selectedIdSet = new HashSet<>();
                        for (Node node : selectedNodeList) {
                            if (null == node) continue;

                            selectedIdSet.add(node.id);
                        }


                        //KGLog.d("要找的一级节点--->" + mRelationship.firstLevelNodeId);

                        Relationship realRelationship = null;
                        for (Relationship relationship : MainActivity.mAllData.relationship) {
                            if (null == relationship) continue;

                            //KGLog.i("内存中的一级节点--->" + relationship.firstLevelNodeId);

                            if (relationship.firstLevelNodeId.equals(mRelationship.firstLevelNodeId)) {
                                realRelationship = relationship;
                                break;
                            }
                        }

                        List<SecondLevelNode> secondLevelNodeList = null;
                        if (null != realRelationship) {
                            secondLevelNodeList = realRelationship.secondLevelNodes;
                            if (selectedNodeList.size() == 0) {
                                secondLevelNodeList = realRelationship.secondLevelNodes = new ArrayList<>();
                            } else {
                                if (null == secondLevelNodeList) {
                                    secondLevelNodeList = realRelationship.secondLevelNodes = new ArrayList<>();
                                }

                                //KGLog.v("1. secondLevelNodeList.size--->" + secondLevelNodeList.size());
                                HashSet<String> originalIdSet = new HashSet<>();
                                if (secondLevelNodeList.size() > 0) {
                                    for (int i = 0, size = secondLevelNodeList.size(); i < size; ) {
                                        SecondLevelNode secondLevelNode = secondLevelNodeList.get(i);
                                        if (null == secondLevelNode) {
                                            i++;
                                            continue;
                                        }

                                        if (!selectedIdSet.contains(secondLevelNode.secondLevelNodeId)) {
                                            secondLevelNodeList.remove(i);
                                            size--;
                                            continue;
                                        }
                                        originalIdSet.add(secondLevelNode.secondLevelNodeId);
                                        i++;
                                    }
                                }

                                for (String selectedId : selectedIdSet) {
                                    if (!originalIdSet.contains(selectedId)) {
                                        SecondLevelNode secondLevelNode = new SecondLevelNode();
                                        secondLevelNode.secondLevelNodeId = selectedId;
                                        secondLevelNodeList.add(secondLevelNode);
                                    }
                                }
                            }
                        }


                        List<Node> realList = new ArrayList<>();
                        if (null != secondLevelNodeList) {
                            for (SecondLevelNode secondLevelNode :
                                    secondLevelNodeList) {
                                if (null == secondLevelNode) continue;
                                Node node = MainActivity.mNodesMap.get(secondLevelNode.secondLevelNodeId);
                                if (null == node) continue;
                                realList.add(node);
                            }
                        }


                        return realList;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Node>>() {
                    @Override
                    public void accept(List<Node> selectedList) throws Exception {
                        mLoadingDialog.dismiss();
                        MainActivity.saveAllDatas();

                        mNodeList.clear();
                        mNodeList.addAll(selectedList);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        ToastUtils.showToast("编辑失败");
                    }
                });
    }


    @Override
    public void finish() {
        if (!mIsAllowSort) {
            super.finish();
        } else {
            if (null == mTipDialog) {
                mTipDialog = new CommonContentDialog.Builder(this)
                        .contentText("排序未保存，确定退出吗？")
                        .noBtnText("取消")
                        .yesBtnText("确定退出")
                        .onNoClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTipDialog.dismiss();
                            }
                        })
                        .onYesClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTipDialog.dismiss();
                                EditSecondLevelRelationActivity.super.finish();
                            }
                        })
                        .build();
            }

            mTipDialog.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQ_ADD_NODE: {
                    List<Node> selectedNodeList = (List<Node>) data.getSerializableExtra("selected_node_list");
                    if (null == selectedNodeList) selectedNodeList = new ArrayList<>();

                    updateData(selectedNodeList);


                    break;
                }

                default: {
                    break;
                }
            }
        }
    }
}
