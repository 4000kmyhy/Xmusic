package com.xu.xmusic.contract;

import android.support.v4.app.Fragment;

import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.bean.MusicListBean;

/**
 * 契约，将Model、View、Presenter 进行约束管理
 */
public interface MainContract {

    interface Model {

    }

    interface View {
        void playMusic(MusicInfo musicInfo);

        void startFragment(Fragment fragment);
    }

    interface Presenter {

    }
}
