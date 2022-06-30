package com.example.camerax;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

public class PoseText {
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);
        Bitmap resizedBitmap;
        PoseLandmark leftShoulder;
        PoseLandmark  rightShoulder;
        float leftShoulderX;

    public  void runPose(Bitmap bitmap){

            int rotationDegree = 0;

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            InputImage image = InputImage.fromBitmap(resizedBitmap, rotationDegree);

            poseDetector.process(image)
                    .addOnSuccessListener(
                            this::processPose)
                    .addOnFailureListener(
                            e -> {
//                                Toast.makeText(PoseTest.this, "Pose Test Edited",Toast.LENGTH_SHORT).show();
                            });
        }
        private void processPose(Pose pose) {
            try {

                // Oguz
                leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                PointF leftShoulderP = leftShoulder.getPosition();
                float leftShoulderX = leftShoulderP.x;




            } catch (Exception e) {
              Log.d("Pose", String.valueOf(leftShoulder));
//            progressBar.setVisibility(View.INVISIBLE);
            }

        }

}

