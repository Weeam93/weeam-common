package com.example.weeam.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.weeam.common.R;


/**
 * Created by Weeam Awad on 5/10/2018.
 */

public class CustomView extends View {
    private final String FORMAT = "000.0";
    private final int DEFAULT_FONT_PADDING = 20;
    private String mText;
    private int mTextSize;

    private TextPaint mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    private Rect mBounds = new Rect();

    public CustomView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet, int deStyle) {
        TypedArray styles = context.obtainStyledAttributes(attributeSet, R.styleable.CustomView, deStyle, 0);
        mText = styles.getString(R.styleable.CustomView_text);
        mTextPaint.setColor(styles.getColor(R.styleable.CustomView_color, ContextCompat.getColor(context, R.color.black)));
        mTextPaint.setTextSize(styles.getDimensionPixelSize(R.styleable.CustomView_textSize, getResources().getDimensionPixelSize(R.dimen.font_size_large)));
        styles.recycle();
    }

    public void setText(String text) {
        mText = text;
        //requestLayout();
        invalidate();
    }

    public String getText() {
        return mText;
    }

    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
        invalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.getTextBounds(FORMAT, 0, FORMAT.length(), mBounds);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int desiredWidth = (int) (mBounds.width() + getPaddingStart() + getPaddingEnd());
        int desiredHeight = (int) (mBounds.height() + getPaddingTop() + getPaddingEnd() + mTextPaint.getFontSpacing());
        int finalWidth;
        int finalHeight;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                //Match_parent
                finalWidth = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                //Wrap_Content
                finalWidth = Math.min(desiredWidth, widthSize);
                Log.d("Testing", "Calling WrapContent, Width = " + finalWidth);
                break;
            default:
                finalWidth = desiredWidth;
                break;
        }

        switch (heightmode) {
            case MeasureSpec.EXACTLY:
                //Match_parent
                finalHeight = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                //Wrap_Content
                finalHeight = Math.min(desiredHeight, heightSize);
                Log.d("Testing", "Calling WrapContent, Height = " + finalHeight);
                break;
            default:
                finalHeight = desiredHeight;
                break;
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText != null) {
            final int width = getWidth();
            final int height = getHeight();
            //Draw in the Center of the View
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mText, width / 2, height / 2, mTextPaint);
        }
    }
}
