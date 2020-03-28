package com.xu.xmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.xu.toolbar.SimpleToolbar;
import com.xu.xmusic.Constant;
import com.xu.xmusic.R;
import com.xu.xmusic.base.BaseActivity;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.contract.MainContract;
import com.xu.xmusic.contract.MusicContract;
import com.xu.xmusic.fragments.MusicListFragment;
import com.xu.xmusic.fragments.MusicSearchFragment;
import com.xu.xmusic.fragments.UserFragment;
import com.xu.xmusic.presenters.MusicPresenter;
import com.xu.xmusic.utils.MusicPlayerUtils;
import com.xu.xmusic.utils.StatusBarUtils;
import com.xu.xmusic.views.MusicView;
import com.xu.xmusic.views.MyMusicPlayerView;
import com.xu.xxplayer.players.BasePlayerView;
import com.xu.xxplayer.utils.XXPlayerManager;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements MainContract.View, MusicContract.View {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    SimpleToolbar toolbar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.musicView)
    MusicView musicView;

    private MyMusicPlayerView mPlayerView;
    private MusicContract.Presenter musicPresenter;

    private long preDownTime = 0l;
    private boolean isPreDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.translucent(this);
        setSwipeBackEnable(false);

        if (getIntent().getBooleanExtra("startFromNotification", false)) {
            musicView.enterFullScreen();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("startFromNotification", false)) {
            musicView.enterFullScreen();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        musicPresenter = new MusicPresenter(this);
        initToolbar();
        initViewPager();
        initPlayer();
    }

    private void initToolbar() {
        toolbar.setPaddingTop(StatusBarUtils.getStatusBarHeight(getContext()))
                .setTitleGravity(Gravity.CENTER)
                .setLeftBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(UserFragment.newInstance());
                    }
                })
                .setRightBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFragment(MusicSearchFragment.newInstance());
                    }
                });
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            String[] listName = {"推荐榜", "巅峰榜", "地区榜", "其他榜"};
            int[][] topList = {Constant.recommendTopid, Constant.topTopid, Constant.regionTopid, Constant.otherTopid};

            @Override
            public Fragment getItem(int i) {
                return MusicListFragment.newInstance(topList[i], i == 0);
            }

            @Override
            public int getCount() {
                return listName.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return listName[position];
            }
        });

        //在viewpager setAdapter之后
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setSnapOnTabClick(true);//viewpager切换不滑动
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initPlayer() {
        if (MusicPlayerUtils.isMusicPlayer()) {
            mPlayerView = (MyMusicPlayerView) XXPlayerManager.instance().getCurrentPlayer();
            musicView.setPlayerView(mPlayerView);
            musicView.setMusicInfo(mPlayerView.getMusicInfo());
            //更改状态
            int state = mPlayerView.getCurrentState();
            mPlayerView.setOnPlayStateChanged(BasePlayerView.STATE_PLAYING);
            mPlayerView.setOnPlayStateChanged(state);
        } else {
            mPlayerView = new MyMusicPlayerView(getContext());
            musicView.setPlayerView(mPlayerView);
            musicPresenter.getHistory(getContext());
        }

        mPlayerView.setOnMusicPlayerListener(new MyMusicPlayerView.OnMusicPlayerListener() {
            @Override
            public void updateMusic(MusicInfo musicInfo) {
                playMusic(musicInfo);
            }
        });
    }

    @Override
    public void setMusicInfo(MusicInfo musicInfo) {
        musicView.setMusicInfo(musicInfo);
        musicView.prepareMusic();
    }

    @Override
    public void playMusic(MusicInfo musicInfo) {
        musicView.setMusicInfo(musicInfo);
        musicView.playMusic();
    }

    @Override
    public void startFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();//出栈
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (musicView.isFullScreen()) {
            musicView.exitFullScreen();
        } else {
            if (!getSupportFragmentManager().popBackStackImmediate()) {//判断fragment出栈
                long lastDownTime = System.currentTimeMillis();
                if (isPreDown) {
                    if (lastDownTime - preDownTime < 2000) {
                        super.onBackPressed();
                    } else {
                        preDownTime = lastDownTime;
                        showToast(getResources().getString(R.string.exit_app));
                    }
                } else {
                    preDownTime = lastDownTime;
                    isPreDown = true;
                    showToast(getResources().getString(R.string.exit_app));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.unbindService();
    }
}
