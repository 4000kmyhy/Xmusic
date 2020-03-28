package com.xu.xmusic.contract;

import android.content.Context;

import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;

import java.util.List;

/**
 * 契约，将Model、View、Presenter 进行约束管理
 */
public interface MusicContract {

    interface Model {
        String getMusicUrl(String response);

        String getLyric(String response);
    }

    interface View {
        void setMusicInfo(MusicInfo musicInfo);
    }

    interface Presenter {
        void getData(List<MusicBean> musicList, int position);

        void getFavorite(Context context);

        void getHistory(Context context);
    }
}
