package com.xu.xmusic.bean;

import java.io.Serializable;

public class MusicBean implements Serializable {

    private String albumImg;//专辑图片
    private String albumname;//专辑名
    private String singer;//歌手
    private String songmid;//音乐mid
    private String songname;//音乐名
    private int payplay;

    public MusicBean() {
    }

    public MusicBean(String albumImg, String albumname, String singer, String songmid, String songname, int payplay) {
        this.albumImg = albumImg;
        this.albumname = albumname;
        this.singer = singer;
        this.songmid = songmid;
        this.songname = songname;
        this.payplay = payplay;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public int getPayplay() {
        return payplay;
    }

    public void setPayplay(int payplay) {
        this.payplay = payplay;
    }

    public String getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }
}
