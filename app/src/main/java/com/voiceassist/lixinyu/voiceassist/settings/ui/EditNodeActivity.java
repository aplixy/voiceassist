package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

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
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.io.File;
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditNodeActivity extends BaseActivity implements IEmptyable {

    private static final int REQ_ADD_EDIT = 0x100;

    private ImageView mBtnAddNode;

    private RecyclerView mRecyclerView;
    private NodeListAdapter mAdapter;

    private List<Node> mNodeList;

    private CommonContentDialog mDeleteDialog;

    private Animation mAnimOut;
    private Animation mAnimIn;
    private boolean mAlreayOut;

    private ViewStub mEmptyViewStub;


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
        mEmptyViewStub = findViewById(R.id.edit_node_empty_view_stub);
    }

    private void initData() {
        setTitle("添加或编辑节点");

        mNodeList = MainActivity.Companion.getMAllData().nodes;

        mAdapter = new NodeListAdapter(EditNodeActivity.this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(EditNodeActivity.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(EditNodeActivity.this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        justifyDisplayEmptyView();

        mAnimOut = AnimationUtils.loadAnimation(this, R.anim.out_to_bottom);
        mAnimIn = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom);

        ToastUtils.showToast("长按可删除");
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
                                    justifyDisplayEmptyView();

                                    if (null != realNode) {
                                        deleteRelationship(realNode);
                                        deleteVoiceFile(realNode.audioPath);
                                    }

                                    MainActivity.Companion.saveAllDatas();
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
                    mDeleteDialog.getContentTextView().setText("确认删除“" + node.cnName + "”节点吗？");
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


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    if (mAlreayOut) {
                        mBtnAddNode.startAnimation(mAnimIn);
                        mAlreayOut = false;
                        mBtnAddNode.setClickable(true);
                    }
                } else {
                    if (!mAlreayOut) {
                        mBtnAddNode.startAnimation(mAnimOut);
                        mAlreayOut = true;
                        mBtnAddNode.setClickable(false);
                    }
                }
            }
        });


    }

    private void deleteRelationship(Node node) {
        if (null == node) return;

        String deleteId = node.id;
        MainActivity.Companion.getMNodesMap().remove(deleteId);

        // 删除关系列表中相关内容
        List<Relationship> relationshipList = MainActivity.Companion.getMAllData().relationship;
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

                MainActivity.Companion.getMNodesMap().put(node.id, node);
                mAdapter.notifyDataSetChanged();
                justifyDisplayEmptyView();
                MainActivity.Companion.saveAllDatas();
                break;
            }

            default: {
                break;
            }
        }
    }

    @Override
    public void justifyDisplayEmptyView() {
        if (null == mNodeList || mNodeList.size() == 0) {
            mEmptyViewStub.setVisibility(View.VISIBLE);
        } else {
            mEmptyViewStub.setVisibility(View.GONE);
        }
    }
}
