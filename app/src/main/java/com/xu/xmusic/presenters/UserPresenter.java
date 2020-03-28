package com.xu.xmusic.presenters;

import android.content.Context;

import com.xu.xmusic.contract.UserContract;
import com.xu.xmusic.fragments.UserFragment;
import com.xu.xmusic.models.UserModel;

public class UserPresenter implements UserContract.Presenter {

    private Context context;
    private UserContract.View mView;
    private UserContract.Model mModel;

    public UserPresenter(UserContract.View view) {
        context = ((UserFragment) view).getContext();
        mView = view;
        mModel = new UserModel();
    }

    @Override
    public void getData() {
        mView.updateData(mModel.getFavorite(context));
        mView.updateData(mModel.getHistory(context));
    }
}
