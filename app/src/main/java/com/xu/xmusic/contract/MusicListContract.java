package com.xu.xmusic.contract;

import android.content.Context;

import com.xu.xmusic.bean.MusicListBean;

import java.util.List;

/**
 * 契约，将Model、View、Presenter 进行约束管理
 */
public interface MusicListContract {

    interface Model {
        MusicListBean getFav(Context context);

        MusicListBean parseJson(String response);
    }

    interface View {
        void updateData(List<MusicListBean> list);

        void finshRefresh();
    }

    interface Presenter {
        void getData(int[] topid, boolean isHomePage);
    }
}
