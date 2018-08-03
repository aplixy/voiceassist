package com.voiceassist.lixinyu.voiceassist.common

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.voiceassist.lixinyu.voiceassist.common.rx.RxManager

/**
 * Created by lilidan on 2018/1/22.
 */

open class BaseActivity : AppCompatActivity() {
    protected var mRxManager = RxManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)

        //getSupportActionBar().hide();

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.elevation = 0f
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO Auto-generated method stub
        //android.R.id.home对应应用程序图标的id
        if (item.itemId == android.R.id.home) {
            onTopLeftButtonClicked()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        onFinishCalled()
        super.finish()
    }

    protected open fun onFinishCalled() {

    }

    protected fun onTopLeftButtonClicked() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        mRxManager.unSubscribe()
    }
}
