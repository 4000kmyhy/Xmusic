package com.xu.xmusic.net;

public interface INetManager {
    void get(String url, INetCallBack callBack);

    void get(String url, String headname, String headvalue, INetCallBack callBack);
}
