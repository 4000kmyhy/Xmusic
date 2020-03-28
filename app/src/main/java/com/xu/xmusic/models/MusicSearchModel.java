package com.xu.xmusic.models;

import com.xu.xmusic.Constant;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.contract.MusicSearchContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicSearchModel implements MusicSearchContract.Model {

    @Override
    public List<MusicBean> parseJson(String response) {
        List<MusicBean> musicList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code", -1);
            if (code == 0) {
                JSONObject data = jsonObject.optJSONObject("data");
                JSONObject song = data.optJSONObject("song");
                JSONArray list = song.optJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject temp = list.optJSONObject(i);
                    String albumImg = Constant.getAlbumImg(temp.optString("albummid", ""));
                    String albumname = temp.optString("albumname", "");
                    JSONArray singer = temp.optJSONArray("singer");
                    String singerName = "";
                    for (int j = 0; j < singer.length(); j++) {
                        JSONObject tempSinger = singer.optJSONObject(j);
                        if (j != 0) singerName += "/";
                        singerName += tempSinger.optString("name", "");
                    }
                    String songmid = temp.optString("songmid", "");
                    String songname = temp.optString("songname", "");
                    JSONObject pay = temp.optJSONObject("pay");
                    int payplay = pay.optInt("payplay", 0);

                    musicList.add(new MusicBean(albumImg, albumname, singerName, songmid, songname, payplay));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return musicList;
    }
}
