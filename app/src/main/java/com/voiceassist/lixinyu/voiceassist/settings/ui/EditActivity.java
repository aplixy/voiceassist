package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditActivity extends BaseActivity {

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
        mBtnEditNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, EditNodeActivity.class);
                startActivity(intent);
            }
        });
    }
}
