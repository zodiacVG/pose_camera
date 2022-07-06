package com.example.pose_camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClientUtil {
    private Handler handler;


    public void SendImgFile(String url, File file, Handler mhandler) {
        this.handler = mhandler;

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).build();
        Log.e("time", "start post");
        Call call = client.newCall(request);
        call.enqueue(new TheCallBack());
    }

    public void SendImgString(String url, String base64, Handler mhandler) {
        this.handler = mhandler;

        OkHttpClient client = new OkHttpClient();
        RequestBody stringBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("base64",base64)
                .build();
//        RequestBody.Companion.create("body参数", mediaType);
        Request request = new Request.Builder().url(url).post(stringBody).build();

        Call call = client.newCall(request);
        call.enqueue(new TheCallBack());
    }

    public class TheCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("time",
                    "服务器错误" + e.getMessage() + e.toString());
        }

        @Override
        public void onResponse(@NonNull Call call, final Response response) throws IOException {

            final String res = response.body().string();


            if ("0".equals(res.trim())) {
                Log.e("time", "无输出");
            } else {
                //Log.e("time", "成功");
                Log.e("time", res);
                Message message =new Message();
                message.what=0;
                message.obj=res;
                handler.sendMessage(message);
            }
        }
    }
}


