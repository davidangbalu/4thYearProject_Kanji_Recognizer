package com.davidangbalu.kanjirecognizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.davidangbalu.kanjirecognizer.model.Classifier;
import com.davidangbalu.kanjirecognizer.model.Recognition;
import com.davidangbalu.kanjirecognizer.canvas.CanvasPoint;
import com.davidangbalu.kanjirecognizer.canvas.KanjiCanvas;
import com.davidangbalu.kanjirecognizer.canvas.Result;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity{

    public KanjiCanvas canvas;
    public Classifier tflite;
    public LinearLayout resultObj;
    public HorizontalScrollView resultScrollView;
    public int resultWidth;
    public Bitmap currentEval;
    public List<List<CanvasPoint>> currentEvalStrokes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = findViewById(R.id.canvas);
        resultObj = findViewById(R.id.results);
        resultWidth = (int) getResources().getDimension(R.dimen.result_size);
        resultScrollView = findViewById(R.id.resultView);

        try {
            tflite = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearCanvas(View view) {
        canvas.clearCanvas();
        currentEval = null;
        currentEvalStrokes = null;
        if (resultObj.getChildCount() > 0) {
            resultObj.removeAllViews();
        }
    }

    public View createButtonFromResult(
            Recognition r, Bitmap image, List<List<CanvasPoint>> writingStrokes) {
        Result btn = new Result(this, null, r.title, r.percentage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resultWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        btn.setLayoutParams(layoutParams);
        return btn;
    }

    public synchronized void runClassifier(View view) {
        if (canvas == null || tflite == null || resultObj == null) {
            return;
        }
        currentEvalStrokes = canvas.wStrokes;
        currentEval = Rendering.ImageStrokes(currentEvalStrokes);
        List<Recognition> results = tflite.recognizeImage(currentEval);
        if (resultObj.getChildCount() > 0) {
            resultObj.removeAllViews();
        }
        for (Recognition result : results) {
            resultObj.addView(createButtonFromResult(
                    result, currentEval, currentEvalStrokes));
        }
        resultScrollView.scrollTo(0, 0);
    }
}



