package com.example.orestis.android_final;


        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import java.sql.SQLException;


public class DBAdapter{
    static final String createNOTE =
            "create table NOTE (ID integer primary key autoincrement not null, TITLE text not null,DESCRIPTION text null,TYPE text not null,LONG real not null,LAT real not null); ";
    static final String createSoundImg =
            "create table SOUNDIMG (ID integer primary key autoincrement not null, NOTEID integer not null,PATH text not null); ";
    final Context context;
    SQLiteDatabase db;
    DatabaseHelper DBHelper;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context)
        {
            super(context, "finaldb", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(createNOTE);
                db.execSQL(createSoundImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        DBHelper.close();
    }
    public void insertNote(String title,String desc,String type,Double lon,Double lat){
        ContentValues initialValues = new ContentValues();
        initialValues.put("TITLE",title);
        initialValues.put("DESCRIPTION",desc);
        initialValues.put("TYPE",type);
        initialValues.put("LONG",lon);
        initialValues.put("LAT",lat);
        db.insert("NOTE",null,initialValues);
    }
    public void insertNoteIMGSOUND(String title,String desc,String type,Double lon,Double lat,String path){
        ContentValues initialValues = new ContentValues();
        initialValues.put("TITLE",title);
        initialValues.put("DESCRIPTION",desc);
        initialValues.put("TYPE",type);
        initialValues.put("LONG",lon);
        initialValues.put("LAT",lat);
        long id = db.insert("NOTE",null,initialValues);

        ContentValues initialValues2 = new ContentValues();
        initialValues2.put("NOTEID",id);
        initialValues2.put("PATH",path);
        db.insert("SOUNDIMG",null,initialValues2);
    }
    public Cursor getAllNotes(){
        return db.query("NOTE",new String[] {"ID","TITLE","DESCRIPTION","TYPE","LONG","LAT"},null,null,null,null,null);

    }

    public Cursor getAllSoundImgNotes(){
        return db.query("SOUNDIMG",new String[] {"ID","NOTEID","PATH"},null,null,null,null,null);
    }
    public void deleteNote(String title){
        db.delete("NOTE","TITLE='"+title+"'",null);
    }

    public void deleteImgSoundNote(String title,String path){
        db.delete("NOTE","TITLE='"+title+"'",null);
        db.delete("SOUNDIMG","PATH='"+path+"'",null);
    }
}
