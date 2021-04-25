package com.davidangbalu.kanjirecognizer;

import com.davidangbalu.kanjirecognizer.canvas.CanvasPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import java.util.Iterator;
import java.util.List;

public class Rendering {
    public static int ImageWidth = 64;
    public static int ImageHeight = 64;
    public static float ImageRatio = 0.125f;
    public static float StrokeRatio = 0.0625f;
    public static int BackgroundColor = Color.BLACK;
    public static int StrokeColor = Color.WHITE;

    public static Bitmap ImageStrokes(List<List<CanvasPoint>> strokes) {
        Bitmap output = Bitmap.createBitmap(ImageWidth, ImageHeight, Bitmap.Config.ARGB_8888);
        Canvas iCanvas = new Canvas(output);
        Path rPath = new Path();

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BackgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        iCanvas.drawRect(new Rect(0, 0, ImageWidth, ImageHeight), backgroundPaint);

        Paint stroke = new Paint();
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(StrokeColor);
        stroke.setStrokeWidth(ImageWidth * StrokeRatio);
        stroke.setStrokeJoin(Paint.Join.ROUND);
        stroke.setStrokeCap(Paint.Cap.ROUND);

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        Iterator<List<CanvasPoint>> strokerator;
        Iterator<CanvasPoint> pointerator;
        CanvasPoint point;
        List<CanvasPoint> strokeC;
        strokerator = strokes.iterator();

        while (strokerator.hasNext()) {
            strokeC = strokerator.next();
            pointerator = strokeC.iterator();

            while (pointerator.hasNext()) {
                point = pointerator.next();
                minX = Math.min(point.x, minX);
                maxX = Math.max(point.x, maxX);
                minY = Math.min(point.y, minY);
                maxY = Math.max(point.y, maxY);
            }
        }

        if (minX < 0) {
            minX = 0;
        }
        if (minY < 0) {
            minY = 0;
        }

        int cWidth = maxX - minX;
        int cHeight = maxY - minY;
        int offLeft = Math.round((float) ImageWidth * ImageRatio);
        int rWidth = ImageWidth - (2 * offLeft);
        int offTop = Math.round((float) ImageHeight * ImageRatio);
        int rHeight = ImageHeight - (2 * offTop);
        float scaleX = (float) rWidth / (float) cWidth;
        float scaleY = (float) rHeight / (float) cHeight;
        float scaleRatio = Math.min(scaleX, scaleY);
        int centerOffX = offLeft + Math.round((rWidth - (cWidth * scaleRatio)) / 2f);
        int centerOffY = offTop + Math.round((rHeight - (cHeight * scaleRatio)) / 2f);
        strokerator = strokes.iterator();

        boolean isFirstPoint;

        while (strokerator.hasNext()) {
            strokeC = strokerator.next();
            isFirstPoint = true;
            pointerator = strokeC.iterator();

            while (pointerator.hasNext()) {
                point = pointerator.next();

                int X = centerOffX + Math.round((point.x - minX) * scaleRatio);
                int Y = centerOffY + Math.round((point.y - minY) * scaleRatio);
                if (isFirstPoint) {
                    isFirstPoint = false;
                    rPath.moveTo(X, Y);
                } else {
                    rPath.lineTo(X, Y);
                }
            }
        }

        iCanvas.drawPath(rPath, stroke);

        return output;
    }
}