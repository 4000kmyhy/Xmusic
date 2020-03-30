package com.xu.xmusic.utils;

import com.xu.xmusic.views.MyMusicPlayerView;
import com.xu.xxplayer.players.MusicPlayerView;
import com.xu.xxplayer.utils.XXPlayerManager;

public class MusicPlayerUtils {

    public static boolean isMusicPlayer() {
        return XXPlayerManager.instance().getCurrentPlayer() != null &&
                XXPlayerManager.instance().getCurrentPlayer() instanceof MusicPlayerView;
    }

    public static MyMusicPlayerView getMusicPlayer() {
        if (isMusicPlayer()) {
            return (MyMusicPlayerView) XXPlayerManager.instance().getCurrentPlayer();
        }
        return null;
    }

    public static void restart() {
        if (isMusicPlayer()) {
            if (getMusicPlayer().isIdle()) {
                getMusicPlayer().start();
            } else {
                getMusicPlayer().restart();
            }
        }
    }

    public static String getSongmid() {
        String songmid = "";
        if (isMusicPlayer()) {
            if (getMusicPlayer().getMusicInfo() != null) {
                songmid = getMusicPlayer().getMusicInfo().getSongmid();
            }
        }
        return songmid;
    }
}
