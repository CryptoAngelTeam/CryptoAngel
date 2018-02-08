/*
 * Copyright (c) 2017 3Coding Inc.
 * All right, including trade secret rights, reserved.
 */

package com.a3coding.cryptoangel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class VoiceButtonAnimation extends View {

    private static final int STROKE_WIDTH = 20;
    private Paint mPaint;
    private RectF mRect;
    private boolean mShouldAnimate = false;
    private int mStep = 0;

    public VoiceButtonAnimation(Context context) {
        super(context);
        init();
    }

    public VoiceButtonAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceButtonAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.hihglight_color));
    }

    public void startAnimation() {
        mShouldAnimate = true;
        mStep = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

            if (mRect == null) {
                // take the minimum of width and height here to be on he safe side:
                int centerX = getMeasuredWidth() / 2;
                int centerY = getMeasuredHeight() / 2;
                int radius = Math.min(centerX, centerY);
                int startTop = STROKE_WIDTH / 2;
                int startLeft = startTop;
                int endBottom = 2 * radius - startTop;
                int endRight = endBottom;

                mRect = new RectF(startTop, startLeft, endRight, endBottom);
            }

        if (mShouldAnimate) {
            mStep++;
            canvas.drawArc(mRect, 90 + 2 * mStep, 102 + 2 * mStep, false, mPaint);
            // reset after full circle
            if (mStep > 360) {
                mStep = 0;
                mShouldAnimate = false;
            }
            this.postInvalidateDelayed(20);
        } else {
            // draw full circle
            canvas.drawArc(mRect, 90, 360, false, mPaint);
        }
    }
}
