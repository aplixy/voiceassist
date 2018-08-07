package com.voiceassist.lixinyu.voiceassist.settings.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.common.BaseActivity;
import com.voiceassist.lixinyu.voiceassist.common.Constants;
import com.voiceassist.lixinyu.voiceassist.common.widget.RecordButton;
import com.voiceassist.lixinyu.voiceassist.common.widget.dialog.CommonContentDialog;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.utils.PlayerUtils;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

import java.io.File;

import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.AppSettingsDialog;

/**
 * Created by lilidan on 2018/1/30.
 */

public class NodeAddEditActivity extends BaseActivity implements View.OnClickListener {

    private static final String RECORD_AUDIO_FORMAT = "m4a";

    private EditText mEtId;
    private EditText mEtCnName;
    private EditText mEtAudioPath;
    private EditText mEtIcon;

    private ImageView mIvPlay;

    private RecordButton mBtnRecord;

    private int mPosition;
    private Node mNode;

    private CommonContentDialog mTipDialog;

    private RxPermissions mRxPermissions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_node);

        initView();
        initData();
        initListener();


    }

    private void initView() {
        mEtId = findViewById(R.id.add_node_id_edittext);
        mEtCnName = findViewById(R.id.add_node_cnname_edittext);
        mEtAudioPath = findViewById(R.id.add_node_audioPath_edittext);
        mEtIcon = findViewById(R.id.add_node_icon_edittext);

        mIvPlay = findViewById(R.id.add_node_play_imageview);

        mBtnRecord = findViewById(R.id.add_node_record_button);
    }

    private void initData() {
        this.mPosition = getIntent().getIntExtra("position", -1);
        this.mNode = (Node) getIntent().getSerializableExtra("node");

        if (null != mNode) {
            String id = mNode.id + "";
            mEtId.setText(mNode.id + "");
            mEtCnName.setText(mNode.cnName + "");
            mEtAudioPath.setText(mNode.audioPath + "");
            mEtIcon.setText(mNode.icon + "");

            mEtAudioPath.setSelection(mEtAudioPath.getText().length());

            if (null != id && id.length() > 0) mEtId.setEnabled(false);
        } else {
            mEtId.setText(null);
            mEtCnName.setText(null);
            mEtAudioPath.setText(null);
            mEtIcon.setText(null);
            mEtId.setEnabled(true);
        }

        requestPermissions();

    }

    private ResolveInfo getResolveInfo() {
        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        ResolveInfo ri = this.getPackageManager().
                resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return ri;
    }

    private void initListener() {

        mBtnRecord.setOnBeforeRecordListener(new RecordButton.OnBeforeRecordListener() {
            @Override
            public boolean isAllowStartRecord() {

                if (null == mRxPermissions) mRxPermissions = new RxPermissions(NodeAddEditActivity.this);
                if (!mRxPermissions.isGranted(Manifest.permission.RECORD_AUDIO)) {
                    requestPermissions();
                    return false;
                }

                String id = mEtId.getText().toString();
                if (null == id || id.trim().length() == 0) {
                    ToastUtils.showToast("id不能为空");
                    return false;
                }

                mBtnRecord.setSavePath(getFilePath(id));
                return true;
            }
        });

        mBtnRecord.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath) {
                if (null != audioPath && audioPath.length() > 0) {
                    ToastUtils.showToast("录制完成");
                    mEtAudioPath.setText(audioPath + "");
                    mEtAudioPath.setSelection(audioPath.length());
                }
            }
        });

        mIvPlay.setOnClickListener(this);
    }

    private String getFilePath(String id) {
        return Constants.AUDIO_RECORD_PATH + id + "." + RECORD_AUDIO_FORMAT;
    }

    private void requestPermissions() {
        if (null == mRxPermissions) mRxPermissions = new RxPermissions(NodeAddEditActivity.this);


        mRxPermissions.requestEach(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            new AppSettingsDialog.Builder(NodeAddEditActivity.this).build().show();// 用户选择『不开提示』时引导用户手动开启权限
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_node_play_imageview: {
                String audioPath = mEtAudioPath.getText().toString();
                if (null != audioPath && audioPath.length() > 0) {
                    PlayerUtils.getInstance(this).play(audioPath);
                } else {
                    ToastUtils.showToast("无效路径");
                }
                break;
            }

            default: {
                break;
            }
        }
    }


    private boolean save() {
        if (verify()) {
            if (null == mNode) mNode = new Node();

            mNode.id = mEtId.getText().toString().trim();
            mNode.cnName = mEtCnName.getText().toString().trim();
            mNode.audioPath = mEtAudioPath.getText().toString().trim();
            mNode.icon = mEtIcon.getText().toString().trim();

            Intent data = new Intent();
            data.putExtra("position", mPosition);
            data.putExtra("node", mNode);
            setResult(RESULT_OK, data);

            return true;
        }

        return false;
    }

    private boolean verify() {
        String id = mEtId.getText().toString().trim();
        if (null == id || id.length() == 0) {
            ToastUtils.showToast("id不能为空");
            return false;
        }

        String cnName = mEtCnName.getText().toString().trim();
        if (null == cnName || cnName.length() == 0) {
            ToastUtils.showToast("cnName不能为空");
            return false;
        }

        String audioPath = mEtAudioPath.getText().toString().trim();
        if (null == audioPath || audioPath.length() == 0) {
            ToastUtils.showToast("audioPath不能为空");
            return false;
        }

        return true;
    }

    @Override
    protected void onFinishCalled() {

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
                if (save()) {
                    super.finish();
                } else {
                    ToastUtils.showToast("保存失败");
                }

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
                            String id = mEtId.getText().toString().trim();
                            if (null != id && id.length() > 0) {
                                File file = new File(getFilePath(id));
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            mTipDialog.dismiss();

                            NodeAddEditActivity.super.finish();
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
