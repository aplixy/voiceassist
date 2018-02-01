package com.voiceassist.lixinyu.voiceassist.common.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.voiceassist.lixinyu.voiceassist.R;

/**
 * Created by lilidan on 2018/1/31.
 */

public class CommonContentDialog extends CommonDialog implements View.OnClickListener {

    private TextView mContentTv;
    private Button mYesBtn;
    private Button mNoBtn;

    private String contentText;

    private String yesBtnText;
    private String noBtnText;

    private View.OnClickListener onYesClickListener;
    private View.OnClickListener onNoClickListener;

    public CommonContentDialog(Builder builder) {
        super(builder);

        contentText = builder.contentText;

        yesBtnText = builder.yesBtnText;
        noBtnText = builder.noBtnText;

        onYesClickListener = builder.onYesClickListener;
        onNoClickListener = builder.onNoClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContentTv = view.findViewById(R.id.dialog_common_content_textview);
        mYesBtn = view.findViewById(R.id.dialog_common_button_yes);
        mNoBtn = view.findViewById(R.id.dialog_common_button_no);

        mContentTv.setText(contentText);

        mYesBtn.setOnClickListener(this);
        mNoBtn.setOnClickListener(this);

        if (!TextUtils.isEmpty(yesBtnText)) {
            mYesBtn.setText(yesBtnText);
        }

        if (!TextUtils.isEmpty(noBtnText)) {
            mNoBtn.setText(noBtnText);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_common_button_yes: {
                if (null != onYesClickListener) {
                    onYesClickListener.onClick(v);
                }
                break;
            }
            case R.id.dialog_common_button_no: {
                if (null != onNoClickListener) {
                    onNoClickListener.onClick(v);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    public Button getYesButton() {
        return mYesBtn;
    }

    public Button getNoButton() {
        return mNoBtn;
    }

    public TextView getContentTextView() {
        return mContentTv;
    }

    public static class Builder extends CommonDialog.Builder {

        private String contentText;

        private String yesBtnText;
        private String noBtnText;

        private View.OnClickListener onYesClickListener;
        private View.OnClickListener onNoClickListener;

        public Builder(Context context) {
            super(context);
        }

        public Builder contentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder yesBtnText(String yesBtnText) {
            this.yesBtnText = yesBtnText;
            return this;
        }

        public Builder noBtnText(String noBtnText) {
            this.noBtnText = noBtnText;
            return this;
        }

        public Builder onYesClickListener(View.OnClickListener listener) {
            this.onYesClickListener = listener;
            return this;
        }

        public Builder onNoClickListener(View.OnClickListener listener) {
            this.onNoClickListener = listener;
            return this;
        }

        @Override
        public CommonContentDialog build() {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
            return new CommonContentDialog(this);
        }
    }
}
