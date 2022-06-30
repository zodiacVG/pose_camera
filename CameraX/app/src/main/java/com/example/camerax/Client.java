package com.example.camerax;

import static java.sql.DriverManager.println;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Client{
    HandlerThread getPostThread;
    Handler getPostHandler;


    public  void ClientUti(String filepath){

        String url=" http://10.22.2.23:5000/ ";
        File file =new File(filepath);

//        File file = new FileInputStream(result);
        getPostThread =new HandlerThread("getThread");
        getPostThread.start();
        getPostHandler =new Handler(getPostThread.getLooper()){
            @Override
            public void handleMessage(Message msg){
                String  res = (String) msg.obj;
            }
        };
        SendImg(url,file,getPostHandler);

    }


    public void SendImg(String url, File file,Handler handler){

        OkHttpClient client =new OkHttpClient();
        RequestBody requestBody =new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),MultipartBody.create(MediaType.parse("multipart/form-data"),file))
                .build();
//        RequestBody requestBody= new FormBody.Builder()
//                .add("file", "love")
//                .build();
        Request request=new Request.Builder().url(url).post(requestBody).build();
//        Log.e("time","start post");
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("time","服务器错误"+e.getMessage()+e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = Objects.requireNonNull(response.body()).string();
                Log.e("res",res);
                if(res.equals("0")){
                    Log.e("time","失败");
                }else{
                    println("onResponse: ${response.body.toString()}");
                    Log.e("res","response Noll issssssssssssssssssss");

                    Message message=Message.obtain();
                    message.obj=res;
                    handler.sendMessage(message);
                }

            }
        });
    }


}



