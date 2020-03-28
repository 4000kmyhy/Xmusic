package com.xu.xmusic;

public class Constant {

    public static int[] topids = {
            3, 4, 5, 6, 16, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 64, 65, 66, 67, 70, 101, 102, 104, 105, 106, 107, 108, 113, 114, 117, 121, 122, 123, 124, 126, 127, 128, 129, 157, 158, 162, 167, 168, 169, 301, 427
    };

    public static int[] recommendTopid = {//推荐
            4, 26, 27, 427, 31, 34, 36, 60, 62, 64, 65, 158, 162, 167, 168
    };

    public static int[] topTopid = {//巅峰
            18, 19, 20, 21, 22, 23, 25, 28, 29, 30, 32, 33, 35, 50, 51, 52, 54, 55, 56
    };

    public static int[] regionTopid = {//地区
            3, 5, 6, 16, 17, 59, 61, 104, 105, 106, 107, 108, 113, 114, 117, 121, 122, 123, 126, 127, 128, 129
    };

    public static int[] otherTopid = {//其他
            53, 57, 58, 66, 67, 70, 101, 102, 124, 157, 169, 301
    };

    //QQ音乐接口
    public static String getMusicApi(int topid) {
        return "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8¬ice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid=" + topid;
    }

    //获取专辑图片
    public static String getAlbumImg(String albummid) {
        return "https://y.gtimg.cn/music/photo_new/T002R300x300M000" + albummid + ".jpg";
    }

    //获取播放地址
    public static final String musicUrl = "http://ws.stream.qqmusic.qq.com/";

    public static String getMusicKey(String songmid) {
        return "https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=" +
                "{\"req_0\":{" +
                "\"module\":\"vkey.GetVkeyServer\"," +
                "\"method\":\"CgiGetVkey\"," +
                "\"param\":{" +
                "\"guid\":\"1\"," +
                "\"songmid\":[\"" + songmid + "\"]," +
                "\"uin\":\"1\"" +
                "}}}";
    }

    //获取歌词
    public static final String qqmusicHeadName = "Referer";
    public static final String qqmusicHeadValue = "https://y.qq.com/portal/player.html";

    public static String getLyric(String songmid) {
        return "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?format=json&nobase64=1&songmid=" + songmid;
    }

    //搜索歌曲、歌手
    public static String getSearch(String name) {
        return "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n=20&format=json&w=" + name;
    }
}
