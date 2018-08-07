package com.voiceassist.lixinyu.voiceassist.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.voiceassist.lixinyu.voiceassist.common.rx.RxManager;

/**
 * Created by lilidan on 2018/1/22.
 */

public class BaseActivity extends AppCompatActivity {
    protected RxManager mRxManager = new RxManager();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        //android.R.id.home对应应用程序图标的id
        if(item.getItemId() == android.R.id.home)
        {
            onTopLeftButtonClicked();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        onFinishCalled();
        super.finish();
    }

    protected void onFinishCalled() {

    }

    protected void onTopLeftButtonClicked() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxManager.unSubscribe();
    }
}