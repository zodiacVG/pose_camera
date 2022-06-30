package com.example.Camerax_demo;

import android.app.Activity;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class CameraXDome extends Activity {
    public static File convertBitmapToFile(Bitmap bitmap, File file) {
        try {
            // Log.e("time", "convertBitmapToFile start");
            // convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos);

            byte[] bitmapData = bos.toByteArray();
            // Log.e("time", "convertBitmapToFile");
            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        }catch (Exception e) {

        }
        // Log.e("time", "convertBitmapToFile end");
        return file;
    }
}
