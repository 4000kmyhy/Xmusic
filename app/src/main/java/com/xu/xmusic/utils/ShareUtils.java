package com.xu.xmusic.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.xu.xxplayer.players.BasePlayerView;

public class ShareUtils {

    /**
     * 设置音乐模式
     *
     * @param context
     * @param mode
     */
    public static void setMusicMode(Context context, int mode) {
        context.getSharedPreferences("music_mode", Context.MODE_PRIVATE)
                .edit()
                .putInt("mode", mode)
                .apply();
    }

    public static int getMusicMode(Context context) {
        return context.getSharedPreferences("music_mode", Context.MODE_PRIVATE)
                .getInt("mode", BasePlayerView.PLAY_MODE_LIST_LOOP);
    }
}
