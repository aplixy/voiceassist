package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.utils.AppUtil;

/**
 * Created by lilidan on 2018/1/25.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private View mNodeItemView;
    private View mRelationshipItemView;

    private TextView mVersionTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mNodeItemView = findViewById(R.id.setting_item_node);
        mRelationshipItemView = findViewById(R.id.setting_item_relationship);
        mVersionTv = findViewById(R.id.setting_item_version_textview);
    }

    private void initData() {
        setTitle("设置");
        mVersionTv.setText(AppUtil.getCurVer(this));
    }

    private void initListener() {
        mNodeItemView.setOnClickListener(this);
        mRelationshipItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_item_node: {
                startActivity(new Intent(this, EditNodeActivity.class));
                break;
            }
            case R.id.setting_item_relationship: {
                startActivity(new Intent(this, EditRelationshipActivity.class));
                break;
            }
            default:{
                break;
            }
        }
    }
}
