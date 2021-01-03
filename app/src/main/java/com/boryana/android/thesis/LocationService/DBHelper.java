package com.boryana.android.thesis.LocationService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Boryana on 7/23/2016.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "MyDBDiplomProject.db";
    public static final String COORDINATES_TABLE_NAME = "location_coordinates";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table "
                        + COORDINATES_TABLE_NAME
                        + "(ID integer primary key, "
                        + " USER_ID text, "
                        + " COORDINATES_DATE text, "
                        + " COORDINATE_X text, "
                        + " COORDINATE_Y text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ COORDINATES_TABLE_NAME);
        onCreate(db);
    }


    //****************
    public boolean insertCoordinate  (String userId, String coordinateDate, String coordinate_x, String coordinate_y)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER_ID", userId);
        contentValues.put("COORDINATES_DATE", coordinateDate);
        contentValues.put("COORDINATE_X", coordinate_x);
        contentValues.put("COORDINATE_Y", coordinate_y);
        db.insert(COORDINATES_TABLE_NAME, null, contentValues);
        return true;
    }


    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ COORDINATES_TABLE_NAME +" where ID="+id+"", null );
        return res;
    }


    public int deleteCoordinate (int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COORDINATES_TABLE_NAME,
                "id = ? ",
                new String[] { String.valueOf(id) });
    }


    //{"rec_id":"1", "user_id":"5", "rec_date":"2016-05-12 00:00:00","coor_x":"43.2237898","coor_y":"27.91888"}
    public ArrayList<String> getAllCoordinates()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + COORDINATES_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(
                    "{\"rec_id\":\""
                            + res.getString(res.getColumnIndex("ID"))
                            + "\", \"user_id\":\""
                            + res.getString(res.getColumnIndex("USER_ID"))
                            + "\", \"rec_date\":\""
                            + res.getString(res.getColumnIndex("COORDINATES_DATE"))
                            + "\",\"coor_x\":\""
                            + res.getString(res.getColumnIndex("COORDINATE_X"))
                            + "\",\"coor_y\":\""
                            + res.getString(res.getColumnIndex("COORDINATE_Y"))
                            + "\"}"
            );
            res.moveToNext();
        }
        return array_list;
    }
}
