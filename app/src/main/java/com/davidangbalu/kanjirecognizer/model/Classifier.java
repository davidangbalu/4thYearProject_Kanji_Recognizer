package com.davidangbalu.kanjirecognizer.model;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


    public class Classifier {
        public static final int MAXRESULTS = 7;
        public static final int BATCHSIZE = 1;
        public static final int PIXELSIZE = 1;
        public static final String LOGTAG = "Classifier";
        public static final int IMAGEWIDTH = 64;
        public static final int IMAGEHEIGHT = 64;
        public static final int BYTES = 4;
        public static final String MODELPATH = "model.tflite";
        public static final String LABELPATH = "labels.txt";
        public int[] intValues = new int[IMAGEWIDTH * IMAGEHEIGHT];
        public final int NUMLABELS;
        public MappedByteBuffer tfliteModel;
        public List<String> labels;
        public Interpreter tflite;
        public ByteBuffer imgData;
        public float[][] labelArray;

        public Classifier(Activity activity) throws IOException {
            tfliteModel = loadModelFile(activity);
            tflite = new Interpreter(tfliteModel);
            labels = loadLabels(activity);
            NUMLABELS = labels.size();
            imgData = ByteBuffer.allocateDirect(BATCHSIZE * IMAGEHEIGHT * IMAGEWIDTH * PIXELSIZE * BYTES);
            imgData.order(ByteOrder.nativeOrder());
            labelArray = new float[BATCHSIZE][NUMLABELS];
            Log.d(LOGTAG, "Classifier Loaded.");
        }

        public MappedByteBuffer loadModelFile(Activity activity) throws IOException {
            AssetFileDescriptor afd = activity.getAssets().openFd(MODELPATH);
            FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
            FileChannel fileChannel = fis.getChannel();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, afd.getStartOffset(), afd.getDeclaredLength());
        }

        public float nPixelValue(int pValue) {
            int red = (pValue >> 16) & 0xFF;
            int green = (pValue >> 8) & 0xFF;
            int blue = pValue & 0xFF;
            float gray = (0.299f * red + 0.597f * green + 0.114f * blue) / 255f;
            return gray;
        }

        public List<String> loadLabels(Activity activity) throws IOException {
            ArrayList<String> labels = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(LABELPATH)));
            String line = reader.readLine();
            while (line != null) {
                labels.add(line);
                line = reader.readLine();
            }
            return labels;
        }

        public void populateBuffer(Bitmap bitmap) throws Exception {
            if ((bitmap.getWidth() != IMAGEWIDTH) && (bitmap.getHeight() != IMAGEHEIGHT)) {
                throw new Exception(String.format("The image with shape " +  bitmap.getWidth(), bitmap.getHeight() + "is not equals " + IMAGEWIDTH, IMAGEHEIGHT));
            }
            imgData.rewind();
            bitmap.getPixels(intValues, 0, IMAGEWIDTH, 0, 0, IMAGEWIDTH, IMAGEHEIGHT);
            int index = 0;
            for (int i = 0; i < IMAGEWIDTH; i++) {
                for (int j = 0; j < IMAGEHEIGHT; j++) {
                    int pValue = intValues[index++];
                    imgData.putFloat(nPixelValue(pValue));
                }
            }
        }

        public synchronized List<Recognition> recognizeImage(Bitmap bitmap) {
            ArrayList<Recognition> results = new ArrayList<>();
            try {
                populateBuffer(bitmap);
            } catch (Exception ex) {
                Log.e(LOGTAG, "Something is wrong with the Bitmap");
                ex.printStackTrace();
                return results;
            }
            tflite.run(imgData, labelArray);
            long time = System.currentTimeMillis();
            PriorityQueue<Recognition> pq =
                    new PriorityQueue<>(
                            NUMLABELS,
                            new Comparator<Recognition>() {
                                @Override
                                public int compare(Recognition a, Recognition b) {
                                    return Float.compare(b.percentage, a.percentage);
                                }
                            });
            for (int i = 0; i < NUMLABELS; i++) {
                String Str = labels.get(i);
                for (int j = 0; j < Str.length(); j++) {
                    String label = Str.substring(j, j + 1);
                    pq.add(new Recognition(i, time, label, labelArray[0][i]));
                }
            }
            int returnSize = Math.min(pq.size(), MAXRESULTS);
            for (int i = 0; i < returnSize; i++) {
                results.add(pq.poll());
            }
            return results;
        }
    }

