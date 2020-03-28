package com.xu.xmusic.utils;

import com.xu.xxplayer.players.MusicPlayerView;
import com.xu.xxplayer.utils.XXPlayerManager;

public class MusicPlayerUtils {

    public static boolean isMusicPlayer() {
        return XXPlayerManager.instance().getCurrentPlayer() != null &&
                XXPlayerManager.instance().getCurrentPlayer() instanceof MusicPlayerView;
    }

    public static void restart(){
        if (isMusicPlayer()) {
            if (XXPlayerManager.instance().getCurrentPlayer().isIdle()) {
                XXPlayerManager.instance().getCurrentPlayer().start();
            } else {
                XXPlayerManager.instance().getCurrentPlayer().restart();
            }
        }
    }
}
