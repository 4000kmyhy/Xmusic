package com.xu.xmusic.views.lyric;

import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricUtils {

    private static final String TAG = "LyricView";

    private static final Pattern PATTERN_LINE = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}\\])+)(.+)");
    private static final Pattern PATTERN_TIME = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})\\]");

    public static List<LyricBean> parseLyric(String lyric) {
        List<LyricBean> lyricList = new ArrayList<>();

        String[] array = lyric.split("\\n");
        for (String line : array) {
            List<LyricBean> list = parseLine(line);
            if (list != null && !list.isEmpty()) {
                lyricList.addAll(list);
            }
        }

        return lyricList;
    }

    private static List<LyricBean> parseLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        line = line.trim();
        //[00:06.51]可惜我们终于来到 一个句号
        Matcher lineMatcher = PATTERN_LINE.matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);

        List<LyricBean> list = new ArrayList<>();
        //[00:06.51]
        Matcher timeMatcher = PATTERN_TIME.matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            String milString = timeMatcher.group(3);
            long mil = Long.parseLong(milString);
            // 如果毫秒是两位数，需要乘以10
            if (milString.length() == 2) {
                mil = mil * 10;
            }
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil;
            list.add(new LyricBean(time, text));
            Log.d(TAG, "parseLine: " + time + "  " + text);
        }
        return list;
    }

    public static void resetDurationScale() {
        try {
            Field mField = ValueAnimator.class.getDeclaredField("sDurationScale");
            mField.setAccessible(true);
            mField.setFloat(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
