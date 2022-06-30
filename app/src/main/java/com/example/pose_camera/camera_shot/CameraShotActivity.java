package com.example.pose_camera.camera_shot;

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.StrictMode;
        import android.util.Log;
        import android.widget.ImageView;
        import android.widget.Toast;

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

        import com.google.android.gms.common.images.Size;
        import com.google.common.util.concurrent.ListenableFuture;

        import java.io.File;
        import java.util.Date;
        import java.util.concurrent.ExecutionException;
        import java.util.concurrent.Executor;

//import androidx.camera.core.VideoCapture;
//import androidx.camera.core.impl.VideoCaptureConfig;
//import android.hardware.camera2.CameraAccessException;
//import android.widget.Button;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View;

public class CameraShotActivity extends AppCompatActivity {

    ImageView bTakePicture;
    ImageView bSelectPicture;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bTakePicture = findViewById(R.id.btn_camera_shutter);
//        bRecording = findViewById(R.id.bRecord);
        previewView = findViewById(R.id.previewView);
        bSelectPicture = findViewById(R.id.btn_camera_filter);
        Permission();
        bSelectPicture.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProcessActivity.class);
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

    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);

    }

    //    动态申请权限读写相册权限
//    以及相机调用权力
    public void Permission() {
        //安卓7.0调用相机
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,//联系人的权限
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE

            };
            //读写SD卡权限
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();
        //选择摄像头的
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

//        为获取摄像头预览中的每一帧，使用的ImageAnalysis
        imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(getExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

                final Bitmap bitmap = previewView.getBitmap();

                image.close();




            }
        });
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture,imageAnalysis);
    }

    //    拍照
    private void capturePhoto() {

        Date date =new Date();
        String timestamp =String.valueOf(date.getTime());
//       设置图像保存路径
//        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraX/";
////          查看文件夹是否存在, 如果不存在就创建一个新文件夹
//        File localFile = new File(filePath);
//        boolean isDirectoryCreated =  localFile.mkdir();
//        if (!isDirectoryCreated){
//          boolean  isDirectoryCreate =  localFile.mkdir();
//          System.out.println(isDirectoryCreate);
//        }
////        图像最终的路径和名字
//        File finalImageFile = new File(localFile, timestamp + ".jpg");
//        if (finalImageFile.exists()) {
//           boolean FileDeleted =  finalImageFile.delete();
//            System.out.println(FileDeleted );
//        }
//        try {
//           boolean isFileCreated =  finalImageFile.createNewFile();
//           System.out.println(isFileCreated);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File finalImageFile = new File(path, timestamp + ".jpg");
        Toast.makeText(MainActivity.this, "拍照开始: ", Toast.LENGTH_SHORT).show();
        Log.e("takePhoto" , String.valueOf(finalImageFile));


        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(finalImageFile).build();

        // Executor cameraExecutor = null;

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        Uri contentUri = Uri.fromFile(new File(finalImageFile.getAbsolutePath()));
//                        System.out.println("URL"+contentUri);
                        Log.e("异常信息", String.valueOf(outputFileResults));
                        Log.e("异常信息", String.valueOf(contentUri));
                        Uri savedUri = outputFileResults.getSavedUri();
                        Log.e("异常信息", String.valueOf(savedUri));
                        Toast.makeText(MainActivity.this, "保存成功: ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ProcessActivity.class);
                        intent.putExtra("image", contentUri.toString());
                        startActivity(intent);

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "保存失败" + exception.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }

        );

    }

}
