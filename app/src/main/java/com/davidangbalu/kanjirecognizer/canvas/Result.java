package com.davidangbalu.kanjirecognizer.canvas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class Result extends View {
    public static final int LABELSIZE = 1;
    public static final int PERCENTAGESIZE = 2;
    public static Typeface LABELFONT = Typeface.MONOSPACE;
    public static Typeface PERCENTAGEFONT = Typeface.MONOSPACE;
    public final String label;
    public final float percentage;
    public TextPaint textPaint;
    public int lFontSize;
    public float lText;
    public float lLeft;
    public float lBottom;
    public int pFontSize;
    public float pTextWidth;
    public float pLeft;
    public float pBottom;

    public Result(Context c, AttributeSet a, String label, float percentage) {
        super(c, a);
        this.label = label;
        this.percentage = percentage;
        textPaint = new TextPaint();
    }

    public String percentageToString() {
        return String.format(Locale.ENGLISH, "%.1f%%", percentage * 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        textPaint.setTypeface(LABELFONT);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(lFontSize);
        textPaint.setTypeface(LABELFONT);
        canvas.drawText(label, lLeft, lBottom, textPaint);
        textPaint.setTextSize(pFontSize);
        textPaint.setTypeface(PERCENTAGEFONT);
        canvas.drawText(percentageToString(), pLeft, pBottom, textPaint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        textPaint.setTypeface(LABELFONT);
        lFontSize = height >> LABELSIZE;
        textPaint.setTextSize(lFontSize);
        textPaint.setTypeface(LABELFONT);
        lText = textPaint.measureText(label);
        lLeft = Math.max(0, (width - lText)) / 2;
        lBottom = height * 0.6f;
        pFontSize = height >> PERCENTAGESIZE;
        textPaint.setTextSize(pFontSize);
        textPaint.setTypeface(PERCENTAGEFONT);
        pTextWidth = textPaint.measureText(percentageToString());
        pLeft = Math.max(0, (width - pTextWidth)) / 2;
        pBottom = height * 0.9f;
    }
}
