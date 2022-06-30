package com.example.Camerax_demo;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Context;

import java.io.File;
import java.io.IOException;


public class  MainActivity extends AppCompatActivity
{
    Button button;
    ImageView imageView;
    int REQUEST_CODE=1000;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button =findViewById(R.id.btn_pick);
        imageView =findViewById(R.id.show_img);
        if (ActivityCompat.checkSelfPermission(MainActivity .this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
       button.setOnClickListener(view -> openSystemImageChooser(REQUEST_CODE));


    }
//   从图片库中选择图片


    private void openSystemImageChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
        // 选择视频: intent.type = "video/*";
        // 选择所有类型的资源: intent.type = "*/*"
    }

    /**
     * 在返回的 onActivityResult 中接收选取返回的图片资源
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            Log.e(TAG, "ActivityResult resultCode error");
            return;
        }

        Bitmap bm;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode ==REQUEST_CODE){
            Uri originalUri = data.getData();//获得图片的uri

//                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片
            String[]filePathColumns = {MediaStore.Images.Media.DATA};
            //到图库中国查询
            @SuppressLint("Recycle") Cursor cursor=getContentResolver().query(
                    originalUri,
                    filePathColumns,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            //获取_data，所在索引
            int columnIndex =cursor.getColumnIndex(filePathColumns[0]);
            //获取图片存储路径
            File file= new File(cursor.getString(columnIndex));

            Log.e("exit", String.valueOf(file.exists()));
            Log.e("len", String.valueOf(file.length()));
            bm = BitmapFactory.decodeFile(cursor.getString(columnIndex));
            imageView.setImageBitmap(bm);
            //游标关闭
            cursor.close();
            CreateSurvey(file);
        }
    }
//    调用ClientUtil的函数
//    连接服务器
//    把图片传上去

    private void CreateSurvey(File file){
        String url="http://192.168.1.115:8000/ ";
        ClientUtil clientUtil = new ClientUtil();
        clientUtil.SendImg(url,file);
    }
    private File getNewFile() {
        File file = null;
        try {
            file = new File(context.getCacheDir(), "img.jpg");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}





