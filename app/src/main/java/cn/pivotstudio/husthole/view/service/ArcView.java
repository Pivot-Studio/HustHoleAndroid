package cn.pivotstudio.husthole.view.service;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.pivotstudio.husthole.R;


public class ArcView extends View {//已经弃用，最开始准备用来画连续弧
    private int mWidth;
    private int mHeight;
    /**
     * 弧形高度
     */
    private int mArcHeight;
    /**
     * 背景颜色
     */
    private int mBgColor;
    private Paint mPaint,mPaint2;
    private Context mContext;

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView);
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0);
        mBgColor=typedArray.getColor(R.styleable.ArcView_bgColor, Color.parseColor("#303F9F"));
        typedArray.recycle();
        mContext = context;
        mPaint = new Paint();
        mPaint2=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);
       // mPaint2.setStyle(Paint.Style.FILL);
        //mPaint2.setColor(mBgColor);

       // Rect rect = new Rect(0, 0, mWidth, mHeight - mArcHeight);
       // canvas.drawRect(rect, mPaint);

        float a=1/3;
        Path path2 = new Path();
        Path path3= new Path();
       path2.moveTo(mWidth/3, mHeight/2);
       path2.quadTo(mWidth/2, mHeight, mWidth*2/3, (mHeight/2));
        //path2.moveTo(mWidth/3,0);
        //path2.lineTo(mWidth/3, 0);
        //path2.lineTo(0,mHeight/4 );
        //path2.close();
        canvas.drawPath(path2, mPaint);
       // path3.moveTo(mWidth/3,0);
       // path3.lineTo(0,mHeight/4 );
        //path3.lineTo(-mWidth/3,0);
       // canvas.drawPath(path3, mPaint2);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }
}

