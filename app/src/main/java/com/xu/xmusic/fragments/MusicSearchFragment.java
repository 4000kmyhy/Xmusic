package com.xu.xmusic.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xu.toolbar.SearchToolbar;
import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.adapters.HistoryAdapter;
import com.xu.xmusic.adapters.MusicAdapter;
import com.xu.xmusic.base.BaseSwipeBackFragment;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.contract.MusicContract;
import com.xu.xmusic.contract.MusicSearchContract;
import com.xu.xmusic.database.HistoryDBHelper;
import com.xu.xmusic.presenters.MusicPresenter;
import com.xu.xmusic.presenters.MusicSearchPresenter;
import com.xu.xmusic.utils.MusicPlayerUtils;
import com.xu.xmusic.utils.StatusBarUtils;
import com.xu.xmusic.views.MyMusicPlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MusicSearchFragment extends BaseSwipeBackFragment implements MusicSearchContract.View, MusicContract.View {

    private static final String TAG = "MusicSearchFragment";

    @BindView(R.id.toolbar)
    SearchToolbar toolbar;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.layout_history)
    FrameLayout layoutHistory;
    @BindView(R.id.rv_music)
    RecyclerView rvMusic;

    private HistoryAdapter historyAdapter;
    private MusicAdapter musicAdapter;
    private List<MusicBean> musicList;
    private HistoryDBHelper dbHelper;
    private MusicSearchContract.Presenter mPresenter;
    private MusicContract.Presenter musicPresenter;
    private int mPosition = -1;
    private String searchName;

    public static MusicSearchFragment newInstance() {
        MusicSearchFragment fragment = new MusicSearchFragment();
        return fragment;
    }

    public static MusicSearchFragment newInstance(String name) {
        MusicSearchFragment fragment = new MusicSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            searchName = bundle.getString("name");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music_search;
    }

    @Override
    public void initView() {
        initToolbar();
        initList();
        mPresenter = new MusicSearchPresenter(this);
        musicPresenter = new MusicPresenter(this);
        initEvent();

        if (!TextUtils.isEmpty(searchName)) {
            toolbar.setText(searchName);
            searchMusic();
        }
    }

    private void initToolbar() {
        toolbar.setPaddingTop(StatusBarUtils.getStatusBarHeight(getContext()))
                .setLeftBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.onBackPressed();
                    }
                })
                .setRightBtnOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(toolbar.getText())) {
                            mActivity.showToast("请输入音乐、歌手、专辑");
                        } else {
                            searchMusic();
                        }
                    }
                })
                .setEditorSearchListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            if (TextUtils.isEmpty(toolbar.getText())) {
                                mActivity.showToast("请输入音乐、歌手、专辑");
                            } else {
                                searchMusic();
                            }
                        }
                        return false;
                    }
                })
                .setOnSearchListener(new SearchToolbar.OnSearchListener() {
                    @Override
                    public void afterTextChanged(String s) {
                        if (TextUtils.isEmpty(s)) {
                            layoutHistory.setVisibility(View.VISIBLE);
                            rvMusic.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initList() {
        historyAdapter = new HistoryAdapter(getContext());
        rvHistory.setAdapter(historyAdapter);
        dbHelper = new HistoryDBHelper(getContext(), "music_history.db");
        historyAdapter.setList(dbHelper.queryData());

        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), musicList);
        rvMusic.setAdapter(musicAdapter);
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
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearData();
                historyAdapter.setList(dbHelper.queryData());
            }
        });

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                toolbar.setText(historyAdapter.getItem(position));
                searchMusic();
            }

            @Override
            public void onItemDel(int position) {
                dbHelper.deleteData(historyAdapter.getItem(position));
                historyAdapter.setList(dbHelper.queryData());
            }
        });

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
                musicPresenter.getData(musicList, position);
            }
        });
    }

    private void searchMusic() {
        musicList.clear();
        musicAdapter.notifyDataSetChanged();
        mPresenter.getData(toolbar.getText());

        dbHelper.insertData(toolbar.getText());
        historyAdapter.setList(dbHelper.queryData());

        layoutHistory.setVisibility(View.GONE);
        rvMusic.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateData(List<MusicBean> list) {
        musicList.addAll(list);
        musicAdapter.notifyDataSetChanged();
        initPlayer();
    }

    @Override
    public void setMusicInfo(MusicInfo musicInfo) {
        ((MainActivity) mActivity).playMusic(musicInfo);
        initPlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭软键盘
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        View view = mActivity.getWindow().peekDecorView();
        if (null != view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
