package com.example.orestis.android_final;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class viewnote extends ActionBarActivity {
    MediaPlayer mp;
    GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String type = i.getStringExtra("type");
        if(type.equals("TEXT")){
            setContentView(R.layout.textview);
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            TextView t = (TextView)findViewById(R.id.title);
            final TextView d = (TextView)findViewById(R.id.desc);
            Button delete = (Button)findViewById(R.id.delete);
            final String title = i.getStringExtra("title");
            String desc = i.getStringExtra("desc");
            Double lon = i.getDoubleExtra("lon", 0);
            Double lat = i.getDoubleExtra("lat", 0);
            t.setText(title);
            d.setText(desc);
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),(float)9.0));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBAdapter db = new DBAdapter(viewnote.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    db.deleteNote(title);
                    db.close();
                    Toast.makeText(viewnote.this,"Note deleted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        if(type.equals("SOUND")){
            setContentView(R.layout.soundview);
            final String title = i.getStringExtra("title");
            Double lon = i.getDoubleExtra("lon", 0);
            Double lat = i.getDoubleExtra("lat", 0);
            final String path = i.getStringExtra("path");
            TextView t = (TextView)findViewById(R.id.title);
            t.setText(title);
            final Spinner spinner = (Spinner)findViewById(R.id.spinner);
            final ToggleButton play = (ToggleButton)findViewById(R.id.play);
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            Button delete = (Button)findViewById(R.id.delete);
            List<String> spinnerList = new ArrayList<String>();
            File[] files = new File(path).listFiles();
            for(int j=0;j<files.length;j++){
                spinnerList.add(files[j].getName().replace(".3gpp",""));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(viewnote.this, android.R.layout.simple_spinner_item, spinnerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),(float)9.0));
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(play.isChecked()){
                        try {
                            String selecteditem = spinner.getSelectedItem().toString();
                            mp = new MediaPlayer();
                            mp.setDataSource(path+File.separator+selecteditem+".3gpp");
                            mp.prepare();
                            mp.start();

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        if(mp!=null){
                            mp.stop();
                            mp.release();
                            mp = null;
                        }
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBAdapter db = new DBAdapter(viewnote.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    db.deleteImgSoundNote(title,path);
                    db.close();
                    deleteDirectory(new File(path));
                    Toast.makeText(viewnote.this,"Note deleted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        if(type.equals("IMAGE")){
            setContentView(R.layout.imageview);
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            final String title = i.getStringExtra("title");
            Double lon = i.getDoubleExtra("lon", 0);
            Double lat = i.getDoubleExtra("lat", 0);
            final String path = i.getStringExtra("path");
            TextView t = (TextView)findViewById(R.id.title);
            Button delete = (Button)findViewById(R.id.delete);
            t.setText(title);
            Gallery g = (Gallery)findViewById(R.id.gallery);
            ImageAdapter ia = new ImageAdapter(viewnote.this,new File(path));
            g.setAdapter(ia);
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),(float)9.0));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBAdapter db = new DBAdapter(viewnote.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    db.deleteImgSoundNote(title,path);
                    db.close();
                    deleteDirectory(new File(path));
                    Toast.makeText(viewnote.this,"Note deleted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

}
