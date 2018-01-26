package com.voiceassist.lixinyu.voiceassist.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

import com.voiceassist.lixinyu.voiceassist.R;


/**
 * ViewPager指示
 * @author xinyuli
 */
public class ViewPagerPointer extends View {

	private static final String TAG = "ViewPagerPointer";

	private static final int DEFAULT_MAX_COUNT = 20;
	private static final int INIT_PADDING = 0;
	
	private int maxCount;
	private int maxCountBackup;

	private ViewPager mViewPager;
	private int mScrollState;
	private OnPageChangeListener mListener;
	private int mCurrentPage;
	private float mPageOffset;

	private Paint mPaint = new Paint();
	
	private float pointSize;
	private float pointPadding;
	private float cursorX;

	private int count;

	private int colorBg;
	private int colorIndicator;
	
	private PagerAdapter mPreviousPagerAdapter;

	private int widthSpecMode;
	private int heightSpecMode;



	public ViewPagerPointer(Context context) {
		this(context, null);
	}

	public ViewPagerPointer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerPointer(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		
		//============================================================================================
		//Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerPointer, defStyleAttr, 0);

        //Retrieve the colors to be used for this view and apply them.
        colorIndicator = a.getColor(R.styleable.ViewPagerPointer_colorIndicatorPoint, Color.parseColor("#E6E6E6"));
        colorBg = a.getColor(R.styleable.ViewPagerPointer_colorBgPoint, Color.parseColor("#FFFFFF"));
        pointSize = a.getDimension(R.styleable.ViewPagerPointer_pointSize, 17);
        pointPadding = a.getDimension(R.styleable.ViewPagerPointer_pointPadding, 23);
		maxCountBackup = maxCount = a.getInt(R.styleable.ViewPagerPointer_maxCount, DEFAULT_MAX_COUNT);
        
        a.recycle();
		
		//============================================================================================
		
		mPaint.setAntiAlias(true);
		
		// mPaint.setStyle(Paint.Style.STROKE);//设置空心
		count = count <= 0 ? 1 : count;
		
		cursorX = pointSize / 2;
		
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int widgetW = getWidth();// 组件宽度
		int unitW = (int)pointSize;
		
		
		if(count > 0){
			if(pointSize * count > widgetW) pointSize = widgetW / count - 3*count;
			pointPadding = (widgetW - count * pointSize) / (count - 1);
			unitW = (int)(pointSize + pointPadding);
		}
		
		float pointRadius = pointSize / 2;
		
		// 画灰色的背景
		mPaint.setColor(colorBg);
		
		for (int i = 0; i < count; i++) {
			canvas.drawCircle(i*unitW + pointRadius, pointRadius, pointRadius, mPaint);
		}
		
		mPaint.setColor(colorIndicator);
		
		if(mCurrentPage == count - 1){
			cursorX = pointRadius + mCurrentPage * unitW - mPageOffset * unitW * (count - 1);
		}else{
			cursorX = pointRadius + mCurrentPage * unitW + mPageOffset * unitW;
		}
		
		canvas.drawCircle(cursorX, pointRadius, pointRadius, mPaint);
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Measure our width in whatever mode specified
        
        
        float width;
        widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
        	width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            //Calculate the text bounds
        	width = pointSize * count + pointPadding * (count - 1);
        }

        //Determine our height
        float height;
        heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            //Calculate the text bounds
        	height = pointSize;
        }

		int measuredWidth = (int)width;
		int measuredHeight = (int)height;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void retrySize() {
		if (MeasureSpec.EXACTLY == widthSpecMode && MeasureSpec.EXACTLY == heightSpecMode) return;;

		float width = 0;
		float height = 0;

		if (MeasureSpec.EXACTLY != widthSpecMode) {
			width = pointSize * count + pointPadding * (count - 1);
		}

		if (MeasureSpec.EXACTLY != heightSpecMode) {
			height = pointSize;
		}

		int measuredWidth = (int)width;
		int measuredHeight = (int)height;

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	public void setViewPager(ViewPager view) {
		if (mViewPager != view) {
			maxCount = maxCountBackup;
		}

		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);

		}

		PagerAdapter pagerAdapter = view.getAdapter();
		if (pagerAdapter == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}

		if (mPreviousPagerAdapter != pagerAdapter) {
			maxCount = maxCountBackup;
		}

		mPreviousPagerAdapter = pagerAdapter;
		
		mViewPager = view;
		
		count = pagerAdapter.getCount();
		if(count > maxCount) count  = maxCount;
		else if(count < maxCount) maxCount = count;


		if (0 == maxCount) return;
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
				mScrollState = state;

				if (mListener != null) {
					mListener.onPageScrollStateChanged(state);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				mCurrentPage = position % maxCount;
				//KGLog.d("mCurrentPage--->" + mCurrentPage);
				mPageOffset = positionOffset;
				
				invalidate();

				if (mListener != null) {
					mListener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
				}
			}

			@Override
			public void onPageSelected(int position) {
				if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
					mCurrentPage = position % maxCount;
					
					//KGLog.i("mCurrentPage--->" + mCurrentPage);
					invalidate();
				}
				
				if (mListener != null) {
					mListener.onPageSelected(position);
				}
			}
		});
		invalidate();

		//retrySize();
	}
	


	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		if(maxCount > DEFAULT_MAX_COUNT) maxCount = DEFAULT_MAX_COUNT;
		this.maxCount = maxCount;
	}
	
	public void setOnPageChangeListener(OnPageChangeListener l){
		this.mListener = l;
	}
}
