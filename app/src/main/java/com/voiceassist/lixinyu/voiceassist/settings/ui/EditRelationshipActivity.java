package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditRelationshipActivity extends BaseActivity {

    private Button mBtnEditNode;
    private Button mBtnEditRelationship;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBtnEditNode = findViewById(R.id.edit_edit_node_button);
        mBtnEditRelationship = findViewById(R.id.edit_edit_relationship_button);
    }

    private void initData() {

    }

    private void initListener() {

    }
}
