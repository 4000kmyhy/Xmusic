package com.xu.xmusic.models;

import com.xu.xmusic.Constant;
import com.xu.xmusic.contract.MusicContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MusicModel implements MusicContract.Model {

    @Override
    public String getMusicUrl(String response) {
        String musicUrl = Constant.musicUrl;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject req_0 = jsonObject.getJSONObject("req_0");
            JSONObject data = req_0.getJSONObject("data");
            JSONArray midurlinfo = data.getJSONArray("midurlinfo");
            JSONObject info = midurlinfo.getJSONObject(0);
            String purl = info.optString("purl", "");
            musicUrl = musicUrl + purl;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return musicUrl;
    }

    @Override
    public String getLyric(String response) {
        String lyric = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            lyric = jsonObject.optString("lyric", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lyric;
    }
}
