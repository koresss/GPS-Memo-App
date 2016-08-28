package com.example.orestis.android_final;

import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


public class txtnote extends ActionBarActivity{

    EditText subject,body;
    Button submit;
    Double lon,lat;
    Location loc;
    GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txtnote);

        subject = (EditText) findViewById(R.id.subject);
        body  = (EditText)findViewById(R.id.body);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAdapter db = new DBAdapter(txtnote.this);
                try {
                        db.open();
                        if(lon!=null && lat!=null) {
                            boolean subjectfound=false;
                            Cursor c = db.getAllNotes();
                            if (c.moveToFirst()) {
                                do {
                                    if(String.valueOf(subject.getText()).equals(c.getString(1))){
                                        subjectfound = true;
                                        break;
                                    }
                                } while (c.moveToNext());
                            }
                            if(!subjectfound) {
                                db.insertNote(String.valueOf(subject.getText()), String.valueOf(body.getText()), "TEXT", lon, lat);
                                db.close();
                                subject.setText("");
                                body.setText("");
                                Toast t = Toast.makeText(txtnote.this, "Note added!", Toast.LENGTH_LONG);
                                t.show();
                            }else{
                                Toast t = Toast.makeText(txtnote.this, "Υπάρχει ήδη σημείωση με αυτό το όνομα!", Toast.LENGTH_LONG);
                                t.show();
                            }
                        }else{
                            Toast t = Toast.makeText(txtnote.this, "Δεν υπάρχουν συντεταγμένες, περιμένετε ή ανοίξτε το GPS αν είναι κλειστό.", Toast.LENGTH_LONG);
                            t.show();
                        }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        map=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                loc = map.getMyLocation();
                if (loc != null) {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                }
            }
        });
    }
}
