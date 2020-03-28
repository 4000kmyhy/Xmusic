package com.xu.xmusic.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.AttributeSet;

import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.contract.MusicContract;
import com.xu.xmusic.database.MusicDBHelper;
import com.xu.xmusic.presenters.MusicPresenter;
import com.xu.xmusic.services.MusicService;
import com.xu.xxplayer.players.BasePlayerView;
import com.xu.xxplayer.players.MusicPlayerView;

public class MyMusicPlayerView extends MusicPlayerView implements MusicContract.View {

    private static final String TAG = "MyMusicPlayerView";

    private MusicInfo mMusicInfo;
    private MusicContract.Presenter mPresenter;

    private MusicService.MusicBind mMusicBinder;
    private Intent mServiceIntent;
    private boolean isBindService;

    public interface OnMusicPlayerListener {
        void updateMusic(MusicInfo musicInfo);
    }

    private OnMusicPlayerListener onMusicPlayerListener;

    public void setOnMusicPlayerListener(OnMusicPlayerListener onMusicPlayerListener) {
        this.onMusicPlayerListener = onMusicPlayerListener;
    }

    public MyMusicPlayerView(Context context) {
        this(context, null);
    }

    public MyMusicPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPresenter = new MusicPresenter(this);
    }

    public void initMusicInfo(MusicInfo musicInfo) {
        mMusicInfo = musicInfo;
    }

    public MusicInfo getMusicInfo() {
        return mMusicInfo;
    }

    @Override
    public void start() {
        super.start();
        startService();

        //保存到历史数据库
        MusicDBHelper dbHelper = new MusicDBHelper(getContext(), MusicDBHelper.HISTORY_DATABASE_NAME);
        dbHelper.insertData(mMusicInfo.getMusicList().get(mMusicInfo.getPosition()));
    }

    @Override
    public void startService() {
        //管理service需要使用Application的Context
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(getContext().getApplicationContext(), MusicService.class);
            getContext().getApplicationContext().startService(mServiceIntent);
        } else {
            mMusicBinder.playMusic(MyMusicPlayerView.this);
        }

        //当前未绑定，绑定服务，同时修改绑定状态
        if (!isBindService) {
            isBindService = true;
            getContext().getApplicationContext().bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void stopService() {
        //释放音乐
        release();
        //解绑服务
        unbindService();
        //停止服务
        if (mServiceIntent != null) {
            getContext().getApplicationContext().stopService(mServiceIntent);
            mServiceIntent = null;
        }
    }

    public void unbindService() {
        if (isBindService) {
            isBindService = false;
            try {
                getContext().getApplicationContext().unbindService(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicService.MusicBind) service;
            mMusicBinder.playMusic(MyMusicPlayerView.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void mOnCompletion() {
        super.mOnCompletion();
        switch (mCurrentPlayMode) {
            case BasePlayerView.PLAY_MODE_LIST_LOOP:
                playNext(true);
                break;
            case BasePlayerView.PLAY_MODE_LOOP:
                restart();
                break;
            case BasePlayerView.PLAY_MODE_RANDOM:
                int position = (int) (Math.random() * (mMusicInfo.getMusicList().size() - 1));
                playNext(position);
                break;
        }
    }

    public void playNext(boolean isNext) {
        if (isNext) {
            if (mMusicInfo.getPosition() < mMusicInfo.getMusicList().size() - 1) {
                playNext(mMusicInfo.getPosition() + 1);
            } else {
                playNext(0);
            }
        } else {
            if (mMusicInfo.getPosition() > 0) {
                playNext(mMusicInfo.getPosition() - 1);
            } else {
                playNext(mMusicInfo.getMusicList().size() - 1);
            }
        }
    }

    public void playNext(int position) {
        mPresenter.getData(mMusicInfo.getMusicList(), position);
    }

    @Override
    public void setMusicInfo(MusicInfo musicInfo) {
        if (onMusicPlayerListener != null) {
            onMusicPlayerListener.updateMusic(musicInfo);
        }
    }
}
