package com.voiceassist.lixinyu.voiceassist.common;

import android.app.Application;

/**
 * Created by lixinyu on 2018/1/21.
 */

public class AssistApplication extends Application {

    protected static AssistApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    /**
     * 获取Application实例
     *
     * @return
     */
    public static AssistApplication getInstance() {
        if (mApplication == null) {
            throw new IllegalStateException("Application is not created.");
        }
        return mApplication;
    }


}
