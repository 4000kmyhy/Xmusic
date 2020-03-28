package com.xu.xmusic.presenters;

import com.xu.xmusic.Constant;
import com.xu.xmusic.contract.MusicSearchContract;
import com.xu.xmusic.models.MusicSearchModel;
import com.xu.xmusic.net.INetCallBack;
import com.xu.xmusic.net.OkHttpUtils;

public class MusicSearchPresenter implements MusicSearchContract.Presenter {

    private MusicSearchContract.View mView;
    private MusicSearchContract.Model mModel;

    public MusicSearchPresenter(MusicSearchContract.View view) {
        mView = view;
        mModel = new MusicSearchModel();
    }

    @Override
    public void getData(String name) {
        String url = Constant.getSearch(name);
        OkHttpUtils.getInstance().getNetManager().get(url, new INetCallBack() {
            @Override
            public void success(String response) {
                mView.updateData(mModel.parseJson(response));
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
