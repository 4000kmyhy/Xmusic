package com.xu.xmusic.presenters;

import android.content.Context;

import com.xu.xmusic.Constant;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.contract.MusicContract;
import com.xu.xmusic.database.MusicDBHelper;
import com.xu.xmusic.models.MusicModel;
import com.xu.xmusic.net.INetCallBack;
import com.xu.xmusic.net.OkHttpUtils;

import java.util.List;

public class MusicPresenter implements MusicContract.Presenter {

    private MusicContract.View mView;
    private MusicContract.Model mModel;
    private MusicInfo musicInfo;
    private boolean isUrlFinish, isLyricFinish;

    public MusicPresenter(MusicContract.View view) {
        mView = view;
        mModel = new MusicModel();
        musicInfo = new MusicInfo();
    }

    @Override
    public void getData(List<MusicBean> musicList, int position) {
        musicInfo.setMusicList(musicList);
        musicInfo.setPosition(position);

        MusicBean musicBean = musicList.get(position);
        //专辑图片、名称
        musicInfo.setImgUrl(musicBean.getAlbumImg());
        musicInfo.setAlbumname(musicBean.getAlbumname());

        //歌曲名称、mid
        musicInfo.setSongname(musicBean.getSongname());
        musicInfo.setSongmid(musicBean.getSongmid());

        //歌手名字
        musicInfo.setSinger(musicBean.getSinger());

        //播放链接
        String url = Constant.getMusicKey(musicBean.getSongmid());
        OkHttpUtils.getInstance().getNetManager().get(url, new INetCallBack() {
            @Override
            public void success(String response) {
                musicInfo.setUrl(mModel.getMusicUrl(response));
                isUrlFinish = true;
                checkAllFinish();
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        //歌词
        String lyricUrl = Constant.getLyric(musicBean.getSongmid());
        OkHttpUtils.getInstance().getNetManager().get(lyricUrl, Constant.qqmusicHeadName, Constant.qqmusicHeadValue, new INetCallBack() {
            @Override
            public void success(String response) {
                musicInfo.setLyric(mModel.getLyric(response));
                isLyricFinish = true;
                checkAllFinish();
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void getFavorite(Context context) {
        MusicDBHelper dbHelper = new MusicDBHelper(context, MusicDBHelper.FAVORITE_DATABASE_NAME);
        List<MusicBean> musicList = dbHelper.queryData();
        if (musicList.size() > 0) {
            getData(dbHelper.queryData(), 0);
        }
    }

    @Override
    public void getHistory(Context context) {
        MusicDBHelper dbHelper = new MusicDBHelper(context, MusicDBHelper.HISTORY_DATABASE_NAME);
        List<MusicBean> musicList = dbHelper.queryData();
        if (musicList.size() > 0) {
            getData(dbHelper.queryData(), 0);
        }
    }

    private void checkAllFinish() {
        if (isUrlFinish && isLyricFinish) {
            isUrlFinish = false;
            isLyricFinish = false;

            mView.setMusicInfo(musicInfo);
        }
    }
}
