package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecyclerViewDivider;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.entity.vo.NodeSelectVo;
import com.voiceassist.lixinyu.voiceassist.main.ui.MainActivity;
import com.voiceassist.lixinyu.voiceassist.settings.adapter.NodeSelectionAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilidan on 2018/1/25.
 */

public class NodeSelectionActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private NodeSelectionAdapter mAdapter;
    private List<NodeSelectVo> mVoList;


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
        for (Node node : MainActivity.mAllData.nodes) {
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

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            setResult();
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onFinishCalled() {
        //KGLog.d("onFinishCalled");
        setResult();
    }
}
