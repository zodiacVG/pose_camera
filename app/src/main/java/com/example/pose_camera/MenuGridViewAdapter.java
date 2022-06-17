package com.example.pose_camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuGridViewAdapter extends BaseAdapter {

    Context context;
    String[] poseName;
    int[] image;

    LayoutInflater inflater;

    public MenuGridViewAdapter(Context context, String[] poseName, int[] image) {
        this.context = context;
        this.poseName = poseName;
        this.image = image;
    }

    @Override
    public int getCount() {
        return poseName.length;  //返回pose的长度
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   //? for what?
        if(convertView == null){
            convertView = inflater.inflate(R.layout.main_menu_grid_item,null);   //import layout by me
        }
        ImageView imageView = convertView.findViewById(R.id.imageView);   //import every view written by me
        TextView textView = convertView.findViewById(R.id.textView4);
//        Button button = convertView.findViewById(R.id.button);

        imageView.setImageResource(image[position]);
        textView.setText(poseName[position]);

        return convertView;
    }
}
