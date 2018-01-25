package com.voiceassist.lixinyu.voiceassist.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.entity.dto.Node;
import com.voiceassist.lixinyu.voiceassist.utils.ToastUtils;

/**
 * Created by lilidan on 2018/1/25.
 */

public class EditNodeDialog extends Dialog implements View.OnClickListener {

    private EditText mEtId;
    private EditText mEtCnName;
    private EditText mEtAudioPath;
    private EditText mEtIcon;

    private Button mBtnNo;
    private Button mBtnYes;

    private Node mNode;

    private int mPosition;

    private OnPositiveButtonClickListener mListener;

    public EditNodeDialog(@NonNull Context context) {
        super(context, R.style.editDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_edit_node);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mEtId = findViewById(R.id.dialog_edit_node_id_edittext);
        mEtCnName = findViewById(R.id.dialog_edit_node_cnname_edittext);
        mEtAudioPath = findViewById(R.id.dialog_edit_node_audioPath_edittext);
        mEtIcon = findViewById(R.id.dialog_edit_node_icon_edittext);

        mBtnNo = findViewById(R.id.dialog_edit_node_button_no);
        mBtnYes = findViewById(R.id.dialog_edit_node_button_yes);
    }

    private void initData() {
        renderView();

    }

    private void initEvent() {
        mBtnNo.setOnClickListener(this);
        mBtnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_edit_node_button_no: {
                dismiss();
                break;
            }

            case R.id.dialog_edit_node_button_yes: {
                save();
                break;
            }

            default: {
                break;
            }
        }
    }

    private void save() {
        if (null == mNode) mNode = new Node();

        if (verify()) {
            mNode.id = mEtId.getText().toString().trim();
            mNode.cnName = mEtCnName.getText().toString().trim();
            mNode.audioPath = mEtAudioPath.getText().toString().trim();
            mNode.icon = mEtIcon.getText().toString().trim();

            if (null != mListener) {
                mListener.onDialogPositiveClick(mPosition, mNode);
            }

            dismiss();
        }
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


    public interface OnPositiveButtonClickListener {
        void onDialogPositiveClick(int position, Node node);
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener listener) {
        this.mListener = listener;
    }

    public void setData(int position, Node node) {
        this.mPosition = position;
        this.mNode = node;

        if (null != mEtId) {
            renderView();
        }
    }

    private void renderView() {


        if (null != mNode) {
            String id = mNode.id + "";
            mEtId.setText(mNode.id + "");
            mEtCnName.setText(mNode.cnName + "");
            mEtAudioPath.setText(mNode.audioPath + "");
            mEtIcon.setText(mNode.icon + "");

            if (null != id && id.length() > 0) mEtId.setEnabled(false);
        } else {
            mEtId.setText(null);
            mEtCnName.setText(null);
            mEtAudioPath.setText(null);
            mEtIcon.setText(null);
            mEtId.setEnabled(true);
        }
    }
}
