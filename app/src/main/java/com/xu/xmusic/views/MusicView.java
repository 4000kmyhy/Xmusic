package com.xu.xmusic.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xu.toolbar.SimpleToolbar;
import com.xu.xmusic.Constant;
import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.adapters.MusicAdapter;
import com.xu.xmusic.base.BaseActivity;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.database.MusicDBHelper;
import com.xu.xmusic.fragments.MusicSearchFragment;
import com.xu.xmusic.utils.GlideUtils;
import com.xu.xmusic.utils.ShareUtils;
import com.xu.xmusic.utils.StatusBarUtils;
import com.xu.xmusic.utils.TranslationTransformer;
import com.xu.xmusic.views.lyric.LyricView;
import com.xu.xxplayer.players.BasePlayerView;
import com.xu.xxplayer.utils.XXPlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "MusicView";
    private static final int START_PLAYING = 0;
    private static final int UPDATE_PLAYER = 1;
    private static final int UPDATE_SEEKBAR = 2;

    //normal
    private ImageView iv_music, iv_musiclist;
    private TextView tv_music;
    private PlayPauseView ppv_music;
    //fullscreen
    private ImageView iv_thumb;
    private SimpleToolbar toolbar;
    private ViewPager viewPager;
    private LinearLayout indicator;
    private TextView tv_current, tv_duration;
    private SeekBar sb_play;
    private ImageView iv_previous, iv_next, iv_mode, iv_list;
    private PlayPauseView ppv_play;
    private SwipePanel swipePanel;
    //viewpager上的自定义view
    private AlbumView albumView;
    private LyricView lyricView;
    //dialog菜单
    private Dialog dialogMenu;
    private View dialogMenuView;
    private TextView dialog_songname, dialog_singer_album, dialogMenuClose;
    private LinearLayout dialog_layout_singer, dialog_layout_album, dialog_layout_fav;
    private ImageView dialog_iv_fav, dialog_iv_volume;
    private SeekBar dialog_sb_volume;
    //dialog列表
    private Dialog dialogList;
    private View dialogListView;
    private ImageView dialog_iv_mode;
    private TextView dialog_tv_mode, dialogListClose;
    private RecyclerView dialog_rv_music;

    private MyMusicPlayerView mPlayerView;
    private MusicAdapter musicAdapter;
    private List<MusicBean> musicList;
    private int mPosition;

    private MusicDBHelper dbHelper;
    private boolean isFav = false;

    private ObjectAnimator animator;
    private boolean isFullScreen = false;

    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_PLAYING:
                    //获取视频长度，设置播放时长，最大进度条
                    long duration = mPlayerView.getDuration();
                    sb_play.setMax((int) duration);
                    tv_duration.setText(XXPlayerUtil.stringForTime(duration));
                    break;
                case UPDATE_PLAYER:
                    //更新播放时间
                    tv_current.setText(XXPlayerUtil.stringForTime(mPlayerView.getCurrentPosition()));
                    lyricView.updateTime(mPlayerView.getCurrentPosition());
                    albumView.setLyric(lyricView.getCurrentText());
                    if (mPlayerView.isPlaying()) {
                        UIHandler.sendEmptyMessageDelayed(UPDATE_PLAYER, 300);
                    }
                    break;
                case UPDATE_SEEKBAR:
                    //更新进度条
                    sb_play.setProgress((int) mPlayerView.getCurrentPosition());
                    if (mPlayerView.isPlaying()) {
                        UIHandler.sendEmptyMessageDelayed(UPDATE_SEEKBAR, 300);
                    }
                    break;
            }
        }
    };

    public MusicView(Context context) {
        this(context, null);
    }

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
        initEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.music_view, this);
        //normal
        iv_music = view.findViewById(R.id.iv_music);
        iv_musiclist = view.findViewById(R.id.iv_musiclist);
        tv_music = view.findViewById(R.id.tv_music);
        ppv_music = view.findViewById(R.id.ppv_music);
        //fullscreen
        swipePanel = view.findViewById(R.id.swipePanel);
        iv_thumb = view.findViewById(R.id.iv_thumb);
        toolbar = view.findViewById(R.id.toolbar);
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        tv_current = view.findViewById(R.id.tv_current);
        tv_duration = view.findViewById(R.id.tv_duration);
        sb_play = view.findViewById(R.id.sb_play);
        iv_previous = view.findViewById(R.id.iv_previous);
        iv_next = view.findViewById(R.id.iv_next);
        iv_mode = view.findViewById(R.id.iv_mode);
        iv_list = view.findViewById(R.id.iv_list);
        ppv_play = view.findViewById(R.id.ppv_play);

        albumView = new AlbumView(getContext());
        lyricView = new LyricView(getContext());

        swipePanel.setVisibility(GONE);
        tv_music.setSelected(true);
        animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360f);
        animator.setDuration(40000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);

        dbHelper = new MusicDBHelper(getContext(), MusicDBHelper.FAVORITE_DATABASE_NAME);

        initToolbar();
        initIndicator();
        initViewPager();
        initDialogMenu();
        initDialogList();
    }

    private void initToolbar() {
        toolbar.setPaddingTop(StatusBarUtils.getStatusBarHeight(getContext()))
                .setTitleGravity(Gravity.CENTER)
                .setLeftBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitFullScreen();
                    }
                })
                .setRightBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMenuDialog();
                    }
                });
    }

    private void initIndicator() {
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8f, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);
        lp.rightMargin = width;
        lp.leftMargin = width;
        for (int i = 0; i < 2; i++) {
            View view = new View(getContext());
            view.setId(i);
            view.setBackgroundResource(i == 0 ? R.drawable.dot_focus : R.drawable.dot_normal);
            view.setLayoutParams(lp);
            indicator.addView(view, i);
        }
    }

    private void initViewPager() {
        viewPager.setPageTransformer(true, new TranslationTransformer());
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0) {
                    container.addView(albumView);
                    return albumView;
                } else {
                    container.addView(lyricView);
                    return lyricView;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int index) {
                for (int i = 0; i < 2; i++) {
                    indicator.getChildAt(i).setBackgroundResource(index == i ? R.drawable.dot_focus : R.drawable.dot_normal);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initDialogMenu() {
        dialogMenuView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_musicmenu, this, false);
        dialog_songname = dialogMenuView.findViewById(R.id.tv_songname);
        dialog_singer_album = dialogMenuView.findViewById(R.id.tv_singer_album);
        dialog_layout_singer = dialogMenuView.findViewById(R.id.layout_singer);
        dialog_layout_album = dialogMenuView.findViewById(R.id.layout_album);
        dialog_layout_fav = dialogMenuView.findViewById(R.id.layout_fav);
        dialog_iv_fav = dialogMenuView.findViewById(R.id.iv_fav);
        dialog_iv_volume = dialogMenuView.findViewById(R.id.iv_volume);
        dialog_sb_volume = dialogMenuView.findViewById(R.id.sb_volume);
        dialogMenuClose = dialogMenuView.findViewById(R.id.tv_close);

        dialog_songname.setSelected(true);
        dialog_singer_album.setSelected(true);

        dialogMenu = new Dialog(getContext(), R.style.bottom_dialog);
        dialogMenu.setCanceledOnTouchOutside(true);

        Window window = dialogMenu.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lps = window.getAttributes();
        lps.width = WindowManager.LayoutParams.MATCH_PARENT;
        lps.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lps);
        dialogMenu.setContentView(dialogMenuView);
    }

    private void initDialogList() {
        dialogListView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_musiclist, this, false);
        dialog_iv_mode = dialogListView.findViewById(R.id.iv_mode);
        dialog_tv_mode = dialogListView.findViewById(R.id.tv_mode);
        dialog_rv_music = dialogListView.findViewById(R.id.rv_music);
        dialogListClose = dialogListView.findViewById(R.id.tv_close);

        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), musicList, R.layout.item_dialog_music);
        dialog_rv_music.setAdapter(musicAdapter);

        dialogList = new Dialog(getContext(), R.style.bottom_dialog);
        dialogList.setCanceledOnTouchOutside(true);

        Window window = dialogList.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lps = window.getAttributes();
        lps.width = WindowManager.LayoutParams.MATCH_PARENT;
        lps.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lps);
        dialogList.setContentView(dialogListView);
    }

    private void initEvent() {
        tv_music.setOnClickListener(this);
        iv_musiclist.setOnClickListener(this);
        ppv_music.setOnClickListener(this);
        ppv_play.setOnClickListener(this);
        iv_previous.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_mode.setOnClickListener(this);
        iv_list.setOnClickListener(this);

        swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {// 设置完全划开松手后的监听
            @Override
            public void onFullSwipe(int direction) {
                exitFullScreen();
                swipePanel.close(true);// 关闭
            }
        });

        sb_play.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIHandler.removeMessages(UPDATE_SEEKBAR);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (!mPlayerView.isIdle()) {
                    mPlayerView.seekTo(progress);
                    tv_current.setText(XXPlayerUtil.stringForTime(progress));
                }
                UIHandler.sendEmptyMessage(UPDATE_SEEKBAR);
            }
        });

        lyricView.setDraggable(true, new LyricView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mPlayerView.seekTo(time);
                if (!mPlayerView.isPlaying()) {
                    mPlayerView.restart();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_music:
                if (TextUtils.isEmpty(mPlayerView.getUrl())) {
                    ((BaseActivity) getContext()).showToast("选择一首音乐");
                } else {
                    enterFullScreen();
                }
                break;
            case R.id.iv_musiclist:
                if (TextUtils.isEmpty(mPlayerView.getUrl())) {
                    ((BaseActivity) getContext()).showToast("选择一首音乐");
                } else {
                    showListDialog();
                }
                break;
            case R.id.ppv_music:
                if (TextUtils.isEmpty(mPlayerView.getUrl())) {
                    ((BaseActivity) getContext()).showToast("选择一首音乐");
                    break;
                }
                if (mPlayerView.isIdle()) {
                    mPlayerView.start();
                    ppv_music.play();
                } else if (mPlayerView.isPlaying() || mPlayerView.isBufferingPlaying()) {
                    mPlayerView.pause();
                    ppv_music.pause();
                } else if (mPlayerView.isPaused() || mPlayerView.isBufferingPaused() ||
                        mPlayerView.isCompleted() || mPlayerView.isError()) {
                    mPlayerView.restart();
                    ppv_music.play();
                }
                break;
            case R.id.ppv_play:
                if (mPlayerView.isIdle()) {
                    mPlayerView.start();
                    ppv_play.play();
                } else if (mPlayerView.isPlaying() || mPlayerView.isBufferingPlaying()) {
                    mPlayerView.pause();
                    ppv_play.pause();
                } else if (mPlayerView.isPaused() || mPlayerView.isBufferingPaused() ||
                        mPlayerView.isCompleted() || mPlayerView.isError()) {
                    mPlayerView.restart();
                    ppv_play.play();
                }
                break;
            case R.id.iv_mode:
                if (mPlayerView.getPlayMode() == BasePlayerView.PLAY_MODE_LIST_LOOP) {//列表循环
                    iv_mode.setImageResource(R.drawable.ic_singleloop);
                    mPlayerView.setPlayMode(BasePlayerView.PLAY_MODE_LOOP);
                    ShareUtils.setMusicMode(getContext(), BasePlayerView.PLAY_MODE_LOOP);
                    ((BaseActivity) getContext()).showToast("切换到单曲循环");
                } else if (mPlayerView.getPlayMode() == BasePlayerView.PLAY_MODE_LOOP) {//单曲循环
                    iv_mode.setImageResource(R.drawable.ic_random);
                    mPlayerView.setPlayMode(BasePlayerView.PLAY_MODE_RANDOM);
                    ShareUtils.setMusicMode(getContext(), BasePlayerView.PLAY_MODE_RANDOM);
                    ((BaseActivity) getContext()).showToast("切换到随机播放");
                } else if (mPlayerView.getPlayMode() == BasePlayerView.PLAY_MODE_RANDOM) {//随机播放
                    iv_mode.setImageResource(R.drawable.ic_listloop);
                    mPlayerView.setPlayMode(BasePlayerView.PLAY_MODE_LIST_LOOP);
                    ShareUtils.setMusicMode(getContext(), BasePlayerView.PLAY_MODE_LIST_LOOP);
                    ((BaseActivity) getContext()).showToast("切换到顺序播放");
                }
                break;
            case R.id.iv_list:
                showListDialog();
                break;
            case R.id.iv_previous:
                mPlayerView.playNext(false);
                break;
            case R.id.iv_next:
                mPlayerView.playNext(true);
                break;
        }
    }

    private void showListDialog() {
        dialogList.show();

        switch (mPlayerView.getPlayMode()) {
            case BasePlayerView.PLAY_MODE_LIST_LOOP:
                dialog_iv_mode.setImageResource(R.drawable.ic_listloop);
                dialog_tv_mode.setText("顺序播放" + "(" + mPlayerView.getMusicInfo().getMusicList().size() + "首)");
                break;
            case BasePlayerView.PLAY_MODE_LOOP:
                dialog_iv_mode.setImageResource(R.drawable.ic_singleloop);
                dialog_tv_mode.setText("单曲循环" + "(" + mPlayerView.getMusicInfo().getMusicList().size() + "首)");
                break;
            case BasePlayerView.PLAY_MODE_RANDOM:
                dialog_iv_mode.setImageResource(R.drawable.ic_random);
                dialog_tv_mode.setText("随机播放" + "(" + mPlayerView.getMusicInfo().getMusicList().size() + "首)");
                break;
        }

        dialog_rv_music.scrollToPosition(mPosition);

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                MusicBean musicBean = musicAdapter.getItemObject(position);
                if (mPosition == position) {//如果当前当前音乐正在播放
                    if (!mPlayerView.isPlaying()) {
                        mPlayerView.restart();
                    }
                    return;
                }
                if (musicBean.getPayplay() == 1) {//vip音乐
                    ((BaseActivity) getContext()).showToast(getResources().getString(R.string.no_vip));
                    return;
                }
                musicAdapter.setSelect(position);
                mPlayerView.playNext(position);
            }
        });

        dialogListClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogList.dismiss();
            }
        });
    }

    private void showMenuDialog() {
        dialogMenu.show();

        int maxVolume = mPlayerView.getMaxVolume();
        int volume = mPlayerView.getVolume();
        dialog_sb_volume.setMax(maxVolume);
        dialog_sb_volume.setProgress(volume);
        if (volume > 0) {
            dialog_iv_volume.setImageResource(R.drawable.ic_volume);
        } else {
            dialog_iv_volume.setImageResource(R.drawable.ic_volume_mute);
        }
        dialog_sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlayerView.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog_layout_singer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenu.dismiss();
                exitFullScreen();
                ((MainActivity) getContext()).startFragment(
                        MusicSearchFragment.newInstance(mPlayerView.getMusicInfo().getSinger()));
            }
        });

        dialog_layout_album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenu.dismiss();
                exitFullScreen();
                ((MainActivity) getContext()).startFragment(
                        MusicSearchFragment.newInstance(mPlayerView.getMusicInfo().getAlbumname()));
            }
        });

        dialog_layout_fav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    dialog_iv_fav.setImageResource(R.drawable.ic_fav_normal);
                    dbHelper.deleteData(mPlayerView.getMusicInfo().getSongmid());
                    isFav = false;
                } else {
                    dialog_iv_fav.setImageResource(R.drawable.ic_fav);
                    dbHelper.insertData(mPlayerView.getMusicInfo().getMusicList().get(mPosition));
                    isFav = true;
                }
            }
        });

        dialogMenuClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenu.dismiss();
            }
        });
    }

    public void setPlayerView(MyMusicPlayerView playerView) {
        mPlayerView = playerView;

        mPlayerView.setOnPlayStateChangedListener(new BasePlayerView.OnPlayStateChangedListener() {
            @Override
            public void onPlayStateChanged(int state) {
                switch (state) {
                    case BasePlayerView.STATE_IDLE:
                        ppv_music.pause(false);
                        ppv_play.pause(false);
                        animator.end();
                        albumView.endAnim();
                        break;
                    case BasePlayerView.STATE_PREPARING:
                        ppv_music.play(false);
                        ppv_play.play(false);
                        animator.start();
                        albumView.startAnim();
                        break;
                    case BasePlayerView.STATE_PLAYING:
                        ppv_music.play(false);
                        ppv_play.play(false);
                        if (!animator.isStarted()) {
                            animator.start();
                        } else {
                            animator.resume();
                        }
                        albumView.resumeAnim();
                        UIHandler.sendEmptyMessage(START_PLAYING);
                        UIHandler.sendEmptyMessage(UPDATE_PLAYER);
                        UIHandler.sendEmptyMessage(UPDATE_SEEKBAR);
                        break;
                    case BasePlayerView.STATE_PAUSED:
                        animator.pause();
                        albumView.pauseAnim();
                        ppv_music.pause(false);
                        ppv_play.pause(false);
                        break;
                    case BasePlayerView.STATE_BUFFERING_PLAYING:
                        break;
                    case BasePlayerView.STATE_BUFFERING_PAUSED:
                        break;
                    case BasePlayerView.STATE_COMPLETED:
                    case BasePlayerView.STATE_ERROR:
                        animator.end();
                        albumView.endAnim();
                        ppv_music.pause(false);
                        ppv_play.pause(false);
                        //进度条跳到最后
                        tv_current.setText(XXPlayerUtil.stringForTime(mPlayerView.getDuration()));
                        sb_play.setProgress(sb_play.getMax());
                        break;
                }
            }
        });

        mPlayerView.setPlayMode(ShareUtils.getMusicMode(getContext()));
        switch (mPlayerView.getPlayMode()) {
            case BasePlayerView.PLAY_MODE_LIST_LOOP:
                iv_mode.setImageResource(R.drawable.ic_listloop);
                break;
            case BasePlayerView.PLAY_MODE_LOOP:
                iv_mode.setImageResource(R.drawable.ic_singleloop);
                break;
            case BasePlayerView.PLAY_MODE_RANDOM:
                iv_mode.setImageResource(R.drawable.ic_random);
                break;
        }
    }

    public void setMusicInfo(MusicInfo musicInfo) {
        mPlayerView.initMusicInfo(musicInfo);

        GlideUtils.loadImage(getContext(), musicInfo.getImgUrl(), R.drawable.pic_launcher, iv_music);
        tv_music.setText(musicInfo.getSongname() + " - " + musicInfo.getSinger());

        GlideUtils.loadImageTransform(getContext(), musicInfo.getImgUrl(), iv_thumb);
        toolbar.setTitle(musicInfo.getSongname());

        albumView.setSinger(musicInfo.getSinger());
        albumView.setAlbumImg(musicInfo.getImgUrl());
        lyricView.setLyric(musicInfo.getLyric());

        //dialogList
        musicList.clear();
        musicList.addAll(musicInfo.getMusicList());
        musicAdapter.notifyDataSetChanged();
        mPosition = musicInfo.getPosition();
        musicAdapter.setSelect(mPosition);

        //dialogMenu
        dialog_songname.setText(musicInfo.getSongname());
        dialog_singer_album.setText(musicInfo.getSinger() + "·" + musicInfo.getAlbumname());
        isFav = dbHelper.isFav(musicInfo.getSongmid());
        if (isFav) {
            dialog_iv_fav.setImageResource(R.drawable.ic_fav);
        } else {
            dialog_iv_fav.setImageResource(R.drawable.ic_fav_normal);
        }
    }

    public void playMusic() {
        String url = mPlayerView.getMusicInfo().getUrl();
        Log.d(TAG, "playMusic: " + url);
        if (TextUtils.equals(url, Constant.musicUrl)) {
            ((BaseActivity) getContext()).showToast("播放链接错误");
            mPlayerView.playNext(true);
            return;
        }
        mPlayerView.release();
        mPlayerView.setUrl(url);
        mPlayerView.start();
    }

    public void prepareMusic() {
        String url = mPlayerView.getMusicInfo().getUrl();
        Log.d(TAG, "prepareMusic: " + url);
        if (TextUtils.equals(url, Constant.musicUrl)) {
            ((BaseActivity) getContext()).showToast("播放链接错误");
            return;
        }
        mPlayerView.release();
        mPlayerView.setUrl(url);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void enterFullScreen() {
        if (isFullScreen) return;
        isFullScreen = true;

        Animation showBottom = AnimationUtils.loadAnimation(getContext(), R.anim.show_bottom);
        swipePanel.startAnimation(showBottom);
        swipePanel.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(0);

        openVolumeReceiver();
    }

    public void exitFullScreen() {
        if (!isFullScreen) return;
        isFullScreen = false;

        Animation hideBottom = AnimationUtils.loadAnimation(getContext(), R.anim.hide_bottom);
        swipePanel.startAnimation(hideBottom);
        swipePanel.setVisibility(View.GONE);

        closeVolumeReceiver();
    }

    /**
     * 开启音量监听
     */
    private void openVolumeReceiver() {
        getContext().registerReceiver(mVolumeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
    }

    /**
     * 关闭音量监听
     */
    private void closeVolumeReceiver() {
        try {
            getContext().unregisterReceiver(mVolumeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                int volume = mPlayerView.getVolume();
                dialog_sb_volume.setProgress(volume);
                if (volume > 0) {
                    dialog_iv_volume.setImageResource(R.drawable.ic_volume);
                } else {
                    dialog_iv_volume.setImageResource(R.drawable.ic_volume_mute);
                }
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator.isRunning()) {
            animator.end();
        }
        albumView.endAnim();
    }
}
