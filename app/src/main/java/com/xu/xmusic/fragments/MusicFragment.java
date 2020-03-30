package com.xu.xmusic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xu.toolbar.SimpleToolbar;
import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.adapters.MusicAdapter;
import com.xu.xmusic.base.BaseSwipeBackFragment;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.contract.MusicContract;
import com.xu.xmusic.presenters.MusicPresenter;
import com.xu.xmusic.utils.GlideUtils;
import com.xu.xmusic.utils.MusicPlayerUtils;
import com.xu.xmusic.utils.StatusBarUtils;
import com.xu.xmusic.views.MyMusicPlayerView;

import java.util.List;

import butterknife.BindView;

public class MusicFragment extends BaseSwipeBackFragment implements MusicContract.View {

    private static final String TAG = "MusicFragment";

    private MusicListBean musicListBean;

    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.simpleToolbar)
    SimpleToolbar simpleToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_nomusic)
    TextView tvNomusic;

    private List<MusicBean> musicList;
    private MusicAdapter musicAdapter;
    private int mPosition = -1;
    private MusicContract.Presenter mPresenter;

    public static MusicFragment newInstance(MusicListBean musicListBean) {
        MusicFragment fragment = new MusicFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("musicListBean", musicListBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            musicListBean = (MusicListBean) bundle.getSerializable("musicListBean");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    public void initView() {
        initToolbar();
        initList();
        initPlayer();
        mPresenter = new MusicPresenter(this);
        initEvent();
    }

    private void initToolbar() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        params.topMargin = StatusBarUtils.getStatusBarHeight(getContext());
        toolbar.setLayoutParams(params);

        GlideUtils.loadImage(getContext(), musicListBean.getPicUrl(), R.drawable.pic_launcher, ivPic);
        simpleToolbar.getTitleView().setAlpha(0);
        simpleToolbar.setTitle(musicListBean.getListName())
                .setTitleGravity(Gravity.CENTER)
                .setLeftBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.onBackPressed();
                    }
                });

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                int height = appBar.getHeight() -
                        toolbar.getHeight() -
                        StatusBarUtils.getStatusBarHeight(getContext());
                float alpha = (float) (1 - 1.0 * (height + i) / height);
                simpleToolbar.getTitleView().setAlpha(alpha);
            }
        });
    }

    private void initList() {
        musicList = musicListBean.getMusicList();
        musicAdapter = new MusicAdapter(getContext(), musicList);
        recyclerview.setAdapter(musicAdapter);

        if (musicList == null || musicList.size() == 0) {
            tvNomusic.setVisibility(View.VISIBLE);
        }
    }

    private void initPlayer() {
        if (MusicPlayerUtils.isMusicPlayer()) {
            mPosition = musicAdapter.getPositionBySongmid(MusicPlayerUtils.getSongmid());
            musicAdapter.setSelect(mPosition);

            MusicPlayerUtils.getMusicPlayer().setOnMusicPlayerListener(new MyMusicPlayerView.OnMusicPlayerListener() {
                @Override
                public void updateMusic(MusicInfo musicInfo) {
                    ((MainActivity) mActivity).playMusic(musicInfo);
                    mPosition = musicAdapter.getPositionBySongmid(musicInfo.getSongmid());
                    musicAdapter.setSelect(mPosition);
                }
            });
        }
    }

    private void initEvent() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                if (mPosition == position) {//如果当前当前音乐正在播放
                    MusicPlayerUtils.restart();
                    return;
                }
                if (musicAdapter.getPayPlay(position) == 1) {//vip音乐
                    mActivity.showToast(getResources().getString(R.string.no_vip));
                    return;
                }
                mPosition = position;
                musicAdapter.setSelect(position);
                mPresenter.getData(musicList, position);
            }
        });
    }

    @Override
    public void setMusicInfo(MusicInfo musicInfo) {
        ((MainActivity) mActivity).playMusic(musicInfo);
        initPlayer();
    }
}
