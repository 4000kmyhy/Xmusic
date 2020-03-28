package com.xu.xmusic.models;

import android.content.Context;

import com.xu.xmusic.R;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.bean.OptionBean;
import com.xu.xmusic.contract.UserContract;
import com.xu.xmusic.database.MusicDBHelper;

import java.util.List;

public class UserModel implements UserContract.Model {

    @Override
    public OptionBean getFavorite(Context context) {
        MusicDBHelper dbHelper = new MusicDBHelper(context, MusicDBHelper.FAVORITE_DATABASE_NAME);
        List<MusicBean> musicList = dbHelper.queryData();
        String picUrl = "";
        if (musicList.size() > 0) {
            picUrl = musicList.get(0).getAlbumImg();
        }
        MusicListBean musicListBean = new MusicListBean("我喜欢", picUrl, musicList);
        return new OptionBean(R.drawable.ic_fav, "喜欢", dbHelper.getCount(), musicListBean);
    }

    @Override
    public OptionBean getHistory(Context context) {
        MusicDBHelper dbHelper = new MusicDBHelper(context, MusicDBHelper.HISTORY_DATABASE_NAME);
        List<MusicBean> musicList = dbHelper.queryData();
        String picUrl = "";
        if (musicList.size() > 0) {
            picUrl = musicList.get(0).getAlbumImg();
        }
        MusicListBean musicListBean = new MusicListBean("最近播放", picUrl, musicList);
        return new OptionBean(R.drawable.ic_history, "最近", dbHelper.getCount(), musicListBean);
    }
}
