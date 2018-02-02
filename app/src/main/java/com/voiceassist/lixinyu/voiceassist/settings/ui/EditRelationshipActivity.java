package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.common.widget.recyclerview.SimpleItemTouchHelperCallback;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Relationship;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeListAdapter;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipListAdapter;

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
    private RelationshipListAdapter mAdapter;

    private Intent mItemClickIntent;

    //private ArrayMap<String, Relationship> mRelationshipMap;
    private ArrayList<Node> mNodeList;

    //private ArrayList<String> mRelationIds;

    private List<Relationship> relationships;

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

        relationships = MainActivity.mAllData.relationship;
        if (null == relationships) return;

        mNodeList = new ArrayList<>();
        //mRelationshipMap = new ArrayMap<>();
        //mRelationIds = new ArrayList<>();
        for (Relationship relationship : relationships) {
            if (null == relationship) continue;

            Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
            //mRelationIds.add(relationship.firstLevelNodeId);
            if (null != node) {
                mNodeList.add(node);
                //mRelationshipMap.put(node.id, relationship);
            }
        }

        mAdapter = new RelationshipListAdapter(this, mNodeList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        //创建SimpleItemTouchHelperCallback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(mRecyclerView);


        mItemClickIntent = new Intent(this, EditSecondLevelRelationActivity.class);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new NodeListAdapter.OnItemClickListener() {
            @Override
            public void onClick(NodeListAdapter adapter, int position, Node node) {
                Relationship selectedRelation = null;
                if (null != relationships) {
                    for (Relationship relationship: relationships) {
                        if (null != relationship && null != node && node.id.equals(relationship.firstLevelNodeId)) {
                            selectedRelation = relationship;
                            break;
                        }
                    }
                }

                mItemClickIntent.putExtra("position", position);
                mItemClickIntent.putExtra("relationship", selectedRelation);

                startActivity(mItemClickIntent);
            }

            @Override
            public void onLongClick(NodeListAdapter adapter, int position, Node node) {

            }
        });

        mAdapter.setOnItemMoveCompleteListener(new RelationshipListAdapter.OnItemMoveCompleteListener() {
            @Override
            public void onComplete(int fromPosition, int toPosition) {
                //KGLog.w("fromPosition--->" + fromPosition);
                //KGLog.v("toPosition--->" + toPosition);

                // 最终目的是要移动relationship列表中的项
                if (null == relationships) return;

                // 先确定要移动的结点的id，以便后续在relationship列表中搜索该结点
                Node moveNode = mNodeList.get(toPosition);
                if (null == moveNode || null == moveNode.id) return;
                String moveId = moveNode.id;

                // 找到当前显示的列表中被移动项的前一项的id，将来在relationship列表中把要移动的项也移动到该项的后面就可以了
                Node preNode = toPosition > 0 ? mNodeList.get(toPosition - 1) : null;
                String preId = null != preNode ? preNode.id : null;

                int fromRelationPos = -1;
                int toRelationPos = null != preId ? -1 : 0;// 如果没有找到前一项的id，说明目标位置就在列表最开始处
                int preRelationPos = -1;

                int i = 0;
                for (Relationship relationship : relationships) {
                    if (null != relationship && null != relationship.firstLevelNodeId) {

                        // 查找要移动的项目前所处的索引位置
                        if (fromRelationPos == -1 && relationship.firstLevelNodeId.equals(moveId)) {
                            fromRelationPos = i;
                        }

                        if (fromRelationPos != -1 && toRelationPos != -1) {
                            break;// 源位置和目标位置都找到的话就要以退出循环了
                        } else if (toRelationPos == -1 && null != preId && relationship.firstLevelNodeId.equals(preId)) {
                            preRelationPos = i;
                        }
                    }
                    i++;
                }

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

                //KGLog.d("fromRelationPosition--->" + fromRelationPos);
                //KGLog.i("toRelationPosition--->" + toRelationPos);

                if (toRelationPos == relationships.size()) {
                    // 如果在列表尾
                    relationships.add(relationships.get(fromRelationPos));
                    relationships.remove(fromRelationPos);
                } else {
                    // 先把原值复制到新位置
                    relationships.add(toRelationPos, relationships.get(fromRelationPos));

                    // 再删除原值
                    if (fromRelationPos < toRelationPos) {
                        // 从上往下移，复制完之后不影响原位置的索引，直接删除原值
                        relationships.remove(fromRelationPos);
                    } else {
                        // 从下往上移，复制完之后原来的索引也会被“顶”下一个位置，所以删除原值时索引要加1
                        relationships.remove(fromRelationPos + 1);
                    }
                }

                MainActivity.saveAllDatas();


            }
        });

        mBtnAddRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRelationshipActivity.this, NodeSelectionActivity.class);
                intent.putStringArrayListExtra("selected_nodes_id", getSelectedRelationIds());
                intent.putExtra("title", "请选择一级结点");

                startActivityForResult(intent, REQ_EDIT_ADD);
            }
        });
    }

    private ArrayList<String> getSelectedRelationIds() {
        ArrayList<String> ids = new ArrayList<>();
        if (null != relationships) {
            for (Relationship relationship : relationships) {
                if (null == relationship) continue;

                ids.add(relationship.firstLevelNodeId);
            }
        }
        return ids;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQ_EDIT_ADD: {
                    List<Node> selectedNodeList = (List<Node>) data.getSerializableExtra("selected_node_list");

                    mNodeList.clear();
                    //mNodeList.addAll(selectedNodeList);
                    //mAdapter.notifyDataSetChanged();

                    HashSet<String> selectedIds = new HashSet<>();
                    for (Node node : selectedNodeList) {
                        if (null == node) continue;

                        selectedIds.add(node.id);
                    }

                    ArrayMap<String, Node> nodeMap = MainActivity.mNodesMap;

                    HashSet<String> originalIds = new HashSet<>();
                    //List<Relationship> allDataRelations = MainActivity.mAllData.relationship;
                    int size = relationships.size();
                    for (int i = 0; i < size;) {
                        Relationship relationship = relationships.get(i);
                        if (null != relationship) {
                            if (!selectedIds.contains(relationship.firstLevelNodeId)) {
                                relationships.remove(i);
                                size--;
                                continue;
                            }

                            originalIds.add(relationship.firstLevelNodeId);

                            Node node = nodeMap.get(relationship.firstLevelNodeId);
                            if (null != node) mNodeList.add(node);
                        }

                        i++;
                    }

                    for (String selectedId : selectedIds) {
                        if (!originalIds.contains(selectedId)) {
                            Relationship relationship = new Relationship();
                            relationship.firstLevelNodeId = selectedId;
                            relationships.add(relationship);

                            Node node = nodeMap.get(selectedId);
                            if (null != node) mNodeList.add(node);
                        }
                    }

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
}
