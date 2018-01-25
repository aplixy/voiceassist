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
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditRelationshipActivity extends BaseActivity {

    private static final int REQ_EDIT_ADD = 0x100;

    private Button mBtnAddRelationship;

    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;

    private Intent mAddIntent;

    private ArrayMap<String, Relationship> mRelationshipMap;
    private List<Node> mNodeList;

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
        for (Relationship relationship : relationships) {
            if (null == relationship) continue;

            Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
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

        mAddIntent = new Intent(this, EditOrAddRelationActivity.class);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new NodeListAdapter.OnItemClickListener() {
            @Override
            public void onClick(NodeListAdapter adapter, int position, Node node) {
                mAddIntent.putExtra("position", position);
                mAddIntent.putExtra("relationship", mRelationshipMap.get(node.id));

                startActivityForResult(mAddIntent, REQ_EDIT_ADD);
            }
        });

        mBtnAddRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAddIntent.removeExtra("position");
//                mAddIntent.removeExtra("relationship");
//                startActivityForResult(mAddIntent, REQ_EDIT_ADD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_EDIT_ADD: {

                break;
            }

            default: {
                break;
            }
        }
    }
}
