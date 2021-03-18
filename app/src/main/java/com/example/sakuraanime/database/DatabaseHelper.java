package com.example.sakuraanime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sakuraanime.database.model.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "historys_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create Historys table
        db.execSQL(History.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + History.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertHistory(String finalUrl, String title, String barTitle, String strEpisodeList) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(History.COLUMN_FINALURL, finalUrl);
        values.put(History.COLUMN_TITLE,title );
        values.put(History.COLUMN_BARTITLE,barTitle );
        values.put(History.COLUMN_STREPISODELIST,strEpisodeList );

        // insert row
        long id = db.insert(History.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public History getHistory(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(History.TABLE_NAME,
                new String[]{History.COLUMN_ID, History.COLUMN_FINALURL, History.COLUMN_TITLE,History.COLUMN_BARTITLE,History.COLUMN_STREPISODELIST,History.COLUMN_TIMESTAMP},
                History.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if ((cursor != null)&&cursor.moveToFirst())
            cursor.moveToFirst();

        // prepare History object
        History history = new History(
                cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_FINALURL)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_BARTITLE)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_STREPISODELIST)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return history;
    }



    public List<History> getAllHistorys() {
        List<History> Historys = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + History.TABLE_NAME + " ORDER BY " +
                History.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History History = new History();
                History.setId(cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)));
                History.setFinalUrl(cursor.getString(cursor.getColumnIndex(History.COLUMN_FINALURL)));
                History.setTitle(cursor.getString(cursor.getColumnIndex(History.COLUMN_TITLE)));
                History.setBarTitle(cursor.getString(cursor.getColumnIndex(History.COLUMN_BARTITLE)));
                History.setStrEpisodeList(cursor.getString(cursor.getColumnIndex(History.COLUMN_STREPISODELIST)));
                History.setTimestamp(cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

                Historys.add(History);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return Historys list
        return Historys;
    }

    public int getHistorysCount() {
        String countQuery = "SELECT  * FROM " + History.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public History getHistoryByBarTitle(String barTitle) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(History.TABLE_NAME,
                new String[]{History.COLUMN_ID, History.COLUMN_FINALURL, History.COLUMN_TITLE,History.COLUMN_BARTITLE,History.COLUMN_STREPISODELIST,History.COLUMN_TIMESTAMP},
                History.COLUMN_BARTITLE + "=?",
                new String[]{String.valueOf(barTitle)}, null, null, null, null);

        if ((cursor != null)&&cursor.moveToFirst()){
            cursor.moveToFirst();
            // prepare History object
            History history = new History(
                    cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(History.COLUMN_FINALURL)),
                    cursor.getString(cursor.getColumnIndex(History.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(History.COLUMN_BARTITLE)),
                    cursor.getString(cursor.getColumnIndex(History.COLUMN_STREPISODELIST)),
                    cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

            // close the db connection
            cursor.close();

            return history;
        }else {
            cursor.close();
            return null;
        }



    }

    public int updateHistory(String barTitle,String finalUrl,String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(History.TABLE_NAME,
                new String[]{History.COLUMN_ID, History.COLUMN_FINALURL, History.COLUMN_TITLE,History.COLUMN_BARTITLE,History.COLUMN_STREPISODELIST,History.COLUMN_TIMESTAMP},
                History.COLUMN_BARTITLE + "=?",
                new String[]{String.valueOf(barTitle)}, null, null, null, null);

        if ((cursor != null)&&cursor.moveToFirst()){
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String newTimeStamp = fmt.format(date);

            String oldTime = cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP));
            ContentValues values = new ContentValues();
            values.put(History.COLUMN_FINALURL, finalUrl);
            values.put(History.COLUMN_TITLE,cursor.getString(cursor.getColumnIndex(History.COLUMN_TITLE)) );
            values.put(History.COLUMN_TIMESTAMP,newTimeStamp);
            values.put(History.COLUMN_TITLE,title);

            cursor.close();

            // updating row
            return db.update(History.TABLE_NAME, values, History.COLUMN_BARTITLE+ " = ?",
                    new String[]{String.valueOf(barTitle)});
        }else{
            cursor.close();
            return -1;
        }




    }

    public void deleteHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(History.TABLE_NAME, History.COLUMN_BARTITLE + " = ?",
                new String[]{String.valueOf(history.getBarTitle())});
        db.close();
    }

}
