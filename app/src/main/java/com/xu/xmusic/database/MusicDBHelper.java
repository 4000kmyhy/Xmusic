package com.xu.xmusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.xu.xmusic.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

public class MusicDBHelper extends SQLiteOpenHelper {

    public static final String FAVORITE_DATABASE_NAME = "favorite_music.db";
    public static final String HISTORY_DATABASE_NAME = "history_music.db";
    public static final int DATABASE_VESION = 1;
    public static final String TABLE_NAME = "favorite_music";
    public static final String ALBUM_IMG = "albumImg";
    public static final String ALBUM_NAME = "albumname";
    public static final String SINGER = "singer";
    public static final String SONG_MID = "songmid";
    public static final String SONG_NAME = "songname";
    public static final String PAYPLAY = "pay_play";
    private SQLiteDatabase db;

    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = getWritableDatabase();
    }

    public MusicDBHelper(Context context, String db_name) {
        super(context, db_name, null, DATABASE_VESION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " +
                TABLE_NAME + "(" +
                ALBUM_IMG + " varchar(100)," +
                ALBUM_NAME + " varchar(100)," +
                SINGER + " varchar(100)," +
                SONG_MID + " varchar(100)," +
                SONG_NAME + " varchar(100)," +
                PAYPLAY + " INTEGER)";
        db.execSQL(sql);
    }

    public void insertData(MusicBean musicBean) {
        deleteData(musicBean.getSongmid());
        ContentValues values = new ContentValues();
        values.put(ALBUM_IMG, musicBean.getAlbumImg());
        values.put(ALBUM_NAME, musicBean.getAlbumname());
        values.put(SINGER, musicBean.getSinger());
        values.put(SONG_MID, musicBean.getSongmid());
        values.put(SONG_NAME, musicBean.getSongname());
        values.put(PAYPLAY, musicBean.getPayplay());
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteData(String songmid) {
        db.delete(TABLE_NAME, SONG_MID + " = ?", new String[]{songmid});
    }

    public void clearData() {
        db.execSQL("delete from " + TABLE_NAME);
    }

    public List<MusicBean> queryData() {
        List<MusicBean> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        ALBUM_IMG,
                        ALBUM_NAME,
                        SINGER,
                        SONG_MID,
                        SONG_NAME,
                        PAYPLAY
                },
                null, null, null, null, null);
        if (cursor.moveToLast()) {
            do {
                String albumImg = cursor.getString(cursor.getColumnIndex(ALBUM_IMG));
                String albumname = cursor.getString(cursor.getColumnIndex(ALBUM_NAME));
                String singerName = cursor.getString(cursor.getColumnIndex(SINGER));
                String songmid = cursor.getString(cursor.getColumnIndex(SONG_MID));
                String songname = cursor.getString(cursor.getColumnIndex(SONG_NAME));
                int payplay = cursor.getInt(cursor.getColumnIndex(PAYPLAY));
                if (list.size() >= 100) {//限制只获取最近100条
                    break;
                }
                list.add(new MusicBean(albumImg, albumname, singerName, songmid, songname, payplay));
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return list;
    }

    public int getCount() {
        int count = 0;
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            count = cursor.getCount();
        }
        cursor.close();
        return count > 100 ? 100 : count;
    }

    public boolean isFav(String songmid) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{SONG_MID},
                null, null, null, null, null);
        if (cursor.moveToLast()) {
            do {
                if (TextUtils.equals(songmid, cursor.getString(cursor.getColumnIndex(SONG_MID)))) {
                    return true;
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}