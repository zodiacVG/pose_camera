package com.example.pose_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.pose_camera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public void jumpToLoginPage (View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String user;
        String pswd;
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            user = bundle.getString("user");
            pswd = bundle.getString("pswd");
            System.out.print(user+pswd);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());   //dont know wtf
        setContentView(binding.getRoot());   //where is my manu?? is it mean main activity?

        String[] poseName = {"psoe1", "pose2", "pose3", "pose4"};
        int[] images = {R.drawable.pose1, R.drawable.pose2, R.drawable.pose3, R.drawable.pose4};

        MenuGridViewAdapter menuGridViewAdapter = new MenuGridViewAdapter(MainActivity.this, poseName, images);  //create an adapter
        binding.gridView.setAdapter(menuGridViewAdapter);

        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //set listner to every item, it reflex every id
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "you clicked" + poseName[position] + id, Toast.LENGTH_SHORT).show();
                System.out.print("点解了");
            }
        });
    }
}