package com.xu.xmusic.contract;

import android.content.Context;

import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.bean.OptionBean;

import java.util.List;

/**
 * 契约，将Model、View、Presenter 进行约束管理
 */
public interface UserContract {

    interface Model {
        OptionBean getFavorite(Context context);

        OptionBean getHistory(Context context);
    }

    interface View {
        void updateData(OptionBean optionBean);
    }

    interface Presenter {
        void getData();
    }
}
