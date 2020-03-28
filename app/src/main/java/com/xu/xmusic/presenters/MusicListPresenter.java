package com.xu.xmusic.presenters;

import com.xu.xmusic.Constant;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.contract.MusicListContract;
import com.xu.xmusic.fragments.MusicListFragment;
import com.xu.xmusic.models.MusicListModel;
import com.xu.xmusic.net.INetCallBack;
import com.xu.xmusic.net.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicListPresenter implements MusicListContract.Presenter {

    private MusicListContract.View mView;
    private MusicListContract.Model mModel;
    private List<MusicListBean> list;

    public MusicListPresenter(MusicListContract.View view) {
        mView = view;
        mModel = new MusicListModel();
    }

    @Override
    public void getData(int[] topid, boolean isHomePage) {
        list = new ArrayList<>();
        if (isHomePage) {
            list.add(mModel.getFav(((MusicListFragment) mView).getContext()));
            mView.updateData(list);
        }
        for (int i = 0; i < topid.length; i++) {
            String url = Constant.getMusicApi(topid[i]);
            OkHttpUtils.getInstance().getNetManager().get(url, new INetCallBack() {
                @Override
                public void success(String response) {
                    list.add(mModel.parseJson(response));
                    mView.updateData(list);
                    mView.finshRefresh();
                }

                @Override
                public void failed(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }
}
