package com.xu.xmusic.bean;

import java.io.Serializable;
import java.util.List;

public class MusicListBean implements Serializable {

    private String ListName;
    private String picUrl;
    private List<MusicBean> musicList;//MusicBean也要序列化

    public MusicListBean() {
    }

    public MusicListBean(String listName, String picUrl, List<MusicBean> musicList) {
        ListName = listName;
        this.picUrl = picUrl;
        this.musicList = musicList;
    }

    public String getListName() {
        return ListName;
    }

    public void setListName(String listName) {
        ListName = listName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }
}
