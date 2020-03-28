package com.xu.xmusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VESION = 1;
    public static final String TABLE_NAME = "history";
    public static final String INPUT_STRING = "input_string";
    private SQLiteDatabase db;

    public HistoryDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = getWritableDatabase();
    }

    public HistoryDBHelper(Context context, String name) {
        super(context, name, null, DATABASE_VESION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + "(" + INPUT_STRING + " varchar(1000))";
        db.execSQL(sql);
    }

    public void insertData(String input) {
        deleteData(input);
        ContentValues values = new ContentValues();
        values.put(INPUT_STRING, input);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteData(String input) {
        db.delete(TABLE_NAME, INPUT_STRING + " = ?", new String[]{input});
    }

    public void clearData() {
        db.execSQL("delete from " + TABLE_NAME);
    }

    public List<String> queryData() {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{INPUT_STRING},
                null, null, null, null, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                String input = cursor.getString(cursor.getColumnIndex(INPUT_STRING));
                list.add(input);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
