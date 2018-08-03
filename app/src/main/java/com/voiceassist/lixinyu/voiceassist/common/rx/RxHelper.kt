package com.voiceassist.lixinyu.voiceassist.common.rx


import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Horrarndoo on 2017/9/12.
 *
 *
 */
object RxHelper {
    /**
     * 统一线程处理
     *
     *
     * 发布事件io线程，接收事件主线程
     */
    fun <T> rxSchedulerHelper(): ObservableTransformer<T, T> {//compose处理线程
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> rxSchedulerNewThreadHelper(): ObservableTransformer<T, T> {//compose处理线程
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 生成Flowable
     *
     * @param t
     * @return Flowable
     */
    fun <T> createFlowable(t: T): Flowable<T> {
        return Flowable.create({ emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }, BackpressureStrategy.BUFFER)
    }

    /**
     * 生成Observable
     *
     * @param t
     * @return Flowable
     */
    fun <T> createObservable(t: T): Observable<T> {
        return Observable.create { emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}
