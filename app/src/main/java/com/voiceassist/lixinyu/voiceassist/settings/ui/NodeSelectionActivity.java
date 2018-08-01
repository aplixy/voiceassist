package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.vo.NodeSelectVo;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeSelectionAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择结点页面，这是一个工具型页面，跳转到该页面即可通过勾选的方式选择结点，可用于编辑一级结点和二级结点<br/>
 * @input title - StringExtra 标题
 * @input selected_nodes_id - StringList 已选中的结点id列表
 * @author Created by lilidan on 2018/1/25.
 */

public class NodeSelectionActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private NodeSelectionAdapter mAdapter;
    private List<NodeSelectVo> mVoList;

    private CommonContentDialog mTipDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_selection);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.node_selection_recyclerview);
    }

    private void initData() {
        String title = getIntent().getStringExtra("title");
        if (null != title && title.length() > 0) {
            setTitle(title);
        } else {
            setTitle("请选择要添加的节点");
        }

        List<String> selectedIdList = (List<String>) getIntent().getSerializableExtra("selected_nodes_id");

        mVoList = new ArrayList<>();
        for (Node node : MainActivity.Companion.getMAllData().nodes) {
            if (null == node) continue;

            NodeSelectVo vo = new NodeSelectVo();
            vo.isSelected = null != selectedIdList && selectedIdList.contains(node.id);
            vo.node = node;
            mVoList.add(vo);
        }

        mAdapter = new NodeSelectionAdapter(this, mVoList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {

    }

    private void setResult() {
        ArrayList<Node> selectedList = new ArrayList<>();
        for (NodeSelectVo vo : mVoList) {
            if (null == vo) continue;

            if (vo.isSelected) selectedList.add(vo.node);
        }

        setResult(RESULT_OK, new Intent().putExtra("selected_node_list", selectedList));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_node, menu);//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                setResult();
                super.finish();
                break;
            }

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        exitTip();
    }

    private void exitTip() {
        if (null == mTipDialog) {
            mTipDialog = new CommonContentDialog.Builder(this)
                    .contentText("确认退出吗？")
                    .yesBtnText("容朕想想")
                    .noBtnText("去意已决")
                    .onNoClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTipDialog.dismiss();
                            NodeSelectionActivity.super.finish();
                        }
                    })
                    .onYesClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTipDialog.dismiss();

                        }
                    })
                    .build();
        }

        mTipDialog.show();
    }
}
