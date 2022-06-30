package com.example.Camerax_demo;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

    public class ClientUtil {


        public void SendImg(String url, File file) {

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

        public static class TheCallBack implements Callback {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("time",
                        "服务器错误" + e.getMessage() + e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();

                if ("0".equals(res.trim())) {
                    Log.e("time", "无输出");
                } else {
                    //Log.e("time", "成功");
                    Log.e("time", res);
                }
            }
        }
    }


