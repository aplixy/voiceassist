package com.voiceassist.lixinyu.voiceassist.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.voiceassist.lixinyu.voiceassist.R;
import com.voiceassist.lixinyu.voiceassist.utils.DisplayUtils;

/**
 * Created by lilidan on 2018/1/25.
 */

public class CommonDialog extends Dialog {

    protected Context context;
    protected int height, width;
    protected boolean cancelTouchout;
    protected View view;



    public CommonDialog(Builder builder) {
        this(builder, R.style.editDialog);
    }

    private CommonDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelTouchout = builder.cancelTouchout;
        view = builder.view;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);

        setCanceledOnTouchOutside(cancelTouchout);


        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;

        if (height > 0) lp.height = height;
        if (width > 0) lp.width = width;

        win.setAttributes(lp);
    }


    public static class Builder {

        protected Context context;
        protected int height, width;
        protected boolean cancelTouchout;
        protected View view;
        protected int resStyle = -1;



        public Builder(Context context) {
            this.context = context;
        }

        public Builder view(int resView) {
            view = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }

        public Builder heightpx(int val) {
            height = val;
            return this;
        }

        public Builder widthpx(int val) {
            width = val;
            return this;
        }

        public Builder heightdp(int val) {
            height = DisplayUtils.INSTANCE.dip2px(context, val);
            return this;
        }

        public Builder widthdp(int val) {
            width = DisplayUtils.INSTANCE.dip2px(context, val);
            return this;
        }

        public Builder heightDimenRes(int dimenRes) {
            height = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder widthDimenRes(int dimenRes) {
            width = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder cancelTouchout(boolean val) {
            cancelTouchout = val;
            return this;
        }

        public Builder addViewOnclick(int viewRes,View.OnClickListener listener){
            view.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }


        public CommonDialog build() {
            if (resStyle != -1) {
                return new CommonDialog(this, resStyle);
            } else {
                return new CommonDialog(this);
            }
        }
    }
}
