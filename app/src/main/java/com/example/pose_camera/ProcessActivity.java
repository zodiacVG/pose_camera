package com.example.pose_camera;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class ProcessActivity extends AppCompatActivity {

    Button button1 ;
    Button button2 ;
    ImageView imageView;
    TextView textView;
    int REQUEST_CODE=1000;
    Bitmap bm;
    String filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       选择图片
        setContentView(R.layout.activity_process);
        button1 =findViewById(R.id.btn_select);
        imageView=findViewById(R.id.imageview);
        button2 = findViewById(R.id.btn_getBeauty);
        textView =findViewById(R.id.btn_pose);
        //这一两行代码主要是向用户请求权限
        if (ActivityCompat.checkSelfPermission(ProcessActivity .this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProcessActivity .this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        button1.setOnClickListener(view -> openSystemImageChooser(REQUEST_CODE));
        button2.setOnClickListener(view -> {
            //todo 需要设置新的client
            Client client =new Client();
            client.ClientUti(filePath);
        });


    }
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

        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode ==REQUEST_CODE) {
            try {
                Uri originalUri = data.getData();        //获得图片的uri
                Log.d("uri", String.valueOf(originalUri));
                bm = MediaStore.Images.Media. getBitmap(resolver, originalUri);        //显得到bitmap图片
                imageView.setImageBitmap(bm);

                //要查询的字段名称
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
                filePath =cursor.getString(columnIndex);
                File file =new File(filePath);
                Log.e("file", String.valueOf(file));

                //游标关闭
                cursor.close();

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}