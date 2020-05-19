/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.team4.mamn01_grupp4.ui.scan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.TextureView;
import android.widget.TextView;

import se.team4.mamn01_grupp4.MainActivity;
import se.team4.mamn01_grupp4.Poi;
import se.team4.mamn01_grupp4.PoiDb;
import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.env.BorderedText;
import se.team4.mamn01_grupp4.env.Logger;
import se.team4.mamn01_grupp4.tflite.Classifier;
import se.team4.mamn01_grupp4.tflite.Classifier.Device;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ScanFragment extends CameraFragment implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  private static final float TEXT_SIZE_DIP = 10;
  private Bitmap rgbFrameBitmap = null;
  private long lastProcessingTimeMs;
  private float lastConfidence = 0;
  private String lastResultName;
  private int popupWindowValue = 97;
  private Integer sensorOrientation;
  private Classifier classifier;
  private BorderedText borderedText;
  /** Input image size of the model along x axis. */
  private int imageSizeX;
  /** Input image size of the model along y axis. */
  private int imageSizeY;

  private String countDownName;
  private boolean timerRunning = false;
  CountDownTimer confidenceTimer = new CountDownTimer(3000, 100) {

    @SuppressLint("DefaultLocale")
    public void onTick(long millisUntilFinished) {
      if (lastConfidence > popupWindowValue && countDownName == lastResultName) {
        countdownText.setText(String.format("%.1f", ((float)millisUntilFinished / 1000)));
      } else {
        countdownText.setText("");
        bottomView.setBackgroundResource(R.color.colorPrimary);
        timerRunning = false;
        this.cancel();
      }
    }

    public void onFinish() {
      timerRunning = false;
      countdownText.setText("");
      bottomView.setBackgroundResource(R.color.colorPrimary);
      showPopup(countDownName);
    }
  };


    @Override
  protected int getLayoutId() {
    return R.layout.tfe_ic_camera_connection_fragment;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    recreateClassifier(getDevice(), getNumThreads());
    if (classifier == null) {
      LOGGER.e("No classifier on preview!");
      return;
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
  }

  @Override
  protected void processImage() {
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final int cropSize = Math.min(previewWidth, previewHeight);
    Runnable processTask = new Runnable() {
      boolean stopped = false;
      @Override
      public void run() {
        if(!stopped) {
          if (classifier != null) {
            final long startTime = SystemClock.uptimeMillis();
            final List<Classifier.Recognition> results =
                    classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);


            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
            lastConfidence = results.get(0).getConfidence() * 100;
            lastResultName = results.get(0).getTitle();
            LOGGER.v("Detect: %s", results);

            if(lastConfidence > popupWindowValue && !timerRunning){
              countDownName = lastResultName;
              bottomView.setBackgroundResource(R.color.colorGreen);
              timerRunning = true;
              confidenceTimer.start();
            }

            getActivity().runOnUiThread(
                    new Runnable() {
                      @Override
                      public void run() {
                        showResultsInBottomSheet(results);
                      }
                    });

          }
          readyForNextImage();
        }
      }
    };
    runInBackground(processTask);
  }

  @Override
  protected void onInferenceConfigurationChanged() {
    if (rgbFrameBitmap == null) {
      // Defer creation until we're getting camera frames.
      return;
    }
    final Classifier.Device device = getDevice();
    final int numThreads = getNumThreads();
    runInBackground(() -> recreateClassifier(device, numThreads));
  }

  private void recreateClassifier(Device device, int numThreads) {
    if (classifier != null) {
      LOGGER.d("Closing classifier.");
      classifier.close();
      classifier = null;
    }
    try {
      classifier = Classifier.create(getActivity(), device, numThreads);
    } catch (IOException e) {
      LOGGER.e(e, "Failed to create classifier.");
    }

  }

  @Override
  public synchronized void onPause() {
    super.onPause();
    confidenceTimer.cancel();
    timerRunning = false;
    bottomView.setBackgroundResource(R.color.colorPrimary);
    countdownText.setText("");
  }

}
