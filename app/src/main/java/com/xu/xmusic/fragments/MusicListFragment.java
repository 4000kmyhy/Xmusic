package com.xu.xmusic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.adapters.MusicListAdapter;
import com.xu.xmusic.base.BaseFragment;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.contract.MusicListContract;
import com.xu.xmusic.presenters.MusicListPresenter;

import java.util.List;

import butterknife.BindView;

public class MusicListFragment extends BaseFragment implements MusicListContract.View {

    private static final String TAG = "MusicListFragment";

    private int[] topid = {};
    private boolean isHomePage = false;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private MusicListAdapter mAdapter;
    private MusicListContract.Presenter mPresenter;

    public static MusicListFragment newInstance(int[] topid, boolean isHomePage) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("topid", topid);
        bundle.putBoolean("isHomePage", isHomePage);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            topid = (int[]) bundle.getSerializable("topid");
            isHomePage = bundle.getBoolean("isHomePage", false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music_list;
    }

    @Override
    public void initView() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        mAdapter = new MusicListAdapter(getContext());
        recyclerview.setAdapter(mAdapter);

        mPresenter = new MusicListPresenter(this);
        mPresenter.getData(topid, isHomePage);

        initEvent();
    }

    private void initEvent() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getData(topid, isHomePage);
            }
        });

        mAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                MusicListBean musicListBean = mAdapter.getItemObject(position);
                ((MainActivity) mActivity).startFragment(MusicFragment.newInstance(musicListBean));
            }
        });
    }

    @Override
    public void updateData(List<MusicListBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void finshRefresh() {
        if (isVisible()) {
            refreshLayout.finishRefresh();
        }
    }
}
