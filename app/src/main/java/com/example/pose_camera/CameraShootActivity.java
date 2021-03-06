package com.example.pose_camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraShootActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ImageView bTakePicture;
    ImageView bSelectPicture;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    private int countAnalysis = 0;
    private boolean isJudge = false;
    private Switch swh_Judge;

    public Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String str = (String) msg.obj;
                Toast.makeText(CameraShootActivity.this, str, Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_shoot);

        bTakePicture = findViewById(R.id.btn_camera_shutter);
//        bRecording = findViewById(R.id.bRecord);
        previewView = findViewById(R.id.previewView);
        bSelectPicture = findViewById(R.id.btn_camera_filter);
        swh_Judge = findViewById(R.id.swh_Judge);
        isJudge = swh_Judge.isChecked();
        Permission();
        bSelectPicture.setOnClickListener(view -> {
            Intent intent = new Intent(CameraShootActivity.this, ProcessActivity.class);
            startActivity(intent);
        });
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }, getExecutor());
        bTakePicture.setOnClickListener(view -> capturePhoto());
        swh_Judge.setOnCheckedChangeListener(this);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);

    }

    //    ????????????????????????????????????
//    ????????????????????????
    public void Permission() {
        //??????7.0????????????
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //??????????????????
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,//??????????????????
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE

            };
            //??????SD?????????
            //????????????????????????
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();
        //??????????????????
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

//        ???????????????????????????????????????????????????ImageAnalysis
        //                .setTargetResolution(new Size(1280,720))
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(getExecutor(), image -> {

            final Bitmap bitmap = previewView.getBitmap();
            String base64 = ImageUtil.imageToBase64(bitmap);
            if (isJudge) {
                if (countAnalysis % 10 == 0) {
                    CreateSurvey(base64);
                }
                countAnalysis++;
            }
            image.close();


        });
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    private void CreateSurvey(String base64) {
        String url = "http://192.168.3.226:8000/Iscomplete ";
        ClientUtil clientUtil = new ClientUtil();
        clientUtil.SendImgString(url, base64, mHandler);
    }

    //    ??????
    private void capturePhoto() {

        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Toast.makeText(CameraShootActivity.this, "????????????: ", Toast.LENGTH_SHORT).show();


        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        // Executor cameraExecutor = null;

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        Uri savedUri = outputFileResults.getSavedUri();
                        Log.e("????????????", String.valueOf(savedUri));
                        Toast.makeText(CameraShootActivity.this, "????????????: ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraShootActivity.this, "????????????" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

        );

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.swh_Judge) {
            countAnalysis = 0;
            isJudge = compoundButton.isChecked();
            if (isJudge) {
                Toast.makeText(CameraShootActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CameraShootActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
}