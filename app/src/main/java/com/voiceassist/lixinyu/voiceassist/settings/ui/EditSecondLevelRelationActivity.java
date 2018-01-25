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
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditSecondLevelRelationActivity extends BaseActivity {

    private static final int REQ_ADD_NODE = 0x100;

    private Button mBtnAdd;
    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;
    private List<Node> mNodeList;

    private int mPosition;
    private Relationship mRelationship;
    private Node mFirstLevelNode;

    private ArrayList<String> mSecondLevelIdList;

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
        mAdapter = new NodeListAdapter(this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mSecondLevelIdList = new ArrayList<>();


        if (null == mRelationship.secondLevelNodes || mRelationship.secondLevelNodes.size() == 0) return;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode)
        switch (requestCode) {
            case REQ_ADD_NODE: {
                List<Node> selectedNodeList = (List<Node>) data.getSerializableExtra("selected_node_list");
                mNodeList.clear();
                mNodeList.addAll(selectedNodeList);
                mAdapter.notifyDataSetChanged();

                List<SecondLevelNode> secondLevelNodes = new ArrayList<>();
                for (Node node : selectedNodeList) {
                    if (null == node) continue;

                    SecondLevelNode secondLevelNode = new SecondLevelNode();
                    secondLevelNode.secondLevelNodeId = node.id;

                    secondLevelNodes.add(secondLevelNode);
                }

                //KGLog.d("要找的一级节点--->" + mRelationship.firstLevelNodeId);

                for (Relationship relationship : MainActivity.mAllData.relationship) {
                    if (null == relationship) continue;

                    //KGLog.i("内存中的一级节点--->" + relationship.firstLevelNodeId);

                    if (relationship.firstLevelNodeId.equals(mRelationship.firstLevelNodeId)) {
                        relationship.secondLevelNodes = secondLevelNodes;
                        //KGLog.d("relationship.secondLevelNodes--->" + relationship.secondLevelNodes.size());
                        break;
                    }
                }

                MainActivity.saveAllDatas();


                break;
            }

            default: {
                break;
            }
        }
    }
}
