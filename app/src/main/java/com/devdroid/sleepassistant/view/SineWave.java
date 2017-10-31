package com.devdroid.sleepassistant.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.devdroid.sleepassistant.utils.Util;

public class SineWave extends View {
    int[] datas = new int[100]; //
    private Paint mPaint = null;

    int centerStartingX, centerStartingY; //
    int centerEndX, centerEndY; //
    double ScaleX, ScaleY; //

    public SineWave(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setAlpha(200);
        mPaint.setStrokeWidth(5);
    }

    public SineWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setAlpha(200);
        mPaint.setStrokeWidth(5);
    }

    public void Set(int frequency) {
        for (int i = 0; i < datas.length - 1; i++) {
            datas[i] = datas[i + 1];
        }
        datas[datas.length - 1] = frequency;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        centerStartingX = Util.centerStartingX;

        for (int i = 0; i < datas.length - 1; i++) {

            centerStartingX += Util.ScaleX;
            canvas.drawLine(centerStartingX, (float) (Util.core - datas[i]
                            * Util.spacingY), (float) (centerStartingX + Util.ScaleX),
                    (float) (Util.core - datas[i + 1] * Util.spacingY), mPaint);
//            canvas.drawText("" + datas[i + 1],
//                    (float) (centerStartingX + Util.ScaleX) - 20,
//                    (float) (Util.core - datas[i + 1] * Util.spacingY), mPaint);
        }

    }

    public void reFresh() {
        this.invalidate();
    }

}
