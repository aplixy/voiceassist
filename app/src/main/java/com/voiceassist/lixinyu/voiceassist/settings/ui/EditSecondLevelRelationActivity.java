package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.widget.LoadingDialog;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipLevel2Adapter;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
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

    private ArrayList<String> mSecondLevelIdList;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_or_add_relation);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBtnAdd = findViewById(R.id.add_relation_button);
        mRecyclerView = findViewById(R.id.add_relation_recyclerview);

        mLoadingDialog = new LoadingDialog(this);
    }

    private void initData() {
        mPosition = getIntent().getIntExtra("position", -1);
        mRelationship = (Relationship) getIntent().getSerializableExtra("relationship");
        if (null == mRelationship) {
            return;
        }

        mFirstLevelNode = MainActivity.mNodesMap.get(mRelationship.firstLevelNodeId);

        if (null == mFirstLevelNode) return;

        setTitle(mFirstLevelNode.cnName + "的子节点");


        mNodeList = new ArrayList<>();
        mAdapter = new RelationshipLevel2Adapter(this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mSecondLevelIdList = new ArrayList<>();


        if (null == mRelationship.secondLevelNodes || mRelationship.secondLevelNodes.size() == 0)
            return;


        for (SecondLevelNode secondLevelNode : mRelationship.secondLevelNodes) {
            if (null == secondLevelNode) continue;

            mSecondLevelIdList.add(secondLevelNode.secondLevelNodeId);

            Node node = MainActivity.mNodesMap.get(secondLevelNode.secondLevelNodeId);
            if (null != node) mNodeList.add(node);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSecondLevelRelationActivity.this, NodeSelectionActivity.class);
                if (null != mSecondLevelIdList) {
                    intent.putStringArrayListExtra("selected_nodes_id", mSecondLevelIdList);
                }

                if (null != mFirstLevelNode) {
                    intent.putExtra("title", "请选择" + mFirstLevelNode.cnName + "的子结点");
                }

                startActivityForResult(intent, REQ_ADD_NODE);
            }
        });
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
                                realRelationship.secondLevelNodes = new ArrayList<>();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQ_ADD_NODE: {
                    List<Node> selectedNodeList = (List<Node>) data.getSerializableExtra("selected_node_list");
                    if (null == selectedNodeList) selectedNodeList = new ArrayList<>();
//                    mNodeList.clear();
//                    mNodeList.addAll(updateData(selectedNodeList));
//                    mAdapter.notifyDataSetChanged();

                    updateData(selectedNodeList);
//                    HashSet<String> selectedIdSet = new HashSet<>();
//                    for (Node node : selectedNodeList) {
//                        if (null == node) continue;
//
//                        selectedIdSet.add(node.id);
//                    }
//
//                    //KGLog.d("要找的一级节点--->" + mRelationship.firstLevelNodeId);
//
//                    Relationship realRelationship = null;
//                    for (Relationship relationship : MainActivity.mAllData.relationship) {
//                        if (null == relationship) continue;
//
//                        //KGLog.i("内存中的一级节点--->" + relationship.firstLevelNodeId);
//
//                        if (relationship.firstLevelNodeId.equals(mRelationship.firstLevelNodeId)) {
//                            //relationship.secondLevelNodes = secondLevelNodes;
//                            //KGLog.d("relationship.secondLevelNodes--->" + relationship.secondLevelNodes.size());
//
//                            realRelationship = relationship;
//                            break;
//                        }
//                    }
//
//                    if (null != realRelationship) {
//                        List<SecondLevelNode> secondLevelNodeList = realRelationship.secondLevelNodes;
//                        if (selectedNodeList.size() == 0) {
//                            realRelationship.secondLevelNodes = new ArrayList<>();
//                        } else {
//                            if (null == secondLevelNodeList) {
//                                secondLevelNodeList = realRelationship.secondLevelNodes = new ArrayList<>();
//                            }
//                            HashSet<String> originalIdSet = new HashSet<>();
//                            if (secondLevelNodeList.size() > 0) {
//                                for (int i = 0, size = secondLevelNodeList.size(); i < size; ) {
//                                    SecondLevelNode secondLevelNode = secondLevelNodeList.get(i);
//                                    if (null == secondLevelNode) {
//                                        i++;
//                                        continue;
//                                    }
//
//                                    if (!selectedIdSet.contains(secondLevelNode.secondLevelNodeId)) {
//                                        secondLevelNodeList.remove(i);
//                                        size--;
//                                        continue;
//                                    }
//                                    originalIdSet.add(secondLevelNode.secondLevelNodeId);
//                                }
//                            }
//
//                            for (String selectedId : selectedIdSet) {
//                                if (!originalIdSet.contains(selectedId)) {
//                                    SecondLevelNode secondLevelNode = new SecondLevelNode();
//                                    secondLevelNode.secondLevelNodeId = selectedId;
//                                    secondLevelNodeList.add(secondLevelNode);
//                                }
//                            }
//                        }
//                    }
//
//                    MainActivity.saveAllDatas();


                    break;
                }

                default: {
                    break;
                }
            }
        }
    }
}
