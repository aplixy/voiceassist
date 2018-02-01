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
import com.voiceassist.lixinyu.voiceassist.common.Constants;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.entity.dto.SecondLevelNode;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditNodeActivity extends BaseActivity {

    private static final int REQ_ADD_EDIT = 0x100;

    private Button mBtnAddNode;

    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;

    private List<Node> mNodeList;

    private CommonContentDialog mDeleteDialog;


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
                Intent intent = new Intent(EditNodeActivity.this, NodeAddEditActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("node", node);
                startActivityForResult(intent, REQ_ADD_EDIT);
            }

            @Override
            public void onLongClick(NodeListAdapter adapter, final int position, final Node node) {
                if (null == mDeleteDialog) {
                    mDeleteDialog = new CommonContentDialog.Builder(EditNodeActivity.this)
                            .contentText("确认删除" + node.cnName + "节点吗？")
                            .onYesClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Object objPos = v.getTag(R.id.node_delete_position_tag);
                                    Object objNode = v.getTag(R.id.node_delete_entity_tag);

                                    int pos = -1;
                                    if (null != objPos && objPos instanceof Integer) {
                                        pos = (int) objPos;
                                    } else {
                                        pos = position;
                                    }

                                    Node realNode = null;
                                    if (null != objNode && objNode instanceof Node) {
                                        realNode = (Node) objNode;
                                    } else {
                                        realNode = node;
                                    }

                                    mDeleteDialog.dismiss();

                                    // start delete

                                    mNodeList.remove(pos);
                                    mAdapter.notifyDataSetChanged();



                                    if (null != realNode) {
                                        deleteRelationship(realNode);
                                        deleteVoiceFile(realNode.audioPath);
                                    }


                                    MainActivity.saveAllDatas();
                                }
                            })
                            .onNoClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDeleteDialog.dismiss();
                                }
                            })
                            .build();
                } else {
                    mDeleteDialog.getContentTextView().setText("确认删除" + node.cnName + "节点吗？");
                }

                Button yesButton = mDeleteDialog.getYesButton();
                if (null != yesButton) {
                    yesButton.setTag(R.id.node_delete_position_tag, position);
                    yesButton.setTag(R.id.node_delete_entity_tag, node);
                }
                mDeleteDialog.show();
            }
        });

        mBtnAddNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditNodeActivity.this, NodeAddEditActivity.class), REQ_ADD_EDIT);
            }
        });
    }

    private void deleteRelationship(Node node) {
        if (null == node) return;

        String deleteId = node.id;
        MainActivity.mNodesMap.remove(deleteId);

        // 删除关系列表中相关内容
        List<Relationship> relationshipList = MainActivity.mAllData.relationship;
        if (null != relationshipList) {
            for (int i = 0, level1Size = relationshipList.size(); i < level1Size;) {
                Relationship relationship = relationshipList.get(i);
                if (null == relationship) {
                    i++;
                    continue;
                }
                if (relationship.firstLevelNodeId.equals(deleteId)) {
                    relationshipList.remove(i);
                    level1Size--;
                    continue;
                } else {
                    List<SecondLevelNode> secondLevelNodeList = relationship.secondLevelNodes;
                    if (null != secondLevelNodeList) {
                        for (int j = 0, level2Size = secondLevelNodeList.size(); j < level2Size;) {
                            SecondLevelNode secondLevelNode = secondLevelNodeList.get(j);
                            if (null == secondLevelNode) {
                                j++;
                                continue;
                            }
                            if (secondLevelNode.secondLevelNodeId.equals(deleteId)) {
                                secondLevelNodeList.remove(j);
                                level2Size--;
                                continue;
                            }
                            j++;
                        }
                    }
                }
                i++;
            }
        }
    }

    private void deleteVoiceFile(String filePath) {
        if (null == filePath || filePath.length() == 0) return;

        if (!filePath.contains(Constants.ROOT_PATH)) {
            filePath = Constants.ROOT_PATH + filePath;
        }

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
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
