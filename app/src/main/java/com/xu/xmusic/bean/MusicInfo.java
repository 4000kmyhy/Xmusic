package com.xu.xmusic.bean;

import java.util.List;

public class MusicInfo {

    private List<MusicBean> musicList;
    private int position;
    private String url;
    private String songname;
    private String songmid;//音乐mid
    private String singer;
    private String albumname;
    private String imgUrl;
    private String lyric;

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "musicList=" + musicList +
                ", position=" + position +
                ", url='" + url + '\'' +
                ", songname='" + songname + '\'' +
                ", songmid='" + songmid + '\'' +
                ", singer='" + singer + '\'' +
                ", albumname='" + albumname + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", lyric='" + lyric + '\'' +
                '}';
    }
}
