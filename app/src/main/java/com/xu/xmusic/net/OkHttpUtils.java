package com.xu.xmusic.net;

public class OkHttpUtils {

    private static OkHttpUtils sInstance = new OkHttpUtils();

    private INetManager mNetManager = new OkHttpNetManager();

    public INetManager getNetManager() {
        return mNetManager;
    }

    public static OkHttpUtils getInstance() {
        return sInstance;
    }
}
