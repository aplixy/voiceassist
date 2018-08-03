package com.voiceassist.lixinyu.voiceassist.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.view.View

import com.voiceassist.lixinyu.voiceassist.R


/**
 * ViewPager指示
 * @author xinyuli
 */
class ViewPagerPointer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var maxCount: Int = 0
    private val maxCountBackup: Int

    private var mViewPager: ViewPager? = null
    private var mScrollState: Int = 0
    private var mListener: OnPageChangeListener? = null
    private var mCurrentPage: Int = 0
    private var mPageOffset: Float = 0.toFloat()

    private val mPaint = Paint()

    private var pointSize: Float = 0.toFloat()
    private var pointPadding: Float = 0.toFloat()
    private var cursorX: Float = 0.toFloat()

    private var count: Int = 0

    private val colorBg: Int
    private val colorIndicator: Int

    private var mPreviousPagerAdapter: PagerAdapter? = null

    private var widthSpecMode: Int = 0
    private var heightSpecMode: Int = 0

    init {


        //============================================================================================
        //Retrieve styles attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerPointer, defStyleAttr, 0)

        //Retrieve the colors to be used for this view and apply them.
        colorIndicator = a.getColor(R.styleable.ViewPagerPointer_colorIndicatorPoint, Color.parseColor("#E6E6E6"))
        colorBg = a.getColor(R.styleable.ViewPagerPointer_colorBgPoint, Color.parseColor("#FFFFFF"))
        pointSize = a.getDimension(R.styleable.ViewPagerPointer_pointSize, 17f)
        pointPadding = a.getDimension(R.styleable.ViewPagerPointer_pointPadding, 23f)
        maxCount = a.getInt(R.styleable.ViewPagerPointer_maxCount, DEFAULT_MAX_COUNT)
        maxCountBackup = maxCount

        a.recycle()

        //============================================================================================

        mPaint.isAntiAlias = true

        // mPaint.setStyle(Paint.Style.STROKE);//设置空心
        count = if (count <= 0) 1 else count

        cursorX = pointSize / 2

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val widgetW = width// 组件宽度
        var unitW = pointSize.toInt()


        if (count > 0) {
            if (pointSize * count > widgetW) pointSize = (widgetW / count - 3 * count).toFloat()
            pointPadding = (widgetW - count * pointSize) / (count - 1)
            unitW = (pointSize + pointPadding).toInt()
        }

        val pointRadius = pointSize / 2

        // 画灰色的背景
        mPaint.color = colorBg

        for (i in 0 until count) {
            canvas.drawCircle(i * unitW + pointRadius, pointRadius, pointRadius, mPaint)
        }

        mPaint.color = colorIndicator

        if (mCurrentPage == count - 1) {
            cursorX = pointRadius + mCurrentPage * unitW - mPageOffset * unitW.toFloat() * (count - 1).toFloat()
        } else {
            cursorX = pointRadius + (mCurrentPage * unitW).toFloat() + mPageOffset * unitW
        }

        canvas.drawCircle(cursorX, pointRadius, pointRadius, mPaint)
    }

    @SuppressLint("WrongConstant")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Measure our width in whatever mode specified


        val width: Float
        widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        if (widthSpecMode == View.MeasureSpec.EXACTLY) {
            //We were told how big to be
            width = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        } else {
            //Calculate the text bounds
            width = pointSize * count + pointPadding * (count - 1)
        }

        //Determine our height
        val height: Float
        heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (heightSpecMode == View.MeasureSpec.EXACTLY) {
            //We were told how big to be
            height = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        } else {
            //Calculate the text bounds
            height = pointSize
        }

        val measuredWidth = width.toInt()
        val measuredHeight = height.toInt()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun retrySize() {
        if (View.MeasureSpec.EXACTLY == widthSpecMode && View.MeasureSpec.EXACTLY == heightSpecMode) return

        var width = 0f
        var height = 0f

        if (View.MeasureSpec.EXACTLY != widthSpecMode) {
            width = pointSize * count + pointPadding * (count - 1)
        }

        if (View.MeasureSpec.EXACTLY != heightSpecMode) {
            height = pointSize
        }

        val measuredWidth = width.toInt()
        val measuredHeight = height.toInt()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    fun setViewPager(view: ViewPager?) {
        if (mViewPager !== view) {
            maxCount = maxCountBackup
        }

        if (mViewPager != null) {
            mViewPager!!.setOnPageChangeListener(null)

        }

        val pagerAdapter = view?.adapter ?: throw IllegalStateException(
                "ViewPager does not have adapter instance.")

        if (mPreviousPagerAdapter !== pagerAdapter) {
            maxCount = maxCountBackup
        }

        mPreviousPagerAdapter = pagerAdapter

        mViewPager = view

        count = pagerAdapter.count
        if (count > maxCount)
            count = maxCount
        else if (count < maxCount) maxCount = count


        if (0 == maxCount) return

        mViewPager!!.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                mScrollState = state

                if (mListener != null) {
                    mListener!!.onPageScrollStateChanged(state)
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
                mCurrentPage = position % maxCount
                //KGLog.d("mCurrentPage--->" + mCurrentPage);
                mPageOffset = positionOffset

                invalidate()

                if (mListener != null) {
                    mListener!!.onPageScrolled(position, positionOffset,
                            positionOffsetPixels)
                }
            }

            override fun onPageSelected(position: Int) {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    mCurrentPage = position % maxCount

                    //KGLog.i("mCurrentPage--->" + mCurrentPage);
                    invalidate()
                }

                if (mListener != null) {
                    mListener!!.onPageSelected(position)
                }
            }
        })
        invalidate()

        //retrySize();
    }


    fun getMaxCount(): Int {
        return maxCount
    }

    fun setMaxCount(maxCount: Int) {
        var maxCount = maxCount
        if (maxCount > DEFAULT_MAX_COUNT) maxCount = DEFAULT_MAX_COUNT
        this.maxCount = maxCount
    }

    fun setOnPageChangeListener(l: OnPageChangeListener) {
        this.mListener = l
    }

    companion object {

        private val TAG = "ViewPagerPointer"

        private val DEFAULT_MAX_COUNT = 20
        private val INIT_PADDING = 0
    }
}
