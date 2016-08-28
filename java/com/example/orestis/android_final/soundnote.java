package com.example.orestis.android_final;

import android.database.Cursor;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class soundnote extends ActionBarActivity {

    EditText subject;
    ToggleButton rec,play;
    Spinner spinner;
    Button submit;
    MediaRecorder mr;
    MediaPlayer mp;
    List<String> spinnerArray;
    int numOfRecs = -1;
    Double lon,lat;
    Location loc;
    GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundnote);

        subject = (EditText)findViewById(R.id.subjectsound);
        rec = (ToggleButton) findViewById(R.id.toggleButtonrec);
        play  = (ToggleButton)findViewById(R.id.toggleButtonplay);
        spinner  = (Spinner) findViewById(R.id.spinner);
        submit = (Button) findViewById(R.id.submitsound);
        spinnerArray =  new ArrayList<String>();


        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rec.isChecked()){
                    if(String.valueOf(subject.getText()).equals("")){
                        rec.setChecked(false);
                        Toast.makeText(soundnote.this,"Subject missing!",Toast.LENGTH_LONG).show();
                    }else{
                        mr = new MediaRecorder();
                        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/AndroidFinal/SoundNotes/"+String.valueOf(subject.getText()));
                        if (! mediaStorageDir.exists()){
                            if (! mediaStorageDir.mkdirs()){
                                Log.d("MyCameraApp", "failed to create director y");
                            }
                        }
                        File f = new File(Environment.getExternalStorageDirectory() + "/AndroidFinal/SoundNotes/"+String.valueOf(subject.getText()));
                        numOfRecs = f.listFiles().length;
                        mr.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidFinal/SoundNotes/"+String.valueOf(subject.getText())+"/audio"+numOfRecs+".3gpp");
                        try {
                            subject.setEnabled(false);
                            mr.prepare();
                            mr.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    mr.stop();
                    mr.release();
                    mr = null;

                    spinnerArray.add("audio"+numOfRecs);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(soundnote.this, android.R.layout.simple_spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play.isChecked()){
                    try {
                        String selecteditem = spinner.getSelectedItem().toString();
                        mp = new MediaPlayer();
                        mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidFinal/SoundNotes/"+String.valueOf(subject.getText())+"/"+selecteditem+".3gpp");
                        mp.prepare();
                        mp.start();

                    }catch(NullPointerException e){
                        Toast.makeText(soundnote.this,"Κάντε ηχογράφηση πρώτα για να παίξετε κάτι!",Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfRecs==-1){
                    Toast.makeText(soundnote.this,"Κάντε ηχογράφηση πρώτα για να αποθηκεύσετε!",Toast.LENGTH_LONG).show();
                }else{
                    DBAdapter db = new DBAdapter(soundnote.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
                    db.insertNoteIMGSOUND(String.valueOf(subject.getText()),"","SOUND",lon,lat,Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidFinal/SoundNotes/"+String.valueOf(subject.getText()));
                    db.close();
                    subject.setText("");
                            spinnerArray = new ArrayList<String>();
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(soundnote.this, android.R.layout.simple_spinner_item, spinnerArray);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                    subject.setEnabled(true);
                    numOfRecs = -1;
                            Toast t = Toast.makeText(soundnote.this, "Note added!", Toast.LENGTH_LONG);
                            t.show();
                        }else{
                            Toast t = Toast.makeText(soundnote.this, "Υπάρχει ήδη σημείωση με αυτό το όνομα!", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }else{
                        Toast t = Toast.makeText(soundnote.this, "Δεν υπάρχουν συντεταγμένες, περιμένετε ή ανοίξτε το GPS αν είναι κλειστό.", Toast.LENGTH_LONG);
                        t.show();
                    }
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
