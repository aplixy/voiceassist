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
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter;
import com.voiceassist.lixinyu.voiceassist.settings.dialog.EditNodeDialog;

import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditNodeActivity extends BaseActivity implements EditNodeDialog.OnPositiveButtonClickListener {

    private static final int REQ_ADD_EDIT = 0x100;

    private Button mBtnAddNode;

    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;

    private List<Node> mNodeList;

    private EditNodeDialog mEditDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_node);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBtnAddNode = findViewById(R.id.edit_node_add_button);
        mRecyclerView = findViewById(R.id.edit_node_recyclerview);
    }

    private void initData() {
        setTitle("添加或编辑节点");

        mNodeList = MainActivity.mAllData.nodes;

        mAdapter = new NodeListAdapter(EditNodeActivity.this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(EditNodeActivity.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(EditNodeActivity.this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new NodeListAdapter.OnItemClickListener() {
            @Override
            public void onClick(NodeListAdapter adapter, int position, Node node) {
                //ToastUtils.showToast(position + "," + node.cnName);

//                if (null == mEditDialog) {
//                    mEditDialog = new EditNodeDialog(EditNodeActivity.this);
//                    mEditDialog.setOnPositiveButtonClickListener(EditNodeActivity.this);
//                }
//                mEditDialog.setData(position, node);
//                mEditDialog.show();

                Intent intent = new Intent(EditNodeActivity.this, NodeAddEditActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("node", node);
                startActivityForResult(intent, REQ_ADD_EDIT);
            }
        });

        mBtnAddNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null == mEditDialog) {
//                    mEditDialog = new EditNodeDialog(EditNodeActivity.this);
//                    mEditDialog.setOnPositiveButtonClickListener(EditNodeActivity.this);
//                }
//
//                mEditDialog.setData(-1, null);
//                mEditDialog.show();

                startActivityForResult(new Intent(EditNodeActivity.this, NodeAddEditActivity.class), REQ_ADD_EDIT);
            }
        });
    }

    @Override
    public void onDialogPositiveClick(int position, Node node) {
        if (position != -1) {
            mNodeList.set(position, node);
        } else {
            mNodeList.add(node);
        }

        MainActivity.mNodesMap.put(node.id, node);



        mAdapter.notifyDataSetChanged();

        MainActivity.saveAllDatas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) return;

        switch (requestCode) {
            case REQ_ADD_EDIT: {
                int position = data.getIntExtra("position", -1);
                Node node = (Node) data.getSerializableExtra("node");

                if (position != -1) {
                    mNodeList.set(position, node);
                } else {
                    mNodeList.add(0, node);
                }

                MainActivity.mNodesMap.put(node.id, node);



                mAdapter.notifyDataSetChanged();

                MainActivity.saveAllDatas();
                break;
            }

            default: {
                break;
            }
        }
    }
}
