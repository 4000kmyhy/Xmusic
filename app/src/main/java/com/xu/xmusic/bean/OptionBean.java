package com.xu.xmusic.bean;

public class OptionBean {

    private int icon;
    private String name;
    private int count;
    private MusicListBean musicListBean;

    public OptionBean() {
    }

    public OptionBean(int icon, String name, int count, MusicListBean musicListBean) {
        this.icon = icon;
        this.name = name;
        this.count = count;
        this.musicListBean = musicListBean;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public MusicListBean getMusicListBean() {
        return musicListBean;
    }

    public void setMusicListBean(MusicListBean musicListBean) {
        this.musicListBean = musicListBean;
    }
}
