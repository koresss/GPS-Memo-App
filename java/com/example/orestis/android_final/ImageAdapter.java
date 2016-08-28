package com.example.orestis.android_final;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends BaseAdapter{
    Context context;
    int itemBackground;
    public static File images[];
    String subject;
    File p;

    public ImageAdapter(Context c,String s){
        context  =c;
        subject = s;
    }
    public ImageAdapter(Context c,File path){
        context  =c;
        p = path;
    }
    public int getCount(){
        if(p!=null){
            images = p.listFiles();
        }else {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/AndroidFinal/" + "ImgNotes" + File.separator + subject);
            images = mediaStorageDir.listFiles();
        }
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv;
        if(convertView ==null){
            iv = new ImageView(context);
            iv.setImageURI(Uri.parse(images[position].getAbsolutePath()));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            if(p!=null) {
                iv.setLayoutParams(new Gallery.LayoutParams(600, 560));
            }else {
                iv.setLayoutParams(new Gallery.LayoutParams(470, 435));
            }

        }else{
            iv = (ImageView)convertView;
        }
        iv.setBackgroundResource(itemBackground);
        return iv;
    }
}
