package com.xu.xmusic.fragments;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xu.toolbar.SimpleToolbar;
import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.adapters.MusicListAdapter;
import com.xu.xmusic.adapters.OptionAdapter;
import com.xu.xmusic.base.BaseSwipeBackFragment;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.bean.OptionBean;
import com.xu.xmusic.contract.UserContract;
import com.xu.xmusic.presenters.UserPresenter;
import com.xu.xmusic.utils.GlideUtils;
import com.xu.xmusic.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends BaseSwipeBackFragment implements UserContract.View {

    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.iv_icon)
    CircleImageView ivIcon;
    @BindView(R.id.simpleToolbar)
    SimpleToolbar simpleToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private List<OptionBean> list;
    private OptionAdapter optionAdapter;
    private UserContract.Presenter mPresenter;

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void initView() {
        initToolbar();
        initList();
        mPresenter = new UserPresenter(this);
        mPresenter.getData();
        initEvent();
    }

    private void initToolbar() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        params.topMargin = StatusBarUtils.getStatusBarHeight(getContext());
        toolbar.setLayoutParams(params);

        GlideUtils.loadImageTransform(getContext(), R.drawable.pic_cat, ivBg);
        GlideUtils.loadImage(getContext(), R.drawable.pic_cat, ivIcon);
        simpleToolbar.getTitleView().setAlpha(0);
        simpleToolbar.setTitle("我的")
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
        list = new ArrayList<>();
        optionAdapter = new OptionAdapter(getContext(), list);
        recyclerview.setAdapter(optionAdapter);
    }

    private void initEvent() {
        optionAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                MusicListBean musicListBean = optionAdapter.getMusicListBean(position);
                ((MainActivity) mActivity).startFragment(MusicFragment.newInstance(musicListBean));
            }
        });
    }

    @Override
    public void updateData(OptionBean optionBean) {
        list.add(optionBean);
        optionAdapter.notifyDataSetChanged();
    }
}
