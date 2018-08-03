package com.voiceassist.lixinyu.voiceassist.common.rx


import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Horrarndoo on 2017/9/12.
 *
 *
 * 用于管理Rxjava 注册订阅和取消订阅
 */

class RxManager {
    private val mCompositeDisposable = CompositeDisposable()// 管理订阅者者

    fun register(d: Disposable?) {
        mCompositeDisposable.add(d)
    }

    fun unSubscribe() {
        mCompositeDisposable.dispose()// 取消订阅
    }
}