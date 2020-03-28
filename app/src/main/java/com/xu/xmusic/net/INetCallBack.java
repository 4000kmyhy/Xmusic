package com.xu.xmusic.net;

public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);
}
