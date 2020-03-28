package com.xu.xmusic.models;

import android.content.Context;

import com.xu.xmusic.Constant;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.contract.MusicListContract;
import com.xu.xmusic.database.MusicDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicListModel implements MusicListContract.Model {

    @Override
    public MusicListBean getFav(Context context) {
        MusicDBHelper dbHelper = new MusicDBHelper(context, MusicDBHelper.FAVORITE_DATABASE_NAME);
        List<MusicBean> musicList = dbHelper.queryData();
        String picUrl = "";
        if (musicList.size() > 0) {
            picUrl = musicList.get(0).getAlbumImg();
        }
        return new MusicListBean("我喜欢", picUrl, musicList);
    }

    @Override
    public MusicListBean parseJson(String response) {
        MusicListBean musicListBean = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code", -1);
            if (code == 0) {
                JSONArray result = jsonObject.optJSONArray("songlist");
                List<MusicBean> musicList = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject temp = result.optJSONObject(i);
                    JSONObject data = temp.optJSONObject("data");

                    String albummid = data.optString("albummid", "");
                    String albumImg = Constant.getAlbumImg(albummid);
                    String albumname = data.optString("albumname", "");
                    JSONArray singer = data.optJSONArray("singer");
                    String singerName = "";
                    for (int j = 0; j < singer.length(); j++) {
                        JSONObject tempSinger = singer.optJSONObject(j);
                        if (j != 0) singerName += "/";
                        singerName += tempSinger.optString("name", "");
                    }
                    String songmid = data.optString("songmid", "");
                    String songname = data.optString("songname", "");
                    JSONObject pay = data.optJSONObject("pay");
                    int payplay = pay.optInt("payplay", 0);

                    musicList.add(new MusicBean(albumImg, albumname, singerName, songmid, songname, payplay));
                }
                JSONObject topinfo = jsonObject.optJSONObject("topinfo");
                String ListName = topinfo.optString("ListName", "");
                String MacDetailPicUrl = topinfo.optString("MacDetailPicUrl", "");

                String imgUrl;
                if (musicList.size() > 0) {
                    imgUrl = musicList.get(0).getAlbumImg();
                } else {
                    imgUrl = MacDetailPicUrl;
                }
                musicListBean = new MusicListBean(ListName, imgUrl, musicList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return musicListBean;
    }
}
