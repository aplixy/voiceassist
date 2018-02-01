package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditRelationshipActivity extends BaseActivity {

    private static final int REQ_EDIT_ADD = 0x100;

    private Button mBtnAddRelationship;

    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;

    private Intent mItemClickIntent;

    private ArrayMap<String, Relationship> mRelationshipMap;
    private ArrayList<Node> mNodeList;

    private ArrayList<String> mRelationIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_relationship);

        initView();
        initData();
        initListener();


    }

    private void initView() {
        mBtnAddRelationship = findViewById(R.id.edit_relationship_add_button);
        mRecyclerView = findViewById(R.id.edit_relationship_recyclerview);
    }

    private void initData() {
        setTitle("一级节点列表");

        List<Relationship> relationships = MainActivity.mAllData.relationship;
        if (null == relationships) return;

        mNodeList = new ArrayList<>();
        mRelationshipMap = new ArrayMap<>();
        mRelationIds = new ArrayList<>();
        for (Relationship relationship : relationships) {
            if (null == relationship) continue;

            Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
            mRelationIds.add(relationship.firstLevelNodeId);
            if (null != node) {
                mNodeList.add(node);
                mRelationshipMap.put(node.id, relationship);
            }
        }

        mAdapter = new NodeListAdapter(this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mItemClickIntent = new Intent(this, EditSecondLevelRelationActivity.class);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new NodeListAdapter.OnItemClickListener() {
            @Override
            public void onClick(NodeListAdapter adapter, int position, Node node) {
                mItemClickIntent.putExtra("position", position);
                mItemClickIntent.putExtra("relationship", mRelationshipMap.get(node.id));

                startActivity(mItemClickIntent);
            }

            @Override
            public void onLongClick(NodeListAdapter adapter, int position, Node node) {

            }
        });

        mBtnAddRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRelationshipActivity.this, NodeSelectionActivity.class);
                intent.putStringArrayListExtra("selected_nodes_id", mRelationIds);
                intent.putExtra("title", "请选择一级结点");

                startActivityForResult(intent, REQ_EDIT_ADD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQ_EDIT_ADD: {
                    List<Node> selectedNodeList = (List<Node>) data.getSerializableExtra("selected_node_list");

                    mNodeList.clear();
                    mNodeList.addAll(selectedNodeList);
                    mAdapter.notifyDataSetChanged();

                    HashSet<String> selectedIds = new HashSet<>();
                    for (Node node : selectedNodeList) {
                        if (null == node) continue;

                        selectedIds.add(node.id);
                    }

                    HashSet<String> originalIds = new HashSet<>();
                    List<Relationship> allDataRelations = MainActivity.mAllData.relationship;
                    int size = allDataRelations.size();
                    for (int i = 0; i < size;) {
                        Relationship relationship = allDataRelations.get(i);
                        if (null != relationship) {
                            if (!selectedIds.contains(relationship.firstLevelNodeId)) {
                                allDataRelations.remove(i);
                                size--;
                                continue;
                            }

                            originalIds.add(relationship.firstLevelNodeId);
                        }

                        i++;
                    }

                    for (String selectedId :
                            selectedIds) {
                        if (!originalIds.contains(selectedId)) {
                            Relationship relationship = new Relationship();
                            relationship.firstLevelNodeId = selectedId;
                            allDataRelations.add(relationship);
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
}
