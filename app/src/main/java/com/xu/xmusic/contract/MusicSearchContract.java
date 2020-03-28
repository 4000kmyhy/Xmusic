package com.xu.xmusic.contract;

import com.xu.xmusic.bean.MusicBean;

import java.util.List;

/**
 * 契约，将Model、View、Presenter 进行约束管理
 */
public interface MusicSearchContract {

    interface Model {
        List<MusicBean> parseJson(String response);
    }

    interface View {
        void updateData(List<MusicBean> list);
    }

    interface Presenter {
        void getData(String name);
    }
}
