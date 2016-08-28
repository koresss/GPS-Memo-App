package com.example.orestis.android_final;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.media.MediaPlayer;
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
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class imgnote extends ActionBarActivity {

    Button capture,submit;
    EditText subject;
    Camera c;
    boolean picsTaken = false;
    Gallery gallery;
    CameraSurfaceView csv;
    Double lon,lat;
    Location loc;
    GoogleMap map;
    ImageAdapter ia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgnote);

        capture = (Button)findViewById(R.id.capture);
        submit = (Button)findViewById(R.id.submitimg);
        gallery = (Gallery)findViewById(R.id.gallery);
        subject = (EditText)findViewById(R.id.subjectimg);
        try{
            c=Camera.open();
        }catch (Exception e){
            e.printStackTrace();
        }
        csv =new CameraSurfaceView(this,c);
        FrameLayout fl = (FrameLayout) findViewById(R.id.camera);
        fl.addView(csv);

       capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!String.valueOf(subject.getText()).equals("")) {
                    boolean subjectfound = false;
                    if(!picsTaken) {
                        DBAdapter db = new DBAdapter(imgnote.this);
                        try {
                            db.open();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        subjectfound = false;
                        Cursor cu = db.getAllNotes();
                        if (cu.moveToFirst()) {
                            do {
                                if (String.valueOf(subject.getText()).equals(cu.getString(1))) {
                                    subjectfound = true;
                                    break;
                                }
                            } while (cu.moveToNext());
                        }
                        db.close();
                    }
                    if(!subjectfound) {
                        c.takePicture(null, null, mPicture);
                        picsTaken = true;
                        subject.setEnabled(false);
                    }else{
                        Toast t = Toast.makeText(imgnote.this, "Υπάρχει ήδη σημείωση με αυτό το όνομα!", Toast.LENGTH_LONG);
                        t.show();
                    }
                }else{
                    Toast t = Toast.makeText(imgnote.this,"Enter subject!",Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!picsTaken){
                    Toast.makeText(imgnote.this,"Τραβήξτε φωτογραφία πρώτα για να αποθηκεύσετε!",Toast.LENGTH_LONG).show();
                }else{
                    DBAdapter db = new DBAdapter(imgnote.this);
                    try {
                        db.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if(lon!=null && lat!=null) {
                            db.insertNoteIMGSOUND(String.valueOf(subject.getText()),"","IMAGE",lon,lat,Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidFinal/ImgNotes/"+String.valueOf(subject.getText()));
                            db.close();
                            subject.setText("");
                            subject.setEnabled(true);
                            picsTaken=false;
                            gallery.setVisibility(View.INVISIBLE);
                            Toast t = Toast.makeText(imgnote.this, "Note added!", Toast.LENGTH_LONG);
                            t.show();
                    }else{
                        Toast t = Toast.makeText(imgnote.this, "Δεν υπάρχουν συντεταγμένες, περιμένετε ή ανοίξτε το GPS αν είναι κλειστό.", Toast.LENGTH_LONG);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.release();
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/AndroidFinal/"+"ImgNotes"+File.separator+subject.getText());
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("MyCameraApp", "failed to create director y");
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath()
                    + File.separator +"IMG_"+ timeStamp + ".jpg");
            if ( mediaFile == null){
                Log.d("Error", "Error creating media file, check storage permissions: " );
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream( mediaFile );
                fos.write(data);
                fos.close();


                String photopath = mediaFile.getAbsolutePath();
                Bitmap bmp = BitmapFactory.decodeFile(photopath);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(mediaFile);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    c.stopPreview();
                    c.startPreview();
                    ia = new ImageAdapter(imgnote.this,String.valueOf(subject.getText()));
                    gallery.setAdapter(ia);
                    gallery.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                Log.d("Error", " File not found:" + e.getMessage());
            } catch (IOException e) {
                Log.d("Error", "Error accessing file: " + e.getMessage());
            }
        }
    };
}
