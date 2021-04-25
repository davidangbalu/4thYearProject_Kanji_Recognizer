package com.davidangbalu.kanjirecognizer.canvas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KanjiCanvas extends View {
    public static final int BACKGROUNDCOLOR = Color.argb(255, 225, 225, 225);
    public static final int STROKECOLOR = Color.BLACK;
    public static int StrokeWidth = 25;

    public boolean fingerD;

    public Rect vRect;
    public Paint vPaint;
    public Paint wStroke;
    public Path strokePath;
    public List<CanvasPoint> currStroke;
    public List<List<CanvasPoint>> wStrokes;

    public KanjiCanvas(Context c, AttributeSet a) {
        super(c, a);
        fingerD = false;
        vRect = new Rect(0, 0, 1, 1);
        vPaint = new Paint();
        vPaint.setColor(BACKGROUNDCOLOR);
        vPaint.setStyle(Paint.Style.FILL);
        vPaint.setAntiAlias(false);
        wStroke = new Paint();
        wStroke.setColor(STROKECOLOR);
        wStroke.setStyle(Paint.Style.STROKE);
        wStroke.setStrokeJoin(Paint.Join.ROUND);
        wStroke.setStrokeCap(Paint.Cap.ROUND);
        wStroke.setAntiAlias(true);
        wStroke.setStrokeWidth(StrokeWidth);
        strokePath = new Path();
        currStroke = null;
        wStrokes = Collections.synchronizedList(new LinkedList<>());
    }

    public void setStrokeWidth(float value) {
        wStroke.setStrokeWidth(value);
    }

    public void clearCanvas() {
        wStrokes.clear();
        strokePath.reset();
        invalidate();
    }

    public void actionDown(CanvasPoint p) {
        if (!fingerD) {
            fingerD = true;
            currStroke = Collections.synchronizedList(new LinkedList<>());
            wStrokes.add(currStroke);
            currStroke.add(p);
            strokePath.moveTo(p.x, p.y);
        }
    }

    public void actionUp(CanvasPoint p) {
        if (fingerD) {
            currStroke = null;
            fingerD = false;
        }
    }

    public void actionMove(CanvasPoint p) {
        if (fingerD) {
            currStroke.add(p);
            strokePath.lineTo(p.x, p.y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        CanvasPoint touchPos = new CanvasPoint(Math.round(event.getX()), Math.round(event.getY()));
        if (action == MotionEvent.ACTION_DOWN) {
            actionDown(touchPos);
        } else if (action == MotionEvent.ACTION_UP) {
            actionUp(touchPos);
        } else if (action == MotionEvent.ACTION_MOVE) {
            actionMove(touchPos);
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(vRect, vPaint);
        wStroke.setStrokeWidth(StrokeWidth);
        canvas.drawPath(strokePath, wStroke);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        vRect = new Rect(0, 0, width, height);
        invalidate();
    }
}
