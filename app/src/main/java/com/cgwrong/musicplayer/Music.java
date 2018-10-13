package com.cgwrong.musicplayer;

import android.net.Uri;

/**
 * Created by admin on 2018/2/6.
 */

public class Music {

    private String musicName;
    private String musicSinger;
    private String musicUri;
    private String allName;
    private String size;

    public int isSelected=0;       //判断是否被选中

    public Music(String name, String singer, String allname, String uri, String size){
        musicName=name;
        musicSinger=singer;
        allName=allname;
        musicUri=uri;
        this.size=size;

    }

    public String getMusicUri() {
        return musicUri;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicSinger() {
        return musicSinger;
    }

    public String getAllName(){
        return allName;
    }

    public String getMusicUrl() {
        return musicUri;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setMusicSinger(String musicSinger) {
        this.musicSinger = musicSinger;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }
}
