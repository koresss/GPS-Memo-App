package com.example.orestis.android_final;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends ActionBarActivity {

    Button txtnote,imgnote,soundnote;
    ListView lv;
    ArrayList<String> notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtnote = (Button) findViewById(R.id.txtnotebutton);
        imgnote = (Button) findViewById(R.id.imgnotebutton);
        lv = (ListView) findViewById(R.id.listView);
        soundnote = (Button) findViewById(R.id.soundnotebutton);

        txtnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, txtnote.class);
                startActivity(i);
            }
        });
        imgnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, imgnote.class);
                startActivity(i);
            }
        });
        soundnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, soundnote.class);
                startActivity(i);
            }
        });

        if(getDatabasePath("finaldb").exists()) {

            DBAdapter db = new DBAdapter(this);
            try {
                db.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Cursor c = db.getAllNotes();
            notes = new ArrayList<String>();
            if (c.moveToFirst()) {
                do {
                    notes.add(c.getString(1));
                } while (c.moveToNext());
            }
            db.close();
            Collections.reverse(notes);
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notes);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedNote = notes.get(position);
                    DBAdapter db = new DBAdapter(MainActivity.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Cursor c = db.getAllNotes();
                    notes = new ArrayList<String>();
                    if (c.moveToFirst()) {
                        do {
                            if(c.getString(1).equals(selectedNote)){
                                if(c.getString(3).equals("IMAGE") || c.getString(3).equals("SOUND")){
                                    String type = c.getString(3);
                                    Double lon = Double.parseDouble(c.getString(4));
                                    Double lat = Double.parseDouble(c.getString(5));
                                    String path = null;
                                    Cursor c2 = db.getAllSoundImgNotes();
                                    if(c2.moveToFirst()){
                        in:             do{
                                           if(c.getString(0).equals(c2.getString(1))){
                                               path = c2.getString(2);
                                               break in;
                                           }
                                        }while(c2.moveToNext());
                                    }
                                    Intent i = new Intent(MainActivity.this,viewnote.class);
                                    i.putExtra("title",selectedNote);
                                    i.putExtra("type",type);
                                    i.putExtra("lon",lon);
                                    i.putExtra("lat",lat);
                                    i.putExtra("path",path);
                                    startActivity(i);
                                    break;
                                }else if(c.getString(3).equals("TEXT")){
                                    String description = c.getString(2);
                                    String type = c.getString(3);
                                    Double lon = Double.parseDouble(c.getString(4));
                                    Double lat = Double.parseDouble(c.getString(5));
                                    Intent i = new Intent(MainActivity.this,viewnote.class);
                                    i.putExtra("title",selectedNote);
                                    i.putExtra("type",type);
                                    i.putExtra("lon",lon);
                                    i.putExtra("lat",lat);
                                    i.putExtra("desc",description);
                                    startActivity(i);
                                    break;
                                }
                            }
                        } while (c.moveToNext());
                    }
                    db.close();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getDatabasePath("finaldb").exists()) {

            DBAdapter db = new DBAdapter(this);
            try {
                db.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Cursor c = db.getAllNotes();
            notes = new ArrayList<String>();
            if (c.moveToFirst()) {
                do {
                    notes.add(c.getString(1));
                } while (c.moveToNext());
            }
            db.close();
            Collections.reverse(notes);
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notes);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedNote = notes.get(position);
                    DBAdapter db = new DBAdapter(MainActivity.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Cursor c = db.getAllNotes();
                    notes = new ArrayList<String>();
                    if (c.moveToFirst()) {
                        do {
                            if(c.getString(1).equals(selectedNote)){
                                if(c.getString(3).equals("IMAGE") || c.getString(3).equals("SOUND")){
                                    String type = c.getString(3);
                                    Double lon = Double.parseDouble(c.getString(4));
                                    Double lat = Double.parseDouble(c.getString(5));
                                    String path = null;
                                    Cursor c2 = db.getAllSoundImgNotes();
                                    if(c2.moveToFirst()){
                                        in:             do{
                                            if(c.getString(0).equals(c2.getString(1))){
                                                path = c2.getString(2);
                                                break in;
                                            }
                                        }while(c2.moveToNext());
                                    }
                                    Intent i = new Intent(MainActivity.this,viewnote.class);
                                    i.putExtra("title",selectedNote);
                                    i.putExtra("type",type);
                                    i.putExtra("lon",lon);
                                    i.putExtra("lat",lat);
                                    i.putExtra("path",path);
                                    startActivity(i);
                                    break;
                                }else if(c.getString(3).equals("TEXT")){
                                    String description = c.getString(2);
                                    String type = c.getString(3);
                                    Double lon = Double.parseDouble(c.getString(4));
                                    Double lat = Double.parseDouble(c.getString(5));
                                    Intent i = new Intent(MainActivity.this,viewnote.class);
                                    i.putExtra("title",selectedNote);
                                    i.putExtra("type",type);
                                    i.putExtra("lon",lon);
                                    i.putExtra("lat",lat);
                                    i.putExtra("desc",description);
                                    startActivity(i);
                                    break;
                                }
                            }
                        } while (c.moveToNext());
                    }
                    db.close();
                }
            });
        }
    }
}

