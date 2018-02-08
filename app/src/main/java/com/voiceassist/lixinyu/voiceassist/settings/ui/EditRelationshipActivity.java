package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.RelationshipListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditRelationshipActivity extends BaseActivity {

    private static final int REQ_EDIT_ADD = 0x100;

    private Button mBtnAddRelationship;

    private RecyclerView mRecyclerView;
    private RelationshipListAdapter mAdapter;
    private ArrayList<Node> mNodeList;

    private List<Relationship> relationships;

    private boolean mIsAllowSort;
    private int mToPosition = -1;
    private CommonContentDialog mTipDialog;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mRootViewGroup;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_relationship);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_order: {
                if (!mIsAllowSort) {
                    item.setTitle("保存排序");
                    mIsAllowSort = true;
                } else {
                    sortOrder(mIsAllowSort, mToPosition);
                    mToPosition = -1;

                    mIsAllowSort = false;
                    item.setTitle("排序");
                }

                mAdapter.setAllowSortOrder(mIsAllowSort);
                break;
            }

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mBtnAddRelationship = findViewById(R.id.edit_relationship_add_button);
        mRecyclerView = findViewById(R.id.edit_relationship_recyclerview);
        mSwipeRefreshLayout = findViewById(R.id.edit_relationship_swiperefreshlayout);
        mRootViewGroup = findViewById(R.id.edit_relationship_root_viewgroup);
    }

    private void initData() {
        setTitle("一级节点列表");

        relationships = MainActivity.mAllData.relationship;
        if (null == relationships) {
            mRootViewGroup.setVisibility(View.VISIBLE);
            return;
        }

        mNodeList = new ArrayList<>();



        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        Observable.just(relationships)
                .map(new Function<List<Relationship>, List<Node>>() {
                    @Override
                    public List<Node> apply(List<Relationship> relationships) throws Exception {
                        if (null != relationships) {
                            List<Node> nodeList = new ArrayList<>();
                            for (Relationship relationship : relationships) {
                                if (null == relationship) continue;
                                Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
                                if (null != node) {
                                    nodeList.add(node);
                                }
                            }
                            return nodeList;
                        }
                        return null;
                    }
                })
                .compose(RxHelper.<List<Node>>rxSchedulerNewThreadHelper())
                .subscribe(new Consumer<List<Node>>() {
                    @Override
                    public void accept(List<Node> nodeList) throws Exception {
                        if (null != nodeList) {
                            mNodeList.addAll(nodeList);
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRootViewGroup.setVisibility(View.VISIBLE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

//        Observable.fromIterable(relationships)
//                .compose(RxHelper.<Relationship>rxSchedulerNewThreadHelper())
//                .subscribe(new Observer<Relationship>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mLoadingDialog.show();
//                    }
//
//                    @Override
//                    public void onNext(Relationship relationship) {
//                        if (null == relationship) return;
//
//                        Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
//                        if (null != node) {
//                            mNodeList.add(node);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mLoadingDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        mLoadingDialog.dismiss();
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });

//        for (Relationship relationship : relationships) {
//            if (null == relationship) continue;
//            Node node = MainActivity.mNodesMap.get(relationship.firstLevelNodeId);
//            if (null != node) {
//                mNodeList.add(node);
//            }
//        }

        mAdapter = new RelationshipListAdapter(this, mNodeList);

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


    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new RelationshipListAdapter.OnItemClickListener() {
            @Override
            public void onClick(RelationshipListAdapter adapter, int position, Node node) {
                int realPosition = -1;
                if (null != relationships) {
                    int i = 0;
                    for (Relationship relationship : relationships) {
                        if (null != relationship && null != node && node.id.equals(relationship.firstLevelNodeId)) {
                            realPosition = i;
                            break;
                        }

                        i++;
                    }
                }

                Intent intent = new Intent(EditRelationshipActivity.this, EditSecondLevelRelationActivity.class);
                intent.putExtra("position", realPosition);
                startActivity(intent);
            }
        });

        mAdapter.setOnItemMoveCompleteListener(new RelationshipListAdapter.OnItemMoveCompleteListener() {
            @Override
            public void onComplete(int fromPosition, int toPosition) {
                mToPosition = toPosition;
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

    private void sortOrder(boolean isAllowSort, int toPosition) {
        if (!isAllowSort) return;
        if (toPosition == -1) return;

        // 最终目的是要移动relationship列表中的项
        if (null == relationships) return;

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
        for (Relationship relationship : relationships) {
            if (null != relationship && null != relationship.firstLevelNodeId) {

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
        if (toRelationPos == relationships.size()) {
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
                                EditRelationshipActivity.super.finish();
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
                    for (int i = 0; i < size; ) {
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
